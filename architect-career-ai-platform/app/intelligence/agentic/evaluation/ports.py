"""Agentic evaluation framework port."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any

from app.intelligence.agentic.models import AgenticEvaluation, RetrievalCapabilityHit


@dataclass(slots=True, frozen=True)
class AgenticEvaluationRequest:
    query: str
    answer: str
    context_hits: list[RetrievalCapabilityHit] = field(default_factory=list)
    latency_ms: float = 0.0
    prompt_tokens: int = 0
    completion_tokens: int = 0
    estimated_cost_usd: float = 0.0
    prompt_version: str = "v1"
    metadata: dict[str, Any] = field(default_factory=dict)


class AgenticEvaluationPort(ABC):
    @abstractmethod
    async def evaluate(self, request: AgenticEvaluationRequest) -> AgenticEvaluation:
        raise NotImplementedError
