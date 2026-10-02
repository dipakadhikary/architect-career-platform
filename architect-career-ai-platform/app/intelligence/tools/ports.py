"""Tool execution port."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any


@dataclass(slots=True, frozen=True)
class ToolRequest:
    name: str
    arguments: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class ToolResponse:
    name: str
    output: dict[str, Any]


class ToolPort(ABC):
    @abstractmethod
    async def execute(self, request: ToolRequest) -> ToolResponse:
        raise NotImplementedError
