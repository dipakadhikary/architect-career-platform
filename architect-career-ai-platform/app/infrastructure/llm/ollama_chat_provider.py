"""Ollama chat provider. The Ollama payload does not leave this module."""

from __future__ import annotations

import httpx

from app.infrastructure.llm.http_chat import map_status, post_json
from app.intelligence.assistant.errors import ProviderNotConfiguredError, ProviderUnexpectedError
from app.intelligence.assistant.models import ChatMessage, NormalizedCompletion
from app.shared.config.settings import AppSettings


class OllamaChatProvider:
    """Calls the local Ollama chat API and returns a normalized completion."""

    provider_name = "ollama"

    def __init__(
        self,
        settings: AppSettings,
        *,
        transport: httpx.AsyncBaseTransport | None = None,
    ) -> None:
        self._settings = settings
        self._transport = transport

    async def chat(
        self,
        messages: list[ChatMessage],
        *,
        model: str,
        temperature: float,
        max_tokens: int,
    ) -> NormalizedCompletion:
        base_url = self._settings.ollama_base_url.strip()
        if not base_url:
            raise ProviderNotConfiguredError("Ollama base URL is not configured")
        response = await post_json(
            url=f"{base_url.rstrip('/')}/api/chat",
            headers={"Content-Type": "application/json"},
            payload={
                "model": model,
                "messages": [
                    {"role": message.role.value, "content": message.content} for message in messages
                ],
                "stream": False,
                "options": {"temperature": temperature, "num_predict": max_tokens},
            },
            timeout_seconds=self._settings.ai_timeout_seconds,
            max_attempts=self._settings.ai_retry_max_attempts,
            transport=self._transport,
        )
        map_status(response)
        return self._normalize(response, fallback_model=model)

    def _normalize(self, response: httpx.Response, *, fallback_model: str) -> NormalizedCompletion:
        try:
            payload = response.json()
        except ValueError as exc:
            raise ProviderUnexpectedError() from exc
        message = payload.get("message") if isinstance(payload, dict) else None
        content = message.get("content") if isinstance(message, dict) else None
        if not isinstance(content, str) or not content.strip():
            raise ProviderUnexpectedError()
        model = payload.get("model") if isinstance(payload, dict) else None
        return NormalizedCompletion(
            answer=content,
            model=model if isinstance(model, str) and model else fallback_model,
            provider=self.provider_name,
        )
