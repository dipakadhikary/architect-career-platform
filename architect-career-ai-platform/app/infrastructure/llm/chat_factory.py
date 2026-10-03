"""Single place that selects the Phase 1 chat provider from configuration."""

from __future__ import annotations

import httpx

from app.infrastructure.llm.ollama_chat_provider import OllamaChatProvider
from app.infrastructure.llm.openai_chat_provider import OpenAiChatProvider
from app.intelligence.assistant.errors import ProviderNotConfiguredError
from app.intelligence.assistant.provider import LlmProvider
from app.shared.config.settings import AppSettings


def build_chat_provider(
    settings: AppSettings,
    *,
    transport: httpx.AsyncBaseTransport | None = None,
) -> LlmProvider:
    """Return the configured provider. Callers do not branch on the provider name."""
    if settings.llm_provider == "openai":
        return OpenAiChatProvider(settings, transport=transport)
    if settings.llm_provider == "ollama":
        return OllamaChatProvider(settings, transport=transport)
    raise ProviderNotConfiguredError(
        f"Provider '{settings.llm_provider}' is not enabled for the assistant"
    )
