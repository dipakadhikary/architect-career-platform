"""Chunking strategies (Strategy Pattern)."""

from __future__ import annotations

import re
import uuid

from app.intelligence.knowledge.chunking.ports import (
    ChunkerPort,
    ChunkerRegistryPort,
    ChunkingOptions,
)
from app.intelligence.knowledge.models import ChunkingStrategy, TextChunk
from app.shared.exceptions import ValidationFailedError


def _make_chunk(text: str, index: int, start: int, end: int) -> TextChunk:
    return TextChunk(
        chunk_id=str(uuid.uuid4()),
        text=text,
        index=index,
        start_offset=start,
        end_offset=end,
    )


class RecursiveChunker(ChunkerPort):
    @property
    def strategy(self) -> ChunkingStrategy:
        return ChunkingStrategy.RECURSIVE

    async def chunk(self, text: str, options: ChunkingOptions) -> list[TextChunk]:
        return _recursive_split(text, options)


class TokenChunker(ChunkerPort):
    @property
    def strategy(self) -> ChunkingStrategy:
        return ChunkingStrategy.TOKEN

    async def chunk(self, text: str, options: ChunkingOptions) -> list[TextChunk]:
        tokens = text.split()
        size = max(options.chunk_size, 1)
        overlap = min(max(options.chunk_overlap, 0), size - 1) if size > 1 else 0
        chunks: list[TextChunk] = []
        start = 0
        index = 0
        while start < len(tokens):
            end = min(start + size, len(tokens))
            piece = " ".join(tokens[start:end])
            chunks.append(_make_chunk(piece, index, start, end))
            index += 1
            if end >= len(tokens):
                break
            start = max(end - overlap, start + 1)
        return chunks


class SentenceChunker(ChunkerPort):
    @property
    def strategy(self) -> ChunkingStrategy:
        return ChunkingStrategy.SENTENCE

    async def chunk(self, text: str, options: ChunkingOptions) -> list[TextChunk]:
        sentences = [s.strip() for s in re.split(r"(?<=[.!?])\s+", text) if s.strip()]
        size = max(options.chunk_size, 1)
        overlap = min(max(options.chunk_overlap, 0), size - 1) if size > 1 else 0
        chunks: list[TextChunk] = []
        start = 0
        index = 0
        while start < len(sentences):
            end = min(start + size, len(sentences))
            piece = " ".join(sentences[start:end])
            chunks.append(_make_chunk(piece, index, start, end))
            index += 1
            if end >= len(sentences):
                break
            start = max(end - overlap, start + 1)
        return chunks


class MarkdownChunker(ChunkerPort):
    @property
    def strategy(self) -> ChunkingStrategy:
        return ChunkingStrategy.MARKDOWN

    async def chunk(self, text: str, options: ChunkingOptions) -> list[TextChunk]:
        sections = re.split(r"(?=^#{1,6}\s)", text, flags=re.M)
        sections = [section.strip() for section in sections if section.strip()]
        if not sections:
            return await RecursiveChunker().chunk(text, options)
        chunks: list[TextChunk] = []
        for index, section in enumerate(sections):
            if len(section) <= options.chunk_size:
                chunks.append(_make_chunk(section, index, 0, len(section)))
            else:
                nested = await RecursiveChunker().chunk(section, options)
                for item in nested:
                    chunks.append(
                        _make_chunk(item.text, len(chunks), item.start_offset, item.end_offset)
                    )
        return chunks


class SemanticChunker(ChunkerPort):
    """Semantic chunking extension point — falls back to recursive until configured."""

    @property
    def strategy(self) -> ChunkingStrategy:
        return ChunkingStrategy.SEMANTIC

    async def chunk(self, text: str, options: ChunkingOptions) -> list[TextChunk]:
        return await RecursiveChunker().chunk(text, options)


class ChunkerRegistry(ChunkerRegistryPort):
    def __init__(self, chunkers: list[ChunkerPort]) -> None:
        self._chunkers = {chunker.strategy: chunker for chunker in chunkers}

    def get(self, strategy: ChunkingStrategy) -> ChunkerPort:
        chunker = self._chunkers.get(strategy)
        if chunker is None:
            raise ValidationFailedError(f"Unsupported chunking strategy: {strategy}")
        return chunker


def _recursive_split(text: str, options: ChunkingOptions) -> list[TextChunk]:
    size = max(options.chunk_size, 1)
    overlap = min(max(options.chunk_overlap, 0), size - 1) if size > 1 else 0
    if len(text) <= size:
        return [_make_chunk(text, 0, 0, len(text))] if text else []

    pieces = _split_with_separators(text, list(options.separators), size)
    chunks: list[TextChunk] = []
    cursor = 0
    for index, piece in enumerate(pieces):
        start = text.find(piece, cursor)
        if start < 0:
            start = cursor
        end = start + len(piece)
        chunks.append(_make_chunk(piece, index, start, end))
        cursor = max(end - overlap, start + 1)
    return chunks


def _split_with_separators(text: str, separators: list[str], size: int) -> list[str]:
    if not text:
        return []
    if len(text) <= size:
        return [text]
    if not separators:
        return [text[i : i + size] for i in range(0, len(text), size)]

    separator = separators[0]
    rest = separators[1:]
    parts = text.split(separator) if separator else list(text)
    merged: list[str] = []
    buffer = ""
    for part in parts:
        candidate = part if not buffer else f"{buffer}{separator}{part}"
        if len(candidate) <= size:
            buffer = candidate
            continue
        if buffer:
            merged.append(buffer)
        if len(part) > size:
            merged.extend(_split_with_separators(part, rest, size))
            buffer = ""
        else:
            buffer = part
    if buffer:
        merged.append(buffer)
    return merged
