"""Retriever port for vector/search backends."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any


@dataclass(slots=True, frozen=True)
class RetrievalQuery:
    text: str
    top_k: int = 5
    filters: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class RetrievedDocument:
    id: str
    content: str
    score: float
    metadata: dict[str, Any] = field(default_factory=dict)


class RetrieverPort(ABC):
    @abstractmethod
    async def retrieve(self, query: RetrievalQuery) -> list[RetrievedDocument]:
        raise NotImplementedError
