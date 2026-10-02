"""Reusable retrieval capability port (wraps Enterprise RAG and future sources)."""

from __future__ import annotations

from abc import ABC, abstractmethod

from app.intelligence.agentic.models import RetrievalCapabilityHit, RetrievalCapabilityQuery


class CapabilityRetrieverPort(ABC):
    @abstractmethod
    async def retrieve(self, query: RetrievalCapabilityQuery) -> list[RetrievalCapabilityHit]:
        raise NotImplementedError
