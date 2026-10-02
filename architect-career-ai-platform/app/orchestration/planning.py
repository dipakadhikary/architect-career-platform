"""Planning extension point."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any


class Planner(ABC):
    @abstractmethod
    async def plan(self, goal: str, context: dict[str, Any]) -> list[dict[str, Any]]:
        raise NotImplementedError
