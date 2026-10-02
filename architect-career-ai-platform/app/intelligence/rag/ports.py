"""RAG pipeline port (abstraction only)."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any


@dataclass(slots=True, frozen=True)
class RagRequest:
    query: str
    top_k: int = 5
    metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class RagResponse:
    answer: str
    sources: list[dict[str, Any]]


class RagPort(ABC):
    @abstractmethod
    async def run(self, request: RagRequest) -> RagResponse:
        raise NotImplementedError
