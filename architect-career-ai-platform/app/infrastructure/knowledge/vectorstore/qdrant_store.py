"""Qdrant vector store repository."""

from __future__ import annotations

from typing import Any
from uuid import uuid4

from qdrant_client.http import models as qmodels

from app.infrastructure.vector.qdrant_adapter import QdrantAdapter
from app.intelligence.knowledge.models import VectorRecord
from app.intelligence.knowledge.vectorstore.ports import VectorSearchQuery, VectorStorePort
from app.shared.config.settings import AppSettings


class QdrantVectorStore(VectorStorePort):
    def __init__(self, settings: AppSettings, qdrant: QdrantAdapter) -> None:
        self._settings = settings
        self._qdrant = qdrant
        self._collection = settings.qdrant_collection

    async def ensure_collection(self, dimensions: int) -> None:
        client = self._qdrant.client
        names = {item.name for item in client.get_collections().collections}
        if self._collection in names:
            return
        client.create_collection(
            collection_name=self._collection,
            vectors_config=qmodels.VectorParams(size=dimensions, distance=qmodels.Distance.COSINE),
        )

    async def upsert(self, records: list[VectorRecord]) -> None:
        if not records:
            return
        points = [
            qmodels.PointStruct(
                id=record.id if _is_uuid(record.id) else str(uuid4()),
                vector=record.embedding,
                payload={
                    "document_id": record.document_id,
                    "text": record.text,
                    "record_id": record.id,
                    **record.metadata,
                },
            )
            for record in records
        ]
        self._qdrant.client.upsert(collection_name=self._collection, points=points)

    async def search(self, query: VectorSearchQuery) -> list[VectorRecord]:
        qdrant_filter = _to_filter(query.filters)
        results = self._qdrant.client.search(
            collection_name=self._collection,
            query_vector=query.embedding,
            limit=query.top_k,
            score_threshold=query.score_threshold,
            query_filter=qdrant_filter,
        )
        records: list[VectorRecord] = []
        for point in results:
            payload = point.payload or {}
            records.append(
                VectorRecord(
                    id=str(payload.get("record_id") or point.id),
                    document_id=str(payload.get("document_id", "")),
                    text=str(payload.get("text", "")),
                    embedding=[],
                    metadata={k: v for k, v in payload.items() if k not in {"text", "record_id"}},
                    score=float(point.score),
                )
            )
        return records

    async def delete_by_document(self, document_id: str) -> None:
        self._qdrant.client.delete(
            collection_name=self._collection,
            points_selector=qmodels.FilterSelector(
                filter=qmodels.Filter(
                    must=[
                        qmodels.FieldCondition(
                            key="document_id",
                            match=qmodels.MatchValue(value=document_id),
                        )
                    ]
                )
            ),
        )

    async def keyword_search(
        self,
        text: str,
        *,
        top_k: int = 10,
        filters: dict[str, Any] | None = None,
    ) -> list[VectorRecord]:
        # Qdrant full-text requires payload indexes; fallback filter by metadata and local score.
        scroll_filter = _to_filter(filters or {})
        points, _ = self._qdrant.client.scroll(
            collection_name=self._collection,
            scroll_filter=scroll_filter,
            limit=max(top_k * 5, 50),
            with_payload=True,
            with_vectors=False,
        )
        query_terms = {term.lower() for term in text.split() if term}
        scored: list[VectorRecord] = []
        for point in points:
            payload = point.payload or {}
            body = str(payload.get("text", "")).lower()
            overlap = sum(1 for term in query_terms if term in body)
            if overlap == 0:
                continue
            scored.append(
                VectorRecord(
                    id=str(payload.get("record_id") or point.id),
                    document_id=str(payload.get("document_id", "")),
                    text=str(payload.get("text", "")),
                    embedding=[],
                    metadata={k: v for k, v in payload.items() if k not in {"text", "record_id"}},
                    score=overlap / max(len(query_terms), 1),
                )
            )
        scored.sort(key=lambda item: item.score or 0.0, reverse=True)
        return scored[:top_k]


def _to_filter(filters: dict[str, Any]) -> qmodels.Filter | None:
    if not filters:
        return None
    conditions = [
        qmodels.FieldCondition(key=key, match=qmodels.MatchValue(value=value))
        for key, value in filters.items()
    ]
    return qmodels.Filter(must=conditions)


def _is_uuid(value: str) -> bool:
    from uuid import UUID

    try:
        UUID(value)
        return True
    except Exception:
        return False
