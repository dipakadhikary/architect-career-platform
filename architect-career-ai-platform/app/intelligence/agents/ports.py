"""Agent execution port (abstraction only)."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any


@dataclass(slots=True, frozen=True)
class AgentRequest:
    goal: str
    context: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class AgentResponse:
    result: str
    steps: list[dict[str, Any]]


class AgentPort(ABC):
    @abstractmethod
    async def run(self, request: AgentRequest) -> AgentResponse:
        raise NotImplementedError
