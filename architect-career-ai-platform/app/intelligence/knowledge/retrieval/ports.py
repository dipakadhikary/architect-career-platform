"""Retrieval pipeline ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any

from app.intelligence.knowledge.models import RetrievalHit, RetrievalMode


@dataclass(slots=True, frozen=True)
class KnowledgeRetrievalQuery:
    text: str
    top_k: int = 10
    score_threshold: float | None = None
    mode: RetrievalMode = RetrievalMode.DENSE
    filters: dict[str, Any] = field(default_factory=dict)


class KnowledgeRetrieverPort(ABC):
    @abstractmethod
    async def retrieve(self, query: KnowledgeRetrievalQuery) -> list[RetrievalHit]:
        raise NotImplementedError
