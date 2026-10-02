"""Response builder ports."""

from __future__ import annotations

from abc import ABC, abstractmethod

from app.intelligence.knowledge.models import (
    BuiltContext,
    GenerationResult,
    KnowledgeAnswer,
    RetrievalHit,
)


class ResponseBuilderPort(ABC):
    @abstractmethod
    async def build(
        self,
        *,
        generation: GenerationResult,
        context: BuiltContext | None,
        hits: list[RetrievalHit],
        confidence: float,
    ) -> KnowledgeAnswer:
        raise NotImplementedError
