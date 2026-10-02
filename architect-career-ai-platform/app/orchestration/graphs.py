"""LangGraph extension point."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any


class GraphOrchestrator(ABC):
    @abstractmethod
    async def invoke(self, graph_name: str, state: dict[str, Any]) -> dict[str, Any]:
        raise NotImplementedError
