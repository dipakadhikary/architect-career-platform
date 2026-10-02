"""Knowledge reranking ports."""

from __future__ import annotations

from abc import ABC, abstractmethod

from app.intelligence.knowledge.models import RetrievalHit


class KnowledgeRerankerPort(ABC):
    @abstractmethod
    async def rerank(self, query: str, documents: list[RetrievalHit]) -> list[RetrievalHit]:
        raise NotImplementedError
