"""Workflow orchestration extension point."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any


@dataclass(slots=True, frozen=True)
class WorkflowRequest:
    name: str
    payload: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class WorkflowResult:
    name: str
    status: str
    output: dict[str, Any] = field(default_factory=dict)


class WorkflowOrchestrator(ABC):
    @abstractmethod
    async def execute(self, request: WorkflowRequest) -> WorkflowResult:
        raise NotImplementedError
