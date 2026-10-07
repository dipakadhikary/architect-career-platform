"""Conversation lifecycle. The language-model call is outside the database transaction."""

from __future__ import annotations

import time
from dataclasses import dataclass

from app.infrastructure.observability.otel import get_tracer
from app.intelligence.assistant.models import (
    CallerContext,
    ChatMessage,
    ChatRequest,
    ChatRole,
)
from app.orchestration.assistant.service import AssistantService
from app.orchestration.conversation.context import (
    NEW_CONVERSATION_TITLE,
    build_retrieval_query,
    select_history,
    title_from_message,
)
from app.orchestration.conversation.store import (
    ConversationRow,
    ConversationStore,
    MessageRow,
    SourceRow,
)
from app.shared.config.settings import AppSettings
from app.shared.exceptions import (
    AuthorizationError,
    ConflictError,
    NotFoundError,
    PlatformError,
    ValidationFailedError,
)
from app.shared.logging.setup import get_logger
from app.shared.observability.metrics import PlatformMetrics

logger = get_logger(__name__)
_tracer = get_tracer("acos.ai.conversation")
_FAILURE = "Sorry, I couldn't generate a response right now. Please try again."


class ConversationGenerationError(PlatformError):
    def __init__(self) -> None:
        super().__init__(
            title="Assistant Unavailable",
            detail=_FAILURE,
            status=503,
            code="AI_CONVERSATION_FAILED",
            type_uri="https://acos.local/problems/ai-conversation-failed",
        )


@dataclass(slots=True, frozen=True)
class ConversationPage:
    items: list[ConversationRow]
    page: int
    size: int
    total: int


@dataclass(slots=True, frozen=True)
class ConversationDetail:
    conversation: ConversationRow
    messages: list[MessageRow]
    truncated: bool


@dataclass(slots=True, frozen=True)
class SentMessages:
    user_message: MessageRow
    assistant_message: MessageRow


class ConversationService:
    def __init__(
        self,
        *,
        settings: AppSettings,
        store: ConversationStore,
        assistant: AssistantService,
        metrics: PlatformMetrics,
    ) -> None:
        self._settings = settings
        self._store = store
        self._assistant = assistant
        self._metrics = metrics

    def create(self, caller: CallerContext) -> ConversationRow:
        with _tracer.start_as_current_span("ai.conversation.create"):
            created = self._store.create(caller.owner_id, NEW_CONVERSATION_TITLE)
        self._metrics.conversation_created.inc()
        logger.info("conversation.created", owner_id=caller.owner_id, conversation_id=created.id)
        return created

    def list_conversations(
        self, caller: CallerContext, *, page: int, size: int
    ) -> ConversationPage:
        items, total = self._store.list_page(caller.owner_id, page=page, size=size)
        return ConversationPage(items=items, page=page, size=size, total=total)

    def get(self, caller: CallerContext, conversation_id: str) -> ConversationDetail:
        with _tracer.start_as_current_span("ai.conversation.load"):
            conversation = self._owned(caller, conversation_id)
            limit = self._settings.conversation_message_limit
            total = self._store.message_count(conversation_id)
            messages = self._store.messages(conversation_id, limit=limit)
        return ConversationDetail(
            conversation=conversation,
            messages=messages,
            truncated=total > len(messages),
        )

    def rename(self, caller: CallerContext, conversation_id: str, title: str) -> ConversationRow:
        cleaned = _validate_title(title, limit=self._settings.conversation_title_max_length)
        self._owned(caller, conversation_id)
        renamed = self._store.rename(conversation_id, caller.owner_id, cleaned)
        if renamed is None:
            raise NotFoundError("Conversation was not found")
        return renamed

    def delete(self, caller: CallerContext, conversation_id: str) -> None:
        self._owned(caller, conversation_id)
        deleted = self._store.delete(conversation_id, caller.owner_id)
        if not deleted:
            raise NotFoundError("Conversation was not found")
        if self._store.message_count(conversation_id) != 0:
            raise ConflictError("Conversation messages were not removed")
        self._metrics.conversation_deleted.inc()
        logger.info(
            "conversation.deleted", owner_id=caller.owner_id, conversation_id=conversation_id
        )

    async def send(
        self,
        caller: CallerContext,
        conversation_id: str,
        content: str,
        *,
        idempotency_key: str | None,
    ) -> SentMessages:
        started = time.perf_counter()
        cleaned = content.strip()
        if not cleaned:
            raise ValidationFailedError("A message is required")
        if len(cleaned) > self._settings.ai_max_message_characters:
            raise ValidationFailedError("A message exceeds the configured size limit")
        conversation = self._owned(caller, conversation_id)
        self._store.expire_stale_processing(
            conversation_id, self._settings.conversation_processing_timeout_seconds
        )
        if idempotency_key:
            existing = self._store.find_idempotent(conversation_id, idempotency_key)
            if existing is not None:
                return await self._resume(caller, existing)
        prior = self._store.messages(
            conversation_id, limit=self._settings.conversation_max_messages
        )
        previous_user = _previous_user(prior)
        original, retrieval_query = build_retrieval_query(
            cleaned,
            previous_user,
            max_characters=self._settings.ai_max_message_characters,
        )
        title = None
        if (
            conversation.title == NEW_CONVERSATION_TITLE
            and self._store.message_count(conversation_id) == 0
        ):
            title = title_from_message(original, limit=self._settings.conversation_title_max_length)
        with _tracer.start_as_current_span("ai.conversation.message.persist"):
            user_message, pending = self._store.begin_turn(
                conversation_id=conversation_id,
                owner_id=caller.owner_id,
                content=original,
                idempotency_key=idempotency_key,
                retrieval_query=retrieval_query,
                title=title,
            )
        self._metrics.conversation_messages.labels("USER", "COMPLETED").inc()
        assistant = await self._generate_into(caller, user_message, pending.id)
        self._metrics.conversation_latency.observe(time.perf_counter() - started)
        return SentMessages(user_message=user_message, assistant_message=assistant)

    async def _resume(self, caller: CallerContext, user_message: MessageRow) -> SentMessages:
        assistant = self._store.assistant_after(
            user_message.conversation_id, user_message.sequence_number
        )
        if assistant is not None and assistant.status == "COMPLETED":
            return SentMessages(user_message=user_message, assistant_message=assistant)
        if assistant is not None and assistant.status == "PROCESSING":
            raise ConflictError("This conversation is already generating a response")
        if assistant is not None and assistant.status == "FAILED":
            self._store.mark_processing(assistant.id)
            generated = await self._generate_into(caller, user_message, assistant.id)
            return SentMessages(user_message=user_message, assistant_message=generated)
        generated = await self._generate(caller, user_message.conversation_id, user_message)
        return SentMessages(user_message=user_message, assistant_message=generated)

    async def _generate(
        self, caller: CallerContext, conversation_id: str, user_message: MessageRow
    ) -> MessageRow:
        if self._store.has_processing(conversation_id):
            raise ConflictError("This conversation is already generating a response")
        pending = self._store.add_processing(
            conversation_id=conversation_id, owner_id=caller.owner_id
        )
        return await self._generate_into(caller, user_message, pending.id)

    async def _generate_into(
        self, caller: CallerContext, user_message: MessageRow, assistant_id: str
    ) -> MessageRow:
        try:
            history = self._store.messages(
                user_message.conversation_id,
                limit=self._settings.conversation_message_limit,
            )
            selected = select_history(
                [message for message in history if message.id != assistant_id],
                max_messages=self._settings.conversation_max_messages,
                max_characters=self._settings.conversation_max_characters,
            )
            self._metrics.conversation_context_messages.observe(len(selected))
            self._metrics.conversation_context_characters.observe(
                sum(len(message.content) for message in selected)
            )
            request = ChatRequest(
                messages=[
                    ChatMessage(
                        role=ChatRole.USER if message.role == "USER" else ChatRole.ASSISTANT,
                        content=message.content or " ",
                    )
                    for message in selected
                    if message.content.strip()
                ]
            )
            if not any(message.role == ChatRole.USER for message in request.messages):
                request = ChatRequest(
                    messages=[ChatMessage(role=ChatRole.USER, content=user_message.content)]
                )
            with _tracer.start_as_current_span("ai.conversation.context.build"):
                response = await self._assistant.chat(
                    request,
                    caller,
                    retrieval_query=user_message.retrieval_query or user_message.content,
                )
        except Exception as exc:
            self._store.fail_assistant(assistant_id, _FAILURE)
            self._metrics.conversation_messages.labels("ASSISTANT", "FAILED").inc()
            self._metrics.conversation_failures.inc()
            logger.warning(
                "conversation.generation_failed",
                owner_id=caller.owner_id,
                conversation_id=user_message.conversation_id,
                message_id=assistant_id,
                error_type=type(exc).__name__,
            )
            raise ConversationGenerationError() from exc
        sources = [
            SourceRow(
                content_id=source.content_id,
                chunk_id=source.chunk_id,
                title=source.title,
                content_type=source.content_type,
                section=source.section,
                path=source.path,
                source_url=source.url,
                score=source.score,
                rank=index,
            )
            for index, source in enumerate(response.sources, start=1)
        ]
        with _tracer.start_as_current_span("ai.assistant.message.persist"):
            saved = self._store.complete_assistant(
                assistant_id,
                content=response.answer,
                model=response.model,
                provider=response.provider,
                prompt_tokens=0,
                completion_tokens=0,
                grounded=response.grounded,
                sources=sources,
            )
        self._metrics.conversation_messages.labels("ASSISTANT", "COMPLETED").inc()
        return saved

    def _owned(self, caller: CallerContext, conversation_id: str) -> ConversationRow:
        return _owned_row(caller, self._store.get(conversation_id))


def _owned_row(self_owner: CallerContext, conversation: ConversationRow | None) -> ConversationRow:
    if conversation is None:
        raise NotFoundError("Conversation was not found")
    if conversation.owner_id != self_owner.owner_id:
        raise AuthorizationError("Conversation belongs to another user")
    return conversation


def _validate_title(title: str, *, limit: int) -> str:
    cleaned = " ".join(title.split())
    if not cleaned:
        raise ValidationFailedError("A title is required")
    if len(cleaned) > limit:
        raise ValidationFailedError("Title exceeds the configured size limit")
    if any(ord(character) < 32 for character in cleaned):
        raise ValidationFailedError("Title contains unsupported characters")
    return cleaned


def _previous_user(messages: list[MessageRow]) -> str | None:
    for message in reversed(messages):
        if message.role == "USER" and message.status == "COMPLETED":
            return message.content
    return None
