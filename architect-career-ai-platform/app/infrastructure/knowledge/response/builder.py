"""Structured Knowledge answer builder."""

from __future__ import annotations

from app.intelligence.knowledge.models import (
    BuiltContext,
    GenerationResult,
    KnowledgeAnswer,
    RetrievalHit,
)
from app.intelligence.knowledge.response.ports import ResponseBuilderPort


class DefaultResponseBuilder(ResponseBuilderPort):
    async def build(
        self,
        *,
        generation: GenerationResult,
        context: BuiltContext | None,
        hits: list[RetrievalHit],
        confidence: float,
    ) -> KnowledgeAnswer:
        sources = context.citations if context is not None else []
        return KnowledgeAnswer(
            answer=generation.answer,
            sources=sources,
            confidence=confidence,
            retrieved_documents=hits,
            latency_ms=generation.latency_ms,
            token_usage={
                "promptTokens": generation.prompt_tokens,
                "completionTokens": generation.completion_tokens,
                "totalTokens": generation.total_tokens,
            },
            model=generation.model,
            provider=generation.provider,
            prompt_version=generation.prompt_version,
            estimated_cost_usd=generation.estimated_cost_usd,
        )
