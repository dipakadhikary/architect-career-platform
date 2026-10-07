"""Explicit tool catalog. Unknown names cannot run."""

from __future__ import annotations

from dataclasses import dataclass
from typing import Any, Protocol

from pydantic import BaseModel

from app.orchestration.agent.models import (
    FORBIDDEN_TOOL_NAMES,
    RiskLevel,
    ToolPermission,
)
from app.shared.exceptions import ValidationFailedError


@dataclass(slots=True, frozen=True)
class ToolContext:
    owner_id: str
    execution_id: str
    conversation_id: str | None


@dataclass(slots=True, frozen=True)
class ToolSpec:
    name: str
    version: str
    description: str
    risk: RiskLevel
    permission: ToolPermission
    input_model: type[BaseModel]
    read_only: bool = True
    idempotent: bool = True


class AgentTool(Protocol):
    spec: ToolSpec

    async def execute(self, context: ToolContext, arguments: dict[str, Any]) -> dict[str, Any]:
        """Run one registered operation for the authenticated owner."""


class ToolRegistry:
    """Only tools passed to register can execute. Nothing is imported from model text."""

    def __init__(self, *, allow_elevated: bool = False) -> None:
        self._tools: dict[str, AgentTool] = {}
        self._allow_elevated = allow_elevated

    def register(self, tool: AgentTool) -> None:
        name = tool.spec.name
        if name in FORBIDDEN_TOOL_NAMES or not name.replace("_", "").isalnum():
            raise ValidationFailedError(f"Tool '{name}' cannot be registered")
        if not tool.spec.read_only and not self._allow_elevated:
            raise ValidationFailedError(f"Tool '{name}' is not read-only")
        if name in self._tools:
            raise ValidationFailedError(f"Tool '{name}' is already registered")
        self._tools[name] = tool

    def get(self, name: str) -> AgentTool | None:
        return self._tools.get(name)

    def visible(self) -> list[ToolSpec]:
        """Tools a normal caller may be told about. Elevated tools stay hidden."""
        return [
            tool.spec
            for tool in self._tools.values()
            if tool.spec.read_only and tool.spec.risk == RiskLevel.LOW
        ]
