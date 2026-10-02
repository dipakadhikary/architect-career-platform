"""Orchestration evaluation extension point."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any


class OrchestrationEvaluator(ABC):
    @abstractmethod
    async def evaluate(self, run_id: str, payload: dict[str, Any]) -> dict[str, Any]:
        raise NotImplementedError
