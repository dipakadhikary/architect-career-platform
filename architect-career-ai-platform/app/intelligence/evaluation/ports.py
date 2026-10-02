"""Evaluation port."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import Any


@dataclass(slots=True, frozen=True)
class EvaluationRequest:
    input_text: str
    output_text: str
    criteria: dict[str, Any]


@dataclass(slots=True, frozen=True)
class EvaluationResult:
    score: float
    details: dict[str, Any]


class EvaluationPort(ABC):
    @abstractmethod
    async def evaluate(self, request: EvaluationRequest) -> EvaluationResult:
        raise NotImplementedError
