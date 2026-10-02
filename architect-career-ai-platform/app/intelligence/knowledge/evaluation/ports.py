"""Knowledge evaluation ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any

from app.intelligence.knowledge.models import RetrievalHit


@dataclass(slots=True, frozen=True)
class RagEvaluationRequest:
    query: str
    answer: str
    hits: list[RetrievalHit] = field(default_factory=list)
    criteria: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class RagEvaluationResult:
    score: float
    details: dict[str, Any] = field(default_factory=dict)


class RagEvaluationPort(ABC):
    @abstractmethod
    async def evaluate(self, request: RagEvaluationRequest) -> RagEvaluationResult:
        raise NotImplementedError
