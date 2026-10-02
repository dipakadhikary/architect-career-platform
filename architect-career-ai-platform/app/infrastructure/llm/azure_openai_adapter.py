"""Azure OpenAI adapter implementing LlmPort."""

from __future__ import annotations

from collections.abc import AsyncIterator

from openai import AsyncAzureOpenAI

from app.intelligence.llm.ports import LlmCompletionRequest, LlmCompletionResponse, LlmPort
from app.shared.config.settings import AppSettings
from app.shared.observability.usage import TokenUsagePlaceholder


class AzureOpenAIAdapter(LlmPort):
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings
        self._client: AsyncAzureOpenAI | None = None
        if (
            settings.azure_openai_enabled
            and settings.azure_openai_api_key is not None
            and settings.azure_openai_endpoint
        ):
            self._client = AsyncAzureOpenAI(
                api_key=settings.azure_openai_api_key.get_secret_value(),
                azure_endpoint=settings.azure_openai_endpoint,
                api_version=settings.azure_openai_api_version,
            )

    @property
    def provider_name(self) -> str:
        return "azure_openai"

    @property
    def enabled(self) -> bool:
        return self._client is not None

    async def complete(self, request: LlmCompletionRequest) -> LlmCompletionResponse:
        if self._client is None:
            raise RuntimeError("Azure OpenAI adapter is disabled")
        model = request.model or self._settings.azure_openai_deployment
        response = await self._client.chat.completions.create(
            model=model,
            messages=[
                {"role": message.role, "content": message.content} for message in request.messages
            ],
            temperature=request.temperature,
            max_tokens=request.max_tokens,
        )
        choice = response.choices[0].message.content or ""
        usage = response.usage
        return LlmCompletionResponse(
            content=choice,
            model=response.model,
            provider=self.provider_name,
            usage=TokenUsagePlaceholder(
                prompt_tokens=getattr(usage, "prompt_tokens", 0) or 0,
                completion_tokens=getattr(usage, "completion_tokens", 0) or 0,
                total_tokens=getattr(usage, "total_tokens", 0) or 0,
            ),
            raw=response.model_dump(),
        )

    async def stream(self, request: LlmCompletionRequest) -> AsyncIterator[str]:
        if self._client is None:
            raise RuntimeError("Azure OpenAI adapter is disabled")
        model = request.model or self._settings.azure_openai_deployment
        stream = await self._client.chat.completions.create(
            model=model,
            messages=[
                {"role": message.role, "content": message.content} for message in request.messages
            ],
            temperature=request.temperature,
            max_tokens=request.max_tokens,
            stream=True,
        )
        async for event in stream:
            delta = event.choices[0].delta.content
            if delta:
                yield delta

    async def health_check(self) -> bool:
        return self._client is not None
