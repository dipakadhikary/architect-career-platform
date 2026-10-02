"""Embedding provider port."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass


@dataclass(slots=True, frozen=True)
class EmbeddingRequest:
    texts: list[str]
    model: str | None = None


@dataclass(slots=True, frozen=True)
class EmbeddingResponse:
    vectors: list[list[float]]
    model: str
    dimensions: int


class EmbeddingPort(ABC):
    @abstractmethod
    async def embed(self, request: EmbeddingRequest) -> EmbeddingResponse:
        raise NotImplementedError
