"""Dense / keyword / hybrid retrieval."""

from __future__ import annotations

import time

from app.intelligence.embeddings.ports import EmbeddingPort, EmbeddingRequest
from app.intelligence.knowledge.caching.ports import RagCachePort
from app.intelligence.knowledge.models import RetrievalHit, RetrievalMode
from app.intelligence.knowledge.retrieval.ports import (
    KnowledgeRetrievalQuery,
    KnowledgeRetrieverPort,
)
from app.intelligence.knowledge.vectorstore.ports import VectorSearchQuery, VectorStorePort
from app.shared.observability.metrics import PlatformMetrics


class DefaultKnowledgeRetriever(KnowledgeRetrieverPort):
    def __init__(
        self,
        *,
        embeddings: EmbeddingPort,
        store: VectorStorePort,
        cache: RagCachePort | None,
        metrics: PlatformMetrics,
        embedding_model: str,
    ) -> None:
        self._embeddings = embeddings
        self._store = store
        self._cache = cache
        self._metrics = metrics
        self._embedding_model = embedding_model

    async def retrieve(self, query: KnowledgeRetrievalQuery) -> list[RetrievalHit]:
        started = time.perf_counter()
        try:
            if query.mode == RetrievalMode.KEYWORD:
                records = await self._store.keyword_search(
                    query.text, top_k=query.top_k, filters=query.filters
                )
            elif query.mode == RetrievalMode.HYBRID:
                dense = await self._dense(query)
                keyword = await self._store.keyword_search(
                    query.text, top_k=query.top_k, filters=query.filters
                )
                records = _rrf_fuse(dense, keyword, query.top_k)
            else:
                records = await self._dense(query)
        finally:
            self._metrics.knowledge_retrieval_latency.observe(time.perf_counter() - started)

        return [
            RetrievalHit(
                id=record.id,
                document_id=record.document_id,
                text=record.text,
                score=float(record.score or 0.0),
                metadata=record.metadata,
            )
            for record in records
        ]

    async def _dense(self, query: KnowledgeRetrievalQuery):
        cache_key = f"emb:{self._embedding_model}:{query.text}"
        vector = await self._cache.get_embedding(cache_key) if self._cache else None
        if vector is None:
            embed_started = time.perf_counter()
            embedded = await self._embeddings.embed(
                EmbeddingRequest(texts=[query.text], model=self._embedding_model)
            )
            self._metrics.knowledge_embedding_latency.observe(time.perf_counter() - embed_started)
            vector = embedded.vectors[0]
            if self._cache is not None:
                await self._cache.set_embedding(cache_key, vector, ttl_seconds=3600)
        return await self._store.search(
            VectorSearchQuery(
                embedding=vector,
                top_k=query.top_k,
                score_threshold=query.score_threshold,
                filters=query.filters,
            )
        )


def _rrf_fuse(dense, keyword, top_k: int):
    scores: dict[str, float] = {}
    items = {}
    for rank, record in enumerate(dense):
        scores[record.id] = scores.get(record.id, 0.0) + 1.0 / (60 + rank)
        items[record.id] = record
    for rank, record in enumerate(keyword):
        scores[record.id] = scores.get(record.id, 0.0) + 1.0 / (60 + rank)
        items[record.id] = record
    ordered = sorted(scores.items(), key=lambda pair: pair[1], reverse=True)
    fused = []
    for record_id, score in ordered[:top_k]:
        record = items[record_id]
        fused.append(
            type(record)(
                id=record.id,
                document_id=record.document_id,
                text=record.text,
                embedding=record.embedding,
                metadata=record.metadata,
                score=score,
            )
        )
    return fused
