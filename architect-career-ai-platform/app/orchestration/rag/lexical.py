"""Lexical retrieval over indexed chunks. This is not a second ACOS product search."""

from __future__ import annotations

import asyncio
import time
from dataclasses import replace

from app.infrastructure.observability.otel import get_tracer
from app.intelligence.knowledge.models import VectorRecord
from app.intelligence.knowledge.vectorstore.ports import VectorStorePort
from app.orchestration.rag.models import RetrievedChunk
from app.orchestration.rag.retriever import chunk_from_record
from app.shared.config.settings import AppSettings
from app.shared.logging.setup import get_logger
from app.shared.observability.metrics import PlatformMetrics

logger = get_logger(__name__)
_tracer = get_tracer("acos.ai.rag")


def query_tokens(query: str) -> list[str]:
    """Split on whitespace and keep technical tokens such as class and property names."""
    tokens: list[str] = []
    for raw in query.split():
        token = raw.strip(".,;:!?\"'()[]{}")
        if token:
            tokens.append(token)
    return tokens


def lexical_rank_score(query: str, text: str) -> float:
    """Rank a chunk without stemming away identifiers.

    A contiguous phrase and a case-sensitive token both outrank a loose word match.
    Scores are only compared inside the lexical list. They are not mixed with cosine.
    """
    tokens = query_tokens(query)
    if not tokens or not text:
        return 0.0
    folded = text.casefold()
    present = sum(1 for token in tokens if token.casefold() in folded)
    if present == 0:
        return 0.0
    exact = sum(1 for token in tokens if token in text)
    phrase = 1.0 if query.casefold() in folded else 0.0
    return phrase * 2.0 + (present / len(tokens)) + 0.25 * (exact / len(tokens))


class IndexedLexicalRetriever:
    """Search the Phase 3 chunk index. Owner filtering happens before the result is returned."""

    def __init__(
        self,
        settings: AppSettings,
        vector_store: VectorStorePort,
        metrics: PlatformMetrics,
    ) -> None:
        self._settings = settings
        self._vector_store = vector_store
        self._metrics = metrics

    async def retrieve(self, query: str, *, owner_id: str) -> list[RetrievedChunk]:
        started = time.perf_counter()
        pool = min(max(self._settings.rag_lexical_top_k * 5, 20), 100)
        try:
            with _tracer.start_as_current_span("ai.lexical_retrieval"):
                records = await asyncio.wait_for(
                    self._vector_store.keyword_search(
                        query,
                        top_k=pool,
                        filters={"owner_id": owner_id},
                    ),
                    self._settings.rag_lexical_timeout_seconds,
                )
        finally:
            self._metrics.rag_lexical_latency.observe(time.perf_counter() - started)
        visible = [
            record
            for record in records
            if str((record.metadata or {}).get("owner_id") or "") == owner_id
        ]
        scored: list[tuple[float, VectorRecord]] = []
        for record in visible:
            score = lexical_rank_score(query, record.text)
            if score > 0:
                scored.append((score, record))
        scored.sort(key=lambda item: (-item[0], item[1].document_id, item[1].id))
        limited = scored[: self._settings.rag_lexical_top_k]
        return [
            _annotate(chunk_from_record(record), score=score, rank=rank)
            for rank, (score, record) in enumerate(limited, start=1)
        ]


def _annotate(chunk: RetrievedChunk, *, score: float, rank: int) -> RetrievedChunk:
    return replace(
        chunk,
        score=score,
        lexical_score=score,
        lexical_rank=rank,
        retrieval_sources=("lexical",),
    )
