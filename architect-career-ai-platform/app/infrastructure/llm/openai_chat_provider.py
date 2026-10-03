"""OpenAI chat provider. SDK types do not leave this module."""

from __future__ import annotations

import httpx

from app.infrastructure.llm.http_chat import map_status, post_json
from app.intelligence.assistant.errors import ProviderNotConfiguredError, ProviderUnexpectedError
from app.intelligence.assistant.models import ChatMessage, NormalizedCompletion
from app.shared.config.settings import AppSettings


class OpenAiChatProvider:
    """Calls the OpenAI chat completions HTTP API and returns a normalized completion."""

    provider_name = "openai"

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
        api_key = self._api_key()
        url = f"{self._settings.openai_base_url.rstrip('/')}/chat/completions"
        response = await post_json(
            url=url,
            headers={
                "Authorization": f"Bearer {api_key}",
                "Content-Type": "application/json",
            },
            payload={
                "model": model,
                "messages": [
                    {"role": message.role.value, "content": message.content} for message in messages
                ],
                "temperature": temperature,
                "max_tokens": max_tokens,
            },
            timeout_seconds=self._settings.ai_timeout_seconds,
            max_attempts=self._settings.ai_retry_max_attempts,
            transport=self._transport,
        )
        map_status(response)
        return self._normalize(response, fallback_model=model)

    def _api_key(self) -> str:
        secret = self._settings.openai_api_key
        if secret is None or not secret.get_secret_value().strip():
            raise ProviderNotConfiguredError("OpenAI API key is not configured")
        return secret.get_secret_value()

    def _normalize(self, response: httpx.Response, *, fallback_model: str) -> NormalizedCompletion:
        try:
            payload = response.json()
        except ValueError as exc:
            raise ProviderUnexpectedError() from exc
        choices = payload.get("choices")
        if not isinstance(choices, list) or not choices:
            raise ProviderUnexpectedError()
        message = choices[0].get("message") if isinstance(choices[0], dict) else None
        content = message.get("content") if isinstance(message, dict) else None
        if not isinstance(content, str) or not content.strip():
            raise ProviderUnexpectedError()
        usage = payload.get("usage") if isinstance(payload.get("usage"), dict) else {}
        model = payload.get("model")
        return NormalizedCompletion(
            answer=content,
            model=model if isinstance(model, str) and model else fallback_model,
            provider=self.provider_name,
            prompt_tokens=_int_field(usage, "prompt_tokens"),
            completion_tokens=_int_field(usage, "completion_tokens"),
        )


def _int_field(payload: dict[str, object], name: str) -> int:
    value = payload.get(name, 0)
    return value if isinstance(value, int) else 0
