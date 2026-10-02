"""Planning capability port."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any

from app.intelligence.agentic.models import ExecutionPlan


class PlannerPort(ABC):
    @abstractmethod
    async def plan(self, goal: str, context: dict[str, Any] | None = None) -> ExecutionPlan:
        raise NotImplementedError
