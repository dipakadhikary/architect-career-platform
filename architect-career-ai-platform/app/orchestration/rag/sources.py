"""Citations come from retrieval metadata. Model text cannot add a source."""

from __future__ import annotations

from app.intelligence.assistant.models import AnswerSource
from app.orchestration.rag.models import RetrievedChunk, ordered_for_context, outranks


def sources_from_chunks(chunks: list[RetrievedChunk], *, limit: int) -> list[AnswerSource]:
    best: dict[str, RetrievedChunk] = {}
    for chunk in chunks:
        current = best.get(chunk.content_id)
        if current is None or outranks(chunk, current):
            best[chunk.content_id] = chunk
    ordered = ordered_for_context(list(best.values()))
    return [_source(chunk) for chunk in ordered[:limit]]


def _source(chunk: RetrievedChunk) -> AnswerSource:
    return AnswerSource(
        content_id=chunk.content_id,
        topic_id=chunk.topic_id,
        title=chunk.title,
        content_type=chunk.content_type,
        section=chunk.section,
        path=chunk.path or chunk.title,
        url=chunk.source_url,
        chunk_id=chunk.chunk_id,
        score=chunk.score,
    )
