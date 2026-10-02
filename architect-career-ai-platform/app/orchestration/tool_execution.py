"""Tool execution orchestration extension point."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any


class ToolExecutionOrchestrator(ABC):
    @abstractmethod
    async def execute(self, tool_name: str, arguments: dict[str, Any]) -> dict[str, Any]:
        raise NotImplementedError
