"""Vector retrieval filtered by the ACOS owner before any text is reused."""

from __future__ import annotations

import asyncio
import time
from collections.abc import Awaitable, Callable
from typing import Protocol, TypeVar

from app.infrastructure.observability.otel import get_tracer
from app.intelligence.assistant.errors import RagUnavailableError
from app.intelligence.embeddings.ports import EmbeddingPort, EmbeddingRequest
from app.intelligence.knowledge.models import VectorRecord
from app.intelligence.knowledge.vectorstore.ports import VectorSearchQuery, VectorStorePort
from app.orchestration.rag.models import RetrievedChunk
from app.shared.config.settings import AppSettings
from app.shared.exceptions import ValidationFailedError
from app.shared.logging.setup import get_logger
from app.shared.observability.metrics import PlatformMetrics

logger = get_logger(__name__)
_T = TypeVar("_T")
_tracer = get_tracer("acos.ai.rag")

_RETRYABLE = (TimeoutError, ConnectionError, OSError)


class ChunkRetriever(Protocol):
    async def retrieve(self, query: str, *, owner_id: str) -> list[RetrievedChunk]:
        """Return chunks the caller is allowed to read."""
        ...


class VectorRetriever:
    """Embeds a query with the indexing model and searches the Phase 3 vector store."""

    def __init__(
        self,
        *,
        settings: AppSettings,
        embeddings: EmbeddingPort,
        vector_store: VectorStorePort,
        metrics: PlatformMetrics,
    ) -> None:
        self._settings = settings
        self._embeddings = embeddings
        self._vector_store = vector_store
        self._metrics = metrics

    async def retrieve(
        self,
        query: str,
        *,
        owner_id: str,
        top_k: int | None = None,
    ) -> list[RetrievedChunk]:
        started = time.perf_counter()
        limit = self._settings.rag_top_k if top_k is None else top_k
        vector = await self._embed(query)
        hits = await self._search(vector, owner_id, limit)
        self._metrics.rag_retrieval_latency.observe(time.perf_counter() - started)
        visible = [hit for hit in hits if hit.owner_id == owner_id]
        minimum = self._settings.rag_min_score
        if minimum is not None:
            visible = [hit for hit in visible if hit.score >= minimum]
        if len(visible) != len(hits):
            logger.info(
                "rag.retrieval.filtered",
                owner_id=owner_id,
                dropped=len(hits) - len(visible),
            )
        ranked = sorted(visible, key=lambda item: item.score, reverse=True)
        limited = ranked[:limit]
        self._metrics.rag_retrieved_chunks.observe(len(limited))
        logger.info(
            "rag.retrieval.completed",
            owner_id=owner_id,
            chunks=len(limited),
            content_ids=[item.content_id for item in limited],
        )
        return limited

    async def _embed(self, query: str) -> list[float]:
        started = time.perf_counter()

        async def call() -> list[float]:
            response = await self._embeddings.embed(
                EmbeddingRequest(texts=[query], model=self._settings.embedding_model)
            )
            if len(response.vectors) != 1:
                raise RagUnavailableError()
            vector = response.vectors[0]
            expected = self._settings.embedding_dimensions
            if response.dimensions != expected or len(vector) != expected:
                raise RagUnavailableError()
            return vector

        try:
            with _tracer.start_as_current_span("query.embedding"):
                vector = await self._attempt(call)
        finally:
            self._metrics.rag_embedding_latency.observe(time.perf_counter() - started)
        return vector

    async def _search(self, vector: list[float], owner_id: str, top_k: int) -> list[RetrievedChunk]:
        started = time.perf_counter()
        minimum = self._settings.rag_min_score

        async def call() -> list[RetrievedChunk]:
            records = await self._vector_store.search(
                VectorSearchQuery(
                    embedding=vector,
                    top_k=top_k,
                    score_threshold=minimum,
                    filters={"owner_id": owner_id},
                )
            )
            return [chunk_from_record(record) for record in records]

        try:
            with _tracer.start_as_current_span("vector.retrieval"):
                return await self._attempt(call)
        finally:
            self._metrics.rag_search_latency.observe(time.perf_counter() - started)

    async def _attempt(self, fn: Callable[[], Awaitable[_T]]) -> _T:
        attempts = self._settings.rag_retry_attempts
        last: Exception | None = None
        for attempt in range(1, attempts + 1):
            try:
                return await asyncio.wait_for(fn(), self._settings.rag_timeout_seconds)
            except (ValidationFailedError, RagUnavailableError):
                raise
            except _RETRYABLE as exc:
                last = exc
                if attempt >= attempts:
                    break
            except Exception as exc:
                logger.info("rag.retrieval.failed", error_type=type(exc).__name__)
                raise RagUnavailableError() from exc
        logger.info("rag.retrieval.failed", error_type=type(last).__name__ if last else "timeout")
        raise RagUnavailableError() from last


def chunk_from_record(record: VectorRecord) -> RetrievedChunk:
    metadata = record.metadata or {}
    return RetrievedChunk(
        chunk_id=str(record.id),
        content_id=str(metadata.get("content_id") or record.document_id),
        content=record.text,
        score=float(record.score or 0.0),
        title=str(metadata.get("title") or ""),
        section=str(metadata.get("section") or ""),
        content_type=str(metadata.get("content_type") or ""),
        source_url=str(metadata.get("source_url") or ""),
        path=str(metadata.get("path") or ""),
        topic_id=_optional(metadata.get("topic_id")),
        owner_id=str(metadata.get("owner_id") or ""),
    )


def _optional(value: object) -> str | None:
    if value is None or value == "":
        return None
    return str(value)
