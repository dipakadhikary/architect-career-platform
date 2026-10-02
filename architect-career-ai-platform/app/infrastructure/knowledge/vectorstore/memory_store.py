"""In-memory vector store for tests and local development."""

from __future__ import annotations

import math
from typing import Any

from app.intelligence.knowledge.models import VectorRecord
from app.intelligence.knowledge.vectorstore.ports import VectorSearchQuery, VectorStorePort


class InMemoryVectorStore(VectorStorePort):
    def __init__(self) -> None:
        self._records: dict[str, VectorRecord] = {}
        self._dimensions: int | None = None

    async def ensure_collection(self, dimensions: int) -> None:
        self._dimensions = dimensions

    async def upsert(self, records: list[VectorRecord]) -> None:
        for record in records:
            self._records[record.id] = record

    async def search(self, query: VectorSearchQuery) -> list[VectorRecord]:
        scored: list[VectorRecord] = []
        for record in self._records.values():
            if not _matches_filters(record.metadata, query.filters):
                continue
            score = _cosine(query.embedding, record.embedding)
            if query.score_threshold is not None and score < query.score_threshold:
                continue
            scored.append(
                VectorRecord(
                    id=record.id,
                    document_id=record.document_id,
                    text=record.text,
                    embedding=record.embedding,
                    metadata=record.metadata,
                    score=score,
                )
            )
        scored.sort(key=lambda item: item.score or 0.0, reverse=True)
        return scored[: query.top_k]

    async def delete_by_document(self, document_id: str) -> None:
        self._records = {
            key: value for key, value in self._records.items() if value.document_id != document_id
        }

    async def keyword_search(
        self,
        text: str,
        *,
        top_k: int = 10,
        filters: dict[str, Any] | None = None,
    ) -> list[VectorRecord]:
        filters = filters or {}
        query_terms = {term.lower() for term in text.split() if term}
        scored: list[VectorRecord] = []
        for record in self._records.values():
            if not _matches_filters(record.metadata, filters):
                continue
            haystack = record.text.lower()
            overlap = sum(1 for term in query_terms if term in haystack)
            if overlap == 0:
                continue
            score = overlap / max(len(query_terms), 1)
            scored.append(
                VectorRecord(
                    id=record.id,
                    document_id=record.document_id,
                    text=record.text,
                    embedding=record.embedding,
                    metadata=record.metadata,
                    score=score,
                )
            )
        scored.sort(key=lambda item: item.score or 0.0, reverse=True)
        return scored[:top_k]


def _matches_filters(metadata: dict[str, Any], filters: dict[str, Any]) -> bool:
    for key, expected in filters.items():
        if metadata.get(key) != expected:
            return False
    return True


def _cosine(left: list[float], right: list[float]) -> float:
    size = min(len(left), len(right))
    if size == 0:
        return 0.0
    dot = sum(left[i] * right[i] for i in range(size))
    left_norm = math.sqrt(sum(left[i] * left[i] for i in range(size))) or 1.0
    right_norm = math.sqrt(sum(right[i] * right[i] for i in range(size))) or 1.0
    return dot / (left_norm * right_norm)
