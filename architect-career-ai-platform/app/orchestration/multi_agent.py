"""Multi-agent execution extension point."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any


class MultiAgentExecutor(ABC):
    @abstractmethod
    async def run(self, goal: str, context: dict[str, Any]) -> dict[str, Any]:
        raise NotImplementedError
