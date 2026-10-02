"""Ollama client adapter implementing LlmPort."""

from __future__ import annotations

from collections.abc import AsyncIterator

import httpx

from app.intelligence.llm.ports import LlmCompletionRequest, LlmCompletionResponse, LlmPort
from app.shared.config.settings import AppSettings
from app.shared.observability.usage import TokenUsagePlaceholder


class OllamaAdapter(LlmPort):
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings
        self._enabled = settings.ollama_enabled

    @property
    def provider_name(self) -> str:
        return "ollama"

    @property
    def enabled(self) -> bool:
        return self._enabled

    async def complete(self, request: LlmCompletionRequest) -> LlmCompletionResponse:
        if not self._enabled:
            raise RuntimeError("Ollama adapter is disabled")
        model = request.model or self._settings.ollama_default_model
        async with httpx.AsyncClient(
            base_url=self._settings.ollama_base_url, timeout=60.0
        ) as client:
            response = await client.post(
                "/api/chat",
                json={
                    "model": model,
                    "messages": [
                        {"role": message.role, "content": message.content}
                        for message in request.messages
                    ],
                    "stream": False,
                },
            )
            response.raise_for_status()
            payload = response.json()
        content = str(payload.get("message", {}).get("content", ""))
        return LlmCompletionResponse(
            content=content,
            model=model,
            provider=self.provider_name,
            usage=TokenUsagePlaceholder(),
            raw=payload,
        )

    async def stream(self, request: LlmCompletionRequest) -> AsyncIterator[str]:
        result = await self.complete(request)
        yield result.content

    async def health_check(self) -> bool:
        if not self._enabled:
            return False
        try:
            async with httpx.AsyncClient(
                base_url=self._settings.ollama_base_url, timeout=5.0
            ) as client:
                response = await client.get("/api/tags")
                return response.status_code == 200
        except httpx.HTTPError:
            return False
