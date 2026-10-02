"""Chunking strategy ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass

from app.intelligence.knowledge.models import ChunkingStrategy, TextChunk


@dataclass(slots=True, frozen=True)
class ChunkingOptions:
    strategy: ChunkingStrategy = ChunkingStrategy.RECURSIVE
    chunk_size: int = 800
    chunk_overlap: int = 120
    separators: tuple[str, ...] = ("\n\n", "\n", ". ", " ", "")


class ChunkerPort(ABC):
    @property
    @abstractmethod
    def strategy(self) -> ChunkingStrategy:
        raise NotImplementedError

    @abstractmethod
    async def chunk(self, text: str, options: ChunkingOptions) -> list[TextChunk]:
        raise NotImplementedError


class ChunkerRegistryPort(ABC):
    @abstractmethod
    def get(self, strategy: ChunkingStrategy) -> ChunkerPort:
        raise NotImplementedError
