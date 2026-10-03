"""Assistant orchestration. This layer does not import provider SDKs or perform retrieval."""

from __future__ import annotations

import time

from app.intelligence.assistant.errors import AiDisabledError
from app.intelligence.assistant.models import CallerContext, ChatRequest, ChatResponse, ChatRole
from app.intelligence.assistant.provider import LlmProvider
from app.orchestration.assistant.prompt import PromptBuilder
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
    ) -> None:
        self._settings = settings
        self._provider = provider
        self._prompt_builder = prompt_builder or PromptBuilder()

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

        elapsed = time.perf_counter() - started
        get_metrics().assistant_requests.labels(completion.provider, "success").inc()
        get_metrics().assistant_latency.labels(completion.provider).observe(elapsed)
        if completion.prompt_tokens or completion.completion_tokens:
            get_metrics().token_usage.labels(
                completion.provider, completion.model, "prompt"
            ).inc(completion.prompt_tokens)
            get_metrics().token_usage.labels(
                completion.provider, completion.model, "completion"
            ).inc(completion.completion_tokens)
        logger.info(
            "assistant.chat.completed",
            provider=completion.provider,
            model=completion.model,
            owner_id=caller.owner_id,
            duration_ms=round(elapsed * 1000, 1),
            prompt_tokens=completion.prompt_tokens,
            completion_tokens=completion.completion_tokens,
        )
        return ChatResponse(
            answer=completion.answer,
            model=completion.model,
            provider=completion.provider,
            correlation_id=caller.correlation_id,
        )

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

