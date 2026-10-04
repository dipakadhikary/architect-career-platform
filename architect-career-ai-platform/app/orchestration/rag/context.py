"""Turn authorized chunks into model-readable context without cutting a chunk in half."""

from __future__ import annotations

from app.orchestration.rag.models import RetrievedChunk
from app.shared.config.settings import AppSettings


class ContextBuilder:
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings

    def build(self, chunks: list[RetrievedChunk]) -> tuple[str, list[RetrievedChunk]]:
        selected: list[RetrievedChunk] = []
        seen: set[str] = set()
        used = 0
        limit = self._settings.rag_max_context_characters
        ordered = sorted(chunks, key=lambda item: item.score, reverse=True)
        for chunk in ordered:
            key = chunk.content.strip()
            if not key or key in seen or chunk.chunk_id in seen:
                continue
            if selected and used + len(chunk.content) > limit:
                break
            selected.append(chunk)
            seen.add(key)
            seen.add(chunk.chunk_id)
            used += len(chunk.content)
        blocks = [_format_source(index, chunk) for index, chunk in enumerate(selected, start=1)]
        return "\n\n".join(blocks), selected


def _format_source(index: int, chunk: RetrievedChunk) -> str:
    path = chunk.path or chunk.title
    return (
        f"SOURCE {index}\n"
        f"Title: {chunk.title}\n"
        f"Path: {path}\n"
        f"Section: {chunk.section}\n"
        f"Content-Type: {chunk.content_type}\n"
        f"\nContent:\n{chunk.content}"
    )
