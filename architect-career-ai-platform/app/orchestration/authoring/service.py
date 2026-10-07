"""Create AI drafts. This service never writes ACOS knowledge."""

from __future__ import annotations

import time
from dataclasses import dataclass

from app.infrastructure.observability.otel import get_tracer
from app.intelligence.assistant.models import AnswerSource, CallerContext, ChatMessage
from app.intelligence.assistant.provider import LlmProvider
from app.orchestration.authoring.operations import (
    SOURCE_REQUIRED,
    TOPIC_OR_SOURCE,
    AuthoringOperation,
)
from app.orchestration.authoring.prompts import build_prompt
from app.orchestration.authoring.store import ProposalRow, ProposalStore, new_id
from app.orchestration.authoring.validator import validate_draft_text, validate_output
from app.orchestration.conversation.context import select_history
from app.orchestration.conversation.store import ConversationStore
from app.orchestration.rag.context import ContextBuilder
from app.orchestration.rag.query import prepare_query
from app.orchestration.rag.retriever import ChunkRetriever
from app.orchestration.rag.sources import sources_from_chunks
from app.shared.config.settings import AppSettings
from app.shared.exceptions import (
    AuthorizationError,
    ConflictError,
    NotFoundError,
    ValidationFailedError,
)
from app.shared.logging.setup import get_logger
from app.shared.observability.metrics import PlatformMetrics

logger = get_logger(__name__)
_tracer = get_tracer("acos.ai.authoring")
_UNGROUNDED = (
    "ACOS knowledge did not contain supporting material. "
    "This draft is not grounded ACOS knowledge."
)


@dataclass(slots=True, frozen=True)
class AuthoringCommand:
    operation: AuthoringOperation
    topic: str
    instructions: str
    source_content: str
    content_id: str | None
    source_version: int | None
    difficulty: str
    question_count: int
    conversation_id: str | None
    use_conversation: bool
    use_knowledge: bool


class ContentAuthoringService:
    """Validate, retrieve, prompt, and store a proposal. Saving stays in ACOS."""

    def __init__(
        self,
        *,
        settings: AppSettings,
        store: ProposalStore,
        provider: LlmProvider,
        metrics: PlatformMetrics,
        retriever: ChunkRetriever | None = None,
        context_builder: ContextBuilder | None = None,
        conversations: ConversationStore | None = None,
    ) -> None:
        self._settings = settings
        self._store = store
        self._provider = provider
        self._metrics = metrics
        self._retriever = retriever
        self._context = context_builder
        self._conversations = conversations

    async def generate(self, caller: CallerContext, command: AuthoringCommand) -> ProposalRow:
        self._require_enabled()
        _validate_command(command)
        started = time.perf_counter()
        operation = command.operation.value
        self._metrics.authoring_requests.labels(operation).inc()
        with _tracer.start_as_current_span("ai.authoring.request") as span:
            span.set_attribute("authoring.operation", operation)
            span.set_attribute("owner_id", caller.owner_id)
            try:
                row = await self._generate_row(caller, command, regenerated_from=None)
            except ValidationFailedError:
                self._metrics.authoring_validation_failure.inc()
                self._metrics.authoring_failure.labels(operation).inc()
                raise
            except Exception:
                self._metrics.authoring_failure.labels(operation).inc()
                raise
        self._metrics.authoring_success.labels(operation).inc()
        self._metrics.authoring_latency.observe(time.perf_counter() - started)
        logger.info(
            "authoring.generated",
            owner_id=caller.owner_id,
            proposal_id=row.id,
            operation=operation,
            prompt_version=row.prompt_version,
        )
        return row

    async def regenerate(
        self,
        caller: CallerContext,
        proposal_id: str,
        *,
        instructions: str | None,
    ) -> ProposalRow:
        previous = self._owned(caller, proposal_id)
        command = AuthoringCommand(
            operation=AuthoringOperation(previous.operation),
            topic=previous.topic,
            instructions=instructions if instructions is not None else previous.instructions,
            source_content=previous.source_content,
            content_id=previous.content_id,
            source_version=previous.source_version,
            difficulty=previous.difficulty,
            question_count=previous.question_count,
            conversation_id=previous.conversation_id,
            use_conversation=bool(previous.conversation_id),
            use_knowledge=previous.use_knowledge,
        )
        created = await self.generate(caller, command)
        self._metrics.authoring_regeneration.inc()
        return created

    def edit(self, caller: CallerContext, proposal_id: str, content: str) -> ProposalRow:
        row = self._owned(caller, proposal_id)
        if row.status == "REJECTED":
            raise ConflictError("A rejected proposal cannot be edited")
        cleaned = validate_draft_text(
            content,
            max_characters=self._settings.authoring_max_output_characters,
        )
        if not self._store.update_content(proposal_id, caller.owner_id, cleaned, "EDITING"):
            raise NotFoundError("Proposal was not found")
        updated = self._store.get(proposal_id)
        if updated is None:
            raise NotFoundError("Proposal was not found")
        return updated

    def accept(self, caller: CallerContext, proposal_id: str) -> ProposalRow:
        row = self._owned(caller, proposal_id)
        if row.status == "REJECTED":
            raise ConflictError("A rejected proposal cannot be accepted")
        if not self._store.update_status(proposal_id, caller.owner_id, "APPROVED"):
            raise NotFoundError("Proposal was not found")
        self._metrics.authoring_approval.inc()
        logger.info(
            "authoring.approved",
            owner_id=caller.owner_id,
            proposal_id=proposal_id,
            operation=row.operation,
        )
        approved = self._store.get(proposal_id)
        if approved is None:
            raise NotFoundError("Proposal was not found")
        return approved

    def reject(self, caller: CallerContext, proposal_id: str) -> ProposalRow:
        row = self._owned(caller, proposal_id)
        if not self._store.update_status(proposal_id, caller.owner_id, "REJECTED"):
            raise NotFoundError("Proposal was not found")
        self._metrics.authoring_rejection.inc()
        logger.info("authoring.rejected", owner_id=caller.owner_id, proposal_id=proposal_id)
        rejected = self._store.get(proposal_id)
        if rejected is None:
            raise NotFoundError("Proposal was not found")
        del row
        return rejected

    def get(self, caller: CallerContext, proposal_id: str) -> ProposalRow:
        return self._owned(caller, proposal_id)

    async def _generate_row(
        self,
        caller: CallerContext,
        command: AuthoringCommand,
        regenerated_from: str | None,
    ) -> ProposalRow:
        del regenerated_from
        source, truncated = _clip(
            command.source_content, self._settings.authoring_max_input_characters
        )
        warnings: list[str] = []
        if truncated:
            warnings.append("Source content was truncated to the configured input limit.")
        conversation = self._conversation_text(caller, command)
        retrieved, sources, grounded = await self._knowledge(caller, command, source)
        if command.use_knowledge and not grounded:
            warnings.append(_UNGROUNDED)
        messages = build_prompt(
            version=self._settings.authoring_prompt_version,
            operation=command.operation,
            topic=command.topic,
            instructions=command.instructions,
            source_content=source,
            retrieved=retrieved,
            conversation=conversation,
            difficulty=command.difficulty,
            question_count=min(command.question_count, self._settings.authoring_max_questions),
        )
        with _tracer.start_as_current_span("ai.authoring.llm"):
            completion = await self._provider.chat(
                messages,
                model=self._settings.resolve_chat_model(),
                temperature=self._settings.authoring_temperature,
                max_tokens=self._settings.authoring_max_output_tokens,
            )
        self._metrics.authoring_tokens_input.inc(completion.prompt_tokens)
        self._metrics.authoring_tokens_output.inc(completion.completion_tokens)
        self._metrics.token_usage.labels(completion.provider, completion.model, "input").inc(
            completion.prompt_tokens
        )
        self._metrics.token_usage.labels(completion.provider, completion.model, "output").inc(
            completion.completion_tokens
        )
        with _tracer.start_as_current_span("ai.authoring.validation"):
            content, questions = validate_output(
                command.operation,
                completion.answer,
                max_characters=self._settings.authoring_max_output_characters,
                max_questions=self._settings.authoring_max_questions,
            )
        now = _stamp()
        return self._store.insert(
            ProposalRow(
                id=new_id(),
                owner_id=caller.owner_id,
                operation=command.operation.value,
                status="GENERATED",
                topic=command.topic,
                instructions=command.instructions,
                source_content=command.source_content,
                content_id=command.content_id,
                source_version=command.source_version,
                conversation_id=command.conversation_id if command.use_conversation else None,
                use_knowledge=command.use_knowledge,
                difficulty=command.difficulty,
                question_count=command.question_count,
                content=content,
                questions=tuple(item.model_dump() for item in questions),
                warnings=tuple(warnings),
                sources=tuple(sources),
                model=completion.model,
                provider=completion.provider,
                prompt_version=self._settings.authoring_prompt_version,
                prompt_tokens=completion.prompt_tokens,
                completion_tokens=completion.completion_tokens,
                grounded=grounded,
                created_at=now,
                updated_at=now,
            )
        )

    def _conversation_text(self, caller: CallerContext, command: AuthoringCommand) -> str:
        if not command.use_conversation:
            return ""
        if not command.conversation_id or self._conversations is None:
            raise ValidationFailedError("A conversation is required for this request")
        with _tracer.start_as_current_span("ai.authoring.context"):
            conversation = self._conversations.get(command.conversation_id)
        if conversation is None:
            raise NotFoundError("Conversation was not found")
        if conversation.owner_id != caller.owner_id:
            raise AuthorizationError("Conversation belongs to another user")
        messages = self._conversations.messages(
            command.conversation_id,
            limit=self._settings.conversation_message_limit,
        )
        selected = select_history(
            messages,
            max_messages=self._settings.conversation_max_messages,
            max_characters=self._settings.conversation_max_characters,
        )
        lines = [f"{item.role}: {item.content}" for item in selected]
        return "\n".join(lines)

    async def _knowledge(
        self,
        caller: CallerContext,
        command: AuthoringCommand,
        source: str,
    ) -> tuple[str, list[AnswerSource], bool]:
        if not command.use_knowledge or self._retriever is None or not self._settings.rag_enabled:
            return "", [], False
        query = command.topic or command.instructions or source
        if not query.strip():
            return "", [], False
        with _tracer.start_as_current_span("ai.authoring.retrieval"):
            prepared = prepare_query(query, max_characters=self._settings.ai_max_message_characters)
            chunks = await self._retriever.retrieve(prepared, owner_id=caller.owner_id)
        if self._context is None:
            return "", [], False
        context, selected = self._context.build(chunks)
        sources = sources_from_chunks(selected, limit=self._settings.rag_top_k)
        return context, sources, bool(selected)

    def _owned(self, caller: CallerContext, proposal_id: str) -> ProposalRow:
        row = self._store.get(proposal_id)
        if row is None:
            raise NotFoundError("Proposal was not found")
        if row.owner_id != caller.owner_id:
            raise AuthorizationError("Proposal belongs to another user")
        return row

    def _require_enabled(self) -> None:
        if not self._settings.ai_enabled:
            raise ValidationFailedError("ACOS AI authoring is disabled")


def _validate_command(command: AuthoringCommand) -> None:
    if command.operation in SOURCE_REQUIRED and not command.source_content.strip():
        raise ValidationFailedError("Existing content is required for this operation")
    if command.operation in TOPIC_OR_SOURCE and not (
        command.topic.strip() or command.instructions.strip() or command.source_content.strip()
    ):
        raise ValidationFailedError("A topic, instruction, or source is required")
    if command.difficulty and command.difficulty not in {"BEGINNER", "INTERMEDIATE", "ADVANCED"}:
        raise ValidationFailedError("Difficulty is not supported")
    if command.question_count < 1:
        raise ValidationFailedError("At least one question is required")


def _clip(value: str, limit: int) -> tuple[str, bool]:
    if len(value) <= limit:
        return value, False
    return value[:limit], True


def _stamp() -> str:
    from datetime import UTC, datetime

    return datetime.now(UTC).isoformat()


def prompt_messages(command: AuthoringCommand, settings: AppSettings) -> list[ChatMessage]:
    """Exposed for tests that inspect prompt boundaries without calling a provider."""
    return build_prompt(
        version=settings.authoring_prompt_version,
        operation=command.operation,
        topic=command.topic,
        instructions=command.instructions,
        source_content=command.source_content,
        retrieved="",
        conversation="",
        difficulty=command.difficulty,
        question_count=command.question_count,
    )
