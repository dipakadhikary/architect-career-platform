"""Reusable LangGraph orchestration ports (engine only, no business graphs)."""

from __future__ import annotations

from abc import ABC, abstractmethod
from collections.abc import Awaitable, Callable
from typing import Any

from app.intelligence.agentic.models import GraphKind, GraphRunRequest, GraphRunResult

NodeFn = Callable[[dict[str, Any]], Awaitable[dict[str, Any]]]


class GraphDefinition:
    def __init__(
        self,
        *,
        name: str,
        kind: GraphKind,
        nodes: dict[str, NodeFn],
        edges: list[tuple[str, str]],
        conditional_edges: dict[str, Callable[[dict[str, Any]], str]] | None = None,
        entry: str,
        finish: str | None = None,
    ) -> None:
        self.name = name
        self.kind = kind
        self.nodes = nodes
        self.edges = edges
        self.conditional_edges = conditional_edges or {}
        self.entry = entry
        self.finish = finish


class GraphEnginePort(ABC):
    @abstractmethod
    def register(self, definition: GraphDefinition) -> None:
        raise NotImplementedError

    @abstractmethod
    async def invoke(self, request: GraphRunRequest) -> GraphRunResult:
        raise NotImplementedError

    @abstractmethod
    async def resume(self, thread_id: str, approval: bool = True) -> GraphRunResult:
        raise NotImplementedError

    @abstractmethod
    async def cancel(self, thread_id: str) -> GraphRunResult:
        raise NotImplementedError
