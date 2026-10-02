"""Tool registry and executable tool ports."""

from __future__ import annotations

from abc import ABC, abstractmethod

from app.intelligence.agentic.models import ToolSpec
from app.intelligence.tools.ports import ToolRequest, ToolResponse


class ExecutableTool(ABC):
    @property
    @abstractmethod
    def spec(self) -> ToolSpec:
        raise NotImplementedError

    @abstractmethod
    async def execute(self, request: ToolRequest) -> ToolResponse:
        raise NotImplementedError


class ToolRegistryPort(ABC):
    @abstractmethod
    def register(self, tool: ExecutableTool) -> None:
        raise NotImplementedError

    @abstractmethod
    def get(self, name: str) -> ExecutableTool:
        raise NotImplementedError

    @abstractmethod
    def list(self) -> list[ToolSpec]:
        raise NotImplementedError

    @abstractmethod
    async def execute(self, request: ToolRequest) -> ToolResponse:
        raise NotImplementedError
