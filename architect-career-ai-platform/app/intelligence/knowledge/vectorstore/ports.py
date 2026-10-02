"""Vector store repository ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any

from app.intelligence.knowledge.models import VectorRecord


@dataclass(slots=True, frozen=True)
class VectorSearchQuery:
    embedding: list[float]
    top_k: int = 10
    score_threshold: float | None = None
    filters: dict[str, Any] = field(default_factory=dict)


class VectorStorePort(ABC):
    @abstractmethod
    async def ensure_collection(self, dimensions: int) -> None:
        raise NotImplementedError

    @abstractmethod
    async def upsert(self, records: list[VectorRecord]) -> None:
        raise NotImplementedError

    @abstractmethod
    async def search(self, query: VectorSearchQuery) -> list[VectorRecord]:
        raise NotImplementedError

    @abstractmethod
    async def delete_by_document(self, document_id: str) -> None:
        raise NotImplementedError

    @abstractmethod
    async def keyword_search(
        self,
        text: str,
        *,
        top_k: int = 10,
        filters: dict[str, Any] | None = None,
    ) -> list[VectorRecord]:
        raise NotImplementedError
