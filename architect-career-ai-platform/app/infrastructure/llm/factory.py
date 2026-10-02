"""LLM provider selection for Knowledge generation."""

from __future__ import annotations

from collections.abc import AsyncIterator

from app.infrastructure.llm.azure_openai_adapter import AzureOpenAIAdapter
from app.infrastructure.llm.ollama_adapter import OllamaAdapter
from app.infrastructure.llm.openai_adapter import OpenAIAdapter
from app.intelligence.llm.ports import LlmCompletionRequest, LlmCompletionResponse, LlmPort
from app.shared.config.settings import AppSettings
from app.shared.observability.usage import CostPlaceholder, TokenUsagePlaceholder


class ExtractiveSummarizer(LlmPort):
    """Offline-safe generation fallback used when remote LLMs are disabled."""

    @property
    def provider_name(self) -> str:
        return "extractive"

    async def complete(self, request: LlmCompletionRequest) -> LlmCompletionResponse:
        user = next(
            (message.content for message in reversed(request.messages) if message.role == "user"),
            "",
        )
        paragraphs = [part.strip() for part in user.split("\n\n") if part.strip()]
        content_block = paragraphs[-1] if paragraphs else user
        if content_block.lower().startswith("content:"):
            content_block = content_block.split(":", 1)[1].strip()
        sentences = [s.strip() for s in content_block.replace("\n", " ").split(". ") if s.strip()]
        summary = ". ".join(sentences[:3]).strip()
        if summary and not summary.endswith("."):
            summary += "."
        bullets = "\n".join(f"- {sentence.rstrip('.')}" for sentence in sentences[:3])
        answer = f"{summary}\n\n{bullets}".strip()
        return LlmCompletionResponse(
            content=answer or "No content available to summarize.",
            model="extractive-v1",
            provider=self.provider_name,
            usage=TokenUsagePlaceholder(
                prompt_tokens=len(user.split()),
                completion_tokens=len(answer.split()),
                total_tokens=len(user.split()) + len(answer.split()),
            ),
            cost=CostPlaceholder(),
        )

    async def stream(self, request: LlmCompletionRequest) -> AsyncIterator[str]:
        result = await self.complete(request)
        yield result.content

    async def health_check(self) -> bool:
        return True


def build_llm_port(settings: AppSettings) -> LlmPort:
    if settings.llm_provider == "openai" and settings.openai_enabled:
        return OpenAIAdapter(settings)
    if settings.llm_provider == "azure_openai" and settings.azure_openai_enabled:
        return AzureOpenAIAdapter(settings)
    if settings.llm_provider == "ollama" and settings.ollama_enabled:
        return OllamaAdapter(settings)
    return ExtractiveSummarizer()
