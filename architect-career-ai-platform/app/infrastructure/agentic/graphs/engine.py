"""Reusable LangGraph orchestration engine (no business workflows)."""

from __future__ import annotations

import asyncio
import uuid
from typing import Any, TypedDict

from langgraph.graph import END, START, StateGraph

from app.intelligence.agentic.graphs.ports import GraphDefinition, GraphEnginePort
from app.intelligence.agentic.models import (
    GraphKind,
    GraphRunRequest,
    GraphRunResult,
    WorkflowStatus,
)
from app.shared.exceptions import NotFoundError, ValidationFailedError


class _GraphState(TypedDict, total=False):
    payload: dict[str, Any]
    steps: list[str]
    cancelled: bool
    waiting_approval: bool
    approved: bool
    failure_reason: str


class LangGraphEngine(GraphEnginePort):
    def __init__(self) -> None:
        self._definitions: dict[str, GraphDefinition] = {}
        self._checkpoints: dict[str, dict[str, Any]] = {}
        self._cancelled: set[str] = set()

    def register(self, definition: GraphDefinition) -> None:
        self._definitions[definition.name] = definition

    async def invoke(self, request: GraphRunRequest) -> GraphRunResult:
        if request.cancel and request.thread_id:
            return await self.cancel(request.thread_id)
        if request.resume and request.thread_id:
            return await self.resume(request.thread_id, approval=True)

        definition = self._definitions.get(request.graph_name)
        if definition is None:
            raise NotFoundError(f"Graph not registered: {request.graph_name}")

        thread_id = request.thread_id or str(uuid.uuid4())
        if request.require_approval:
            state = {
                "payload": dict(request.state),
                "steps": ["awaiting_approval"],
                "waiting_approval": True,
                "approved": False,
                "cancelled": False,
            }
            self._checkpoints[thread_id] = {
                "graph_name": request.graph_name,
                "kind": request.kind.value,
                "state": state,
                "retry_count": 0,
            }
            return GraphRunResult(
                graph_name=request.graph_name,
                status=WorkflowStatus.WAITING_APPROVAL,
                state=state,
                thread_id=thread_id,
                steps=["awaiting_approval"],
            )

        return await self._run(definition, request.state, thread_id, retry_count=0)

    async def resume(self, thread_id: str, approval: bool = True) -> GraphRunResult:
        checkpoint = self._checkpoints.get(thread_id)
        if checkpoint is None:
            raise NotFoundError(f"No checkpoint for thread: {thread_id}")
        if not approval:
            return GraphRunResult(
                graph_name=checkpoint["graph_name"],
                status=WorkflowStatus.CANCELLED,
                state=checkpoint["state"],
                thread_id=thread_id,
                steps=list(checkpoint["state"].get("steps") or []),
                failure_reason="approval_denied",
                retry_count=int(checkpoint.get("retry_count") or 0),
            )
        definition = self._definitions[checkpoint["graph_name"]]
        payload = dict(checkpoint["state"].get("payload") or {})
        retry = int(checkpoint.get("retry_count") or 0) + 1
        return await self._run(definition, payload, thread_id, retry_count=retry)

    async def cancel(self, thread_id: str) -> GraphRunResult:
        self._cancelled.add(thread_id)
        checkpoint = self._checkpoints.get(thread_id, {})
        state = dict(checkpoint.get("state") or {"payload": {}, "steps": ["cancelled"]})
        state["cancelled"] = True
        return GraphRunResult(
            graph_name=str(checkpoint.get("graph_name") or "unknown"),
            status=WorkflowStatus.CANCELLED,
            state=state,
            thread_id=thread_id,
            steps=list(state.get("steps") or ["cancelled"]),
            failure_reason="cancelled",
            retry_count=int(checkpoint.get("retry_count") or 0),
        )

    async def _run(
        self,
        definition: GraphDefinition,
        payload: dict[str, Any],
        thread_id: str,
        *,
        retry_count: int,
    ) -> GraphRunResult:
        if thread_id in self._cancelled:
            return await self.cancel(thread_id)

        if definition.kind == GraphKind.PARALLEL:
            return await self._run_parallel(definition, payload, thread_id, retry_count)

        graph = StateGraph(_GraphState)
        for name, fn in definition.nodes.items():
            graph.add_node(name, _wrap_node(name, fn, thread_id, self._cancelled))

        graph.add_edge(START, definition.entry)
        if definition.kind == GraphKind.CONDITIONAL and definition.conditional_edges:
            for source, router in definition.conditional_edges.items():
                targets = {target: target for _src, target in definition.edges if _src == source}
                targets[END] = END
                graph.add_conditional_edges(source, router, targets)
            for source, target in definition.edges:
                if source in definition.conditional_edges:
                    continue
                graph.add_edge(source, target if target != "__end__" else END)
        else:
            for source, target in definition.edges:
                graph.add_edge(source, target if target != "__end__" else END)

        finish = definition.finish or next(
            (target for _src, target in definition.edges if target == "__end__"),
            None,
        )
        if finish is None and definition.kind == GraphKind.SEQUENTIAL:
            # ensure terminal edge exists
            pass

        compiled = graph.compile()
        try:
            result = await compiled.ainvoke(
                {"payload": payload, "steps": [], "cancelled": False, "approved": True}
            )
        except Exception as exc:
            failure = GraphRunResult(
                graph_name=definition.name,
                status=WorkflowStatus.FAILED,
                state={"payload": payload, "steps": []},
                thread_id=thread_id,
                failure_reason=str(exc),
                retry_count=retry_count,
            )
            self._checkpoints[thread_id] = {
                "graph_name": definition.name,
                "kind": definition.kind.value,
                "state": failure.state,
                "retry_count": retry_count,
            }
            return failure

        steps = list(result.get("steps") or [])
        status = WorkflowStatus.CANCELLED if result.get("cancelled") else WorkflowStatus.SUCCEEDED
        run = GraphRunResult(
            graph_name=definition.name,
            status=status,
            state=dict(result),
            thread_id=thread_id,
            steps=steps,
            retry_count=retry_count,
        )
        self._checkpoints[thread_id] = {
            "graph_name": definition.name,
            "kind": definition.kind.value,
            "state": run.state,
            "retry_count": retry_count,
        }
        return run

    async def _run_parallel(
        self,
        definition: GraphDefinition,
        payload: dict[str, Any],
        thread_id: str,
        retry_count: int,
    ) -> GraphRunResult:
        async def _call(name: str, fn):
            partial = await fn({"payload": dict(payload), "steps": []})
            return name, partial

        results = await asyncio.gather(*[_call(name, fn) for name, fn in definition.nodes.items()])
        merged: dict[str, Any] = {"payload": dict(payload), "parallel": {}}
        steps: list[str] = []
        for name, partial in results:
            steps.append(name)
            merged["parallel"][name] = partial.get("payload", partial)
            merged["payload"].update(partial.get("payload") or {})
        merged["steps"] = steps
        run = GraphRunResult(
            graph_name=definition.name,
            status=WorkflowStatus.SUCCEEDED,
            state=merged,
            thread_id=thread_id,
            steps=steps,
            retry_count=retry_count,
        )
        self._checkpoints[thread_id] = {
            "graph_name": definition.name,
            "kind": definition.kind.value,
            "state": run.state,
            "retry_count": retry_count,
        }
        return run


def _wrap_node(name: str, fn, thread_id: str, cancelled: set[str]):
    async def _node(state: _GraphState) -> _GraphState:
        if thread_id in cancelled:
            return {
                **state,
                "cancelled": True,
                "steps": [*list(state.get("steps") or []), name],
            }
        updated = await fn(dict(state))
        steps = list(state.get("steps") or [])
        steps.append(name)
        payload = dict(state.get("payload") or {})
        payload.update(dict(updated.get("payload") or updated))
        failure_reason = str(updated.get("failure_reason") or state.get("failure_reason") or "")
        return {
            "payload": payload,
            "steps": steps,
            "cancelled": bool(updated.get("cancelled") or state.get("cancelled")),
            "waiting_approval": bool(updated.get("waiting_approval", False)),
            "approved": bool(updated.get("approved", state.get("approved", True))),
            "failure_reason": failure_reason,
        }

    return _node


def build_sequential_graph(
    name: str,
    nodes: dict[str, Any],
    order: list[str],
) -> GraphDefinition:
    if not order:
        raise ValidationFailedError("Sequential graph requires ordered nodes")
    edges = [(order[i], order[i + 1]) for i in range(len(order) - 1)]
    edges.append((order[-1], "__end__"))
    return GraphDefinition(
        name=name,
        kind=GraphKind.SEQUENTIAL,
        nodes=nodes,
        edges=edges,
        entry=order[0],
        finish="__end__",
    )


def build_conditional_graph(
    name: str,
    nodes: dict[str, Any],
    entry: str,
    edges: list[tuple[str, str]],
    conditional_edges: dict[str, Any],
) -> GraphDefinition:
    return GraphDefinition(
        name=name,
        kind=GraphKind.CONDITIONAL,
        nodes=nodes,
        edges=edges,
        conditional_edges=conditional_edges,
        entry=entry,
        finish="__end__",
    )


def build_parallel_graph(name: str, nodes: dict[str, Any]) -> GraphDefinition:
    return GraphDefinition(
        name=name,
        kind=GraphKind.PARALLEL,
        nodes=nodes,
        edges=[],
        entry=next(iter(nodes)),
        finish="__end__",
    )
