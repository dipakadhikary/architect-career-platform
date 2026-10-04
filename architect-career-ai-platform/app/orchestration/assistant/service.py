"""Assistant orchestration. Retrieval stays behind the RAG engine when it is enabled."""

from __future__ import annotations

import time

from app.intelligence.assistant.errors import AiDisabledError
from app.intelligence.assistant.models import CallerContext, ChatRequest, ChatResponse, ChatRole
from app.intelligence.assistant.provider import LlmProvider
from app.orchestration.assistant.prompt import PromptBuilder
from app.orchestration.rag.engine import RagEngine
from app.shared.config.settings import AppSettings
from app.shared.exceptions import AuthorizationError, ValidationFailedError
from app.shared.logging.setup import get_logger
from app.shared.observability.metrics import get_metrics

logger = get_logger(__name__)


class AssistantService:
    """Validates a chat request, applies the system instruction, and calls the provider port."""

    def __init__(
        self,
        settings: AppSettings,
        provider: LlmProvider,
        prompt_builder: PromptBuilder | None = None,
        rag: RagEngine | None = None,
    ) -> None:
        self._settings = settings
        self._provider = provider
        self._prompt_builder = prompt_builder or PromptBuilder()
        self._rag = rag if settings.rag_enabled else None

    async def chat(self, request: ChatRequest, caller: CallerContext) -> ChatResponse:
        started = time.perf_counter()
        provider_name = self._provider.provider_name
        logger.info(
            "assistant.chat.received",
            provider=provider_name,
            owner_id=caller.owner_id,
            message_count=len(request.messages),
        )
        try:
            self._ensure_enabled()
            self._ensure_owner(request, caller)
            self._validate_limits(request)
            if self._rag is not None:
                grounded = await self._rag.answer(request, caller, self._provider)
                return self._finish(caller, started, grounded)
            messages = self._prompt_builder.build(
                self._settings.ai_system_instruction, request.messages
            )
            completion = await self._provider.chat(
                messages,
                model=self._settings.resolve_chat_model(),
                temperature=self._settings.ai_temperature,
                max_tokens=self._settings.ai_max_tokens,
            )
        except Exception as exc:
            elapsed = time.perf_counter() - started
            get_metrics().assistant_requests.labels(provider_name, "failure").inc()
            get_metrics().assistant_latency.labels(provider_name).observe(elapsed)
            logger.warning(
                "assistant.chat.failed",
                provider=provider_name,
                owner_id=caller.owner_id,
                duration_ms=round(elapsed * 1000, 1),
                error_type=type(exc).__name__,
            )
            raise

        response = ChatResponse(
            answer=completion.answer,
            model=completion.model,
            provider=completion.provider,
            correlation_id=caller.correlation_id,
        )
        return self._finish(
            caller,
            started,
            response,
            prompt_tokens=completion.prompt_tokens,
            completion_tokens=completion.completion_tokens,
        )

    def _finish(
        self,
        caller: CallerContext,
        started: float,
        response: ChatResponse,
        *,
        prompt_tokens: int = 0,
        completion_tokens: int = 0,
    ) -> ChatResponse:
        elapsed = time.perf_counter() - started
        get_metrics().assistant_requests.labels(response.provider, "success").inc()
        get_metrics().assistant_latency.labels(response.provider).observe(elapsed)
        if prompt_tokens or completion_tokens:
            get_metrics().token_usage.labels(response.provider, response.model, "prompt").inc(
                prompt_tokens
            )
            get_metrics().token_usage.labels(
                response.provider, response.model, "completion"
            ).inc(completion_tokens)
        logger.info(
            "assistant.chat.completed",
            provider=response.provider,
            model=response.model,
            owner_id=caller.owner_id,
            duration_ms=round(elapsed * 1000, 1),
            grounded=response.grounded,
            sources=len(response.sources),
            prompt_tokens=prompt_tokens,
            completion_tokens=completion_tokens,
        )
        return response

    def _ensure_enabled(self) -> None:
        if not self._settings.ai_enabled:
            raise AiDisabledError()

    def _ensure_owner(self, request: ChatRequest, caller: CallerContext) -> None:
        if request.user_id is not None and request.user_id != caller.owner_id:
            raise AuthorizationError("user_id does not match the authenticated caller")

    def _validate_limits(self, request: ChatRequest) -> None:
        if len(request.messages) > self._settings.ai_max_messages:
            raise ValidationFailedError("Too many messages")
        if not any(message.role == ChatRole.USER for message in request.messages):
            raise ValidationFailedError("At least one user message is required")
        limit = self._settings.ai_max_message_characters
        for message in request.messages:
            if len(message.content) > limit:
                raise ValidationFailedError("A message exceeds the configured size limit")

