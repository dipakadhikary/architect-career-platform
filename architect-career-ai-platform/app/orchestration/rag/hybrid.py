"""Run lexical and vector retrieval together, then fuse and optionally rerank."""

from __future__ import annotations

import asyncio
import time
from collections.abc import Awaitable
from dataclasses import replace
from typing import Protocol

from app.infrastructure.observability.otel import get_tracer
from app.intelligence.assistant.errors import RagUnavailableError
from app.orchestration.rag.fusion import reciprocal_rank_fusion, weighted_fusion
from app.orchestration.rag.models import RetrievedChunk
from app.orchestration.rag.rerank import RagReranker
from app.shared.config.settings import AppSettings
from app.shared.logging.setup import get_logger
from app.shared.observability.metrics import PlatformMetrics

logger = get_logger(__name__)
_tracer = get_tracer("acos.ai.rag")


class LexicalLeg(Protocol):
    async def retrieve(self, query: str, *, owner_id: str) -> list[RetrievedChunk]:
        """Return lexical hits for this owner."""
        ...


class VectorLeg(Protocol):
    async def retrieve(
        self,
        query: str,
        *,
        owner_id: str,
        top_k: int | None = None,
    ) -> list[RetrievedChunk]:
        """Return vector hits for this owner."""
        ...


class HybridRetriever:
    def __init__(
        self,
        *,
        settings: AppSettings,
        lexical: LexicalLeg,
        vector: VectorLeg,
        reranker: RagReranker,
        metrics: PlatformMetrics,
    ) -> None:
        self._settings = settings
        self._lexical = lexical
        self._vector = vector
        self._reranker = reranker
        self._metrics = metrics

    async def retrieve(self, query: str, *, owner_id: str) -> list[RetrievedChunk]:
        started = time.perf_counter()
        try:
            return await self._retrieve(query, owner_id=owner_id)
        finally:
            self._metrics.rag_hybrid_latency.observe(time.perf_counter() - started)

    async def _retrieve(self, query: str, *, owner_id: str) -> list[RetrievedChunk]:
        lexical_raw, vector_raw, degraded = await self._search_both(query, owner_id)
        self._metrics.rag_candidates_before_auth.observe(len(lexical_raw) + len(vector_raw))
        with _tracer.start_as_current_span("ai.authorization_filter"):
            lexical_ok = _authorized(lexical_raw, owner_id, "lexical")
            vector_ok = _authorized(vector_raw, owner_id, "vector")
        authorized = len({_key(chunk) for chunk in lexical_ok + vector_ok})
        self._metrics.rag_candidates_after_auth.observe(authorized)
        if not lexical_ok and not vector_ok:
            outcome = "degraded" if degraded else "empty"
            self._metrics.rag_hybrid_requests.labels(outcome).inc()
            self._metrics.rag_no_results.inc()
            self._metrics.rag_final_chunks.observe(0)
            logger.info(
                "rag.hybrid.empty",
                owner_id=owner_id,
                degraded=degraded,
            )
            return []
        fused = self._fuse(lexical_ok, vector_ok)
        reranked, rerank_fallback = await self._rerank(query, owner_id, fused)
        selected = reranked[: self._settings.rag_top_k]
        ranked = [replace(chunk, final_rank=index) for index, chunk in enumerate(selected, start=1)]
        if not ranked:
            self._metrics.rag_no_results.inc()
            self._metrics.rag_hybrid_requests.labels("empty").inc()
            self._metrics.rag_final_chunks.observe(0)
            return []
        outcome = "degraded" if degraded or rerank_fallback else "success"
        self._metrics.rag_hybrid_requests.labels(outcome).inc()
        self._metrics.rag_final_chunks.observe(len(ranked))
        logger.info(
            "rag.hybrid.completed",
            owner_id=owner_id,
            lexical=len(lexical_ok),
            vector=len(vector_ok),
            final=len(ranked),
            degraded=degraded or rerank_fallback,
            fusion=self._settings.rag_fusion_strategy,
        )
        logger.debug(
            "rag.hybrid.diagnostics",
            owner_id=owner_id,
            results=[_diagnostic(chunk) for chunk in ranked],
        )
        return ranked

    async def _search_both(
        self, query: str, owner_id: str
    ) -> tuple[list[RetrievedChunk], list[RetrievedChunk], bool]:
        lexical_task = self._leg(
            "lexical",
            self._lexical.retrieve(query, owner_id=owner_id),
            owner_id,
        )
        vector_task = self._leg(
            "vector",
            self._vector.retrieve(
                query,
                owner_id=owner_id,
                top_k=self._settings.rag_vector_top_k,
            ),
            owner_id,
        )
        lexical_result, vector_result = await asyncio.gather(lexical_task, vector_task)
        lexical_chunks, lexical_error = lexical_result
        vector_chunks, vector_error = vector_result
        if lexical_error is not None and vector_error is not None:
            self._metrics.rag_hybrid_requests.labels("failed").inc()
            logger.warning(
                "rag.hybrid.unavailable",
                owner_id=owner_id,
                lexical_error=type(lexical_error).__name__,
                vector_error=type(vector_error).__name__,
            )
            raise RagUnavailableError() from vector_error
        return lexical_chunks, vector_chunks, lexical_error is not None or vector_error is not None

    async def _leg(
        self,
        name: str,
        awaitable: Awaitable[list[RetrievedChunk]],
        owner_id: str,
    ) -> tuple[list[RetrievedChunk], Exception | None]:
        started = time.perf_counter()
        counter = (
            self._metrics.rag_lexical_requests
            if name == "lexical"
            else self._metrics.rag_vector_leg_requests
        )
        histogram = self._metrics.rag_vector_leg_latency if name == "vector" else None
        span = "ai.vector_retrieval" if name == "vector" else "ai.lexical_retrieval"
        try:
            with _tracer.start_as_current_span(span):
                chunks = await awaitable
        except Exception as exc:
            counter.labels("failure").inc()
            logger.warning(
                "rag.retrieval.degraded",
                leg=name,
                owner_id=owner_id,
                error_type=type(exc).__name__,
            )
            return [], exc
        finally:
            if histogram is not None:
                histogram.observe(time.perf_counter() - started)
        counter.labels("success" if chunks else "empty").inc()
        return chunks, None

    def _fuse(
        self,
        lexical: list[RetrievedChunk],
        vector: list[RetrievedChunk],
    ) -> list[RetrievedChunk]:
        started = time.perf_counter()
        lists = {"lexical": lexical, "vector": vector}
        try:
            with _tracer.start_as_current_span("ai.result_normalization"):
                with _tracer.start_as_current_span("ai.result_deduplication"):
                    with _tracer.start_as_current_span("ai.result_fusion"):
                        if self._settings.rag_fusion_strategy == "weighted":
                            return weighted_fusion(
                                lists,
                                lexical_weight=self._settings.rag_lexical_weight,
                                vector_weight=self._settings.rag_vector_weight,
                                limit=self._settings.rag_fusion_top_k,
                            )
                        return reciprocal_rank_fusion(
                            lists,
                            k=self._settings.rag_rrf_k,
                            limit=self._settings.rag_fusion_top_k,
                        )
        finally:
            self._metrics.rag_fusion_latency.observe(time.perf_counter() - started)

    async def _rerank(
        self,
        query: str,
        owner_id: str,
        fused: list[RetrievedChunk],
    ) -> tuple[list[RetrievedChunk], bool]:
        if not self._settings.rag_reranking_enabled:
            return fused, False
        pool = fused[: self._settings.rag_rerank_candidate_k]
        started = time.perf_counter()
        try:
            with _tracer.start_as_current_span("ai.reranking"):
                reranked = await asyncio.wait_for(
                    self._reranker.rerank(
                        query,
                        pool,
                        top_k=self._settings.rag_rerank_top_k,
                    ),
                    self._settings.rag_rerank_timeout_seconds,
                )
        except Exception as exc:
            self._metrics.rag_reranker_failures.inc()
            self._metrics.rag_reranker_requests.labels("fallback").inc()
            logger.warning(
                "rag.reranker.degraded",
                owner_id=owner_id,
                error_type=type(exc).__name__,
            )
            return pool, True
        finally:
            self._metrics.rag_reranker_latency.observe(time.perf_counter() - started)
        self._metrics.rag_reranker_requests.labels("success").inc()
        minimum = self._settings.rag_rerank_min_score
        if minimum is not None:
            reranked = [chunk for chunk in reranked if (chunk.rerank_score or 0.0) >= minimum]
        return reranked, False


def _authorized(chunks: list[RetrievedChunk], owner_id: str, source: str) -> list[RetrievedChunk]:
    kept: list[RetrievedChunk] = []
    for rank, chunk in enumerate(chunks, start=1):
        if chunk.owner_id != owner_id:
            continue
        if source == "vector":
            kept.append(
                replace(
                    chunk,
                    vector_rank=rank,
                    vector_score=chunk.score if chunk.vector_score is None else chunk.vector_score,
                    retrieval_sources=_with_source(chunk.retrieval_sources, source),
                )
            )
        else:
            kept.append(
                replace(
                    chunk,
                    lexical_rank=rank,
                    lexical_score=(
                        chunk.lexical_score if chunk.lexical_score is not None else chunk.score
                    ),
                    retrieval_sources=_with_source(chunk.retrieval_sources, source),
                )
            )
    return kept


def _with_source(sources: tuple[str, ...], source: str) -> tuple[str, ...]:
    if source in sources:
        return sources
    return (*sources, source)


def _key(chunk: RetrievedChunk) -> tuple[str, str]:
    return chunk.content_id, chunk.chunk_id


def _diagnostic(chunk: RetrievedChunk) -> dict[str, object]:
    return {
        "chunk_id": chunk.chunk_id,
        "content_id": chunk.content_id,
        "sources": list(chunk.retrieval_sources),
        "lexical_rank": chunk.lexical_rank,
        "vector_rank": chunk.vector_rank,
        "fusion_score": chunk.fusion_score,
        "rerank_score": chunk.rerank_score,
        "final_rank": chunk.final_rank,
    }
