"""Reranker port."""

from __future__ import annotations

from abc import ABC, abstractmethod

from app.intelligence.retrievers.ports import RetrievedDocument


class RerankerPort(ABC):
    @abstractmethod
    async def rerank(
        self, query: str, documents: list[RetrievedDocument]
    ) -> list[RetrievedDocument]:
        raise NotImplementedError
