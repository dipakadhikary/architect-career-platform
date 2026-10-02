"""Reusable workflow engine port."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any, Protocol

from app.orchestration.workflows import WorkflowRequest, WorkflowResult


class WorkflowDefinition(Protocol):
    @property
    def name(self) -> str: ...

    async def run(self, payload: dict[str, Any]) -> dict[str, Any]: ...


class WorkflowEnginePort(ABC):
    @abstractmethod
    def register(self, workflow: WorkflowDefinition) -> None:
        raise NotImplementedError

    @abstractmethod
    def list_names(self) -> list[str]:
        raise NotImplementedError

    @abstractmethod
    async def execute(self, request: WorkflowRequest) -> WorkflowResult:
        raise NotImplementedError
