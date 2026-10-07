"""Fuse lexical and vector ranks. Raw scores from the two legs are not compared."""

from __future__ import annotations

from dataclasses import replace

from app.orchestration.rag.models import RetrievedChunk

_SOURCE_ORDER = ("lexical", "vector")


def reciprocal_rank_fusion(
    lists: dict[str, list[RetrievedChunk]],
    *,
    k: int,
    limit: int,
) -> list[RetrievedChunk]:
    """RRF(chunk) = sum 1 / (k + rank) using 1-based ranks."""
    merged, ranks = _merge_lists(lists)
    scored: list[tuple[float, RetrievedChunk]] = []
    for key, chunk in merged.items():
        fusion = sum(1.0 / (k + rank) for rank in ranks[key].values())
        scored.append((fusion, chunk))
    scored.sort(key=lambda item: (-item[0], item[1].content_id, item[1].chunk_id))
    return [_with_fusion(chunk, fusion) for fusion, chunk in scored[:limit]]


def weighted_fusion(
    lists: dict[str, list[RetrievedChunk]],
    *,
    lexical_weight: float,
    vector_weight: float,
    limit: int,
) -> list[RetrievedChunk]:
    """Min-max normalize each leg, then apply weights.

    A chunk missing from a leg contributes 0 for that leg. A leg whose scores are
    all equal normalizes every member to 1. This is not the default strategy.
    """
    merged, _ranks = _merge_lists(lists)
    lexical_norm = _minmax(lists.get("lexical", []), "lexical_score")
    vector_norm = _minmax(lists.get("vector", []), "vector_score")
    scored: list[tuple[float, RetrievedChunk]] = []
    for key, chunk in merged.items():
        fusion = lexical_weight * lexical_norm.get(key, 0.0)
        fusion += vector_weight * vector_norm.get(key, 0.0)
        scored.append((fusion, chunk))
    scored.sort(key=lambda item: (-item[0], item[1].content_id, item[1].chunk_id))
    return [_with_fusion(chunk, fusion) for fusion, chunk in scored[:limit]]


def _merge_lists(
    lists: dict[str, list[RetrievedChunk]],
) -> tuple[dict[tuple[str, str], RetrievedChunk], dict[tuple[str, str], dict[str, int]]]:
    merged: dict[tuple[str, str], RetrievedChunk] = {}
    ranks: dict[tuple[str, str], dict[str, int]] = {}
    for source, chunks in lists.items():
        for rank, chunk in enumerate(chunks, start=1):
            key = (chunk.content_id, chunk.chunk_id)
            current = merged.get(key)
            merged[key] = chunk if current is None else _merge(current, chunk)
            ranks.setdefault(key, {}).setdefault(source, rank)
    return merged, ranks


def _merge(left: RetrievedChunk, right: RetrievedChunk) -> RetrievedChunk:
    vector_score = left.vector_score if left.vector_score is not None else right.vector_score
    lexical_score = left.lexical_score if left.lexical_score is not None else right.lexical_score
    public = vector_score if vector_score is not None else lexical_score
    if public is None:
        public = left.score
    return replace(
        left,
        score=public,
        lexical_score=lexical_score,
        vector_score=vector_score,
        lexical_rank=left.lexical_rank if left.lexical_rank is not None else right.lexical_rank,
        vector_rank=left.vector_rank if left.vector_rank is not None else right.vector_rank,
        retrieval_sources=_sources(left.retrieval_sources, right.retrieval_sources),
    )


def _sources(*groups: tuple[str, ...]) -> tuple[str, ...]:
    found = {name for group in groups for name in group}
    return tuple(name for name in _SOURCE_ORDER if name in found)


def _with_fusion(chunk: RetrievedChunk, fusion: float) -> RetrievedChunk:
    return replace(chunk, fusion_score=fusion, final_score=fusion)


def _minmax(chunks: list[RetrievedChunk], attribute: str) -> dict[tuple[str, str], float]:
    keys: list[tuple[str, str]] = []
    values: list[float] = []
    for chunk in chunks:
        raw = getattr(chunk, attribute)
        if raw is None:
            raw = chunk.score
        keys.append((chunk.content_id, chunk.chunk_id))
        values.append(float(raw))
    if not values:
        return {}
    low = min(values)
    high = max(values)
    normalized: dict[tuple[str, str], float] = {}
    for key, value in zip(keys, values, strict=True):
        normalized[key] = 1.0 if high == low else (value - low) / (high - low)
    return normalized
