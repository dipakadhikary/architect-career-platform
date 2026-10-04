"""Retrieval models. These are not vector-database responses."""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(slots=True, frozen=True)
class RetrievedChunk:
    chunk_id: str
    content_id: str
    content: str
    score: float
    title: str
    section: str
    content_type: str
    source_url: str
    path: str
    topic_id: str | None
    owner_id: str
    lexical_score: float | None = None
    vector_score: float | None = None
    lexical_rank: int | None = None
    vector_rank: int | None = None
    fusion_score: float | None = None
    rerank_score: float | None = None
    final_score: float | None = None
    final_rank: int | None = None
    retrieval_sources: tuple[str, ...] = ()


def ordered_for_context(chunks: list[RetrievedChunk]) -> list[RetrievedChunk]:
    """Keep an explicit hybrid rank. Otherwise fall back to similarity score."""
    if chunks and all(chunk.final_rank is not None for chunk in chunks):
        return sorted(chunks, key=lambda item: item.final_rank or 0)
    return sorted(chunks, key=lambda item: item.score, reverse=True)


def outranks(candidate: RetrievedChunk, current: RetrievedChunk) -> bool:
    if candidate.final_rank is not None and current.final_rank is not None:
        return candidate.final_rank < current.final_rank
    return candidate.score > current.score
