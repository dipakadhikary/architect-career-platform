"""Unit tests for agentic capabilities, planner, tools, memory, prompts, graphs."""

from __future__ import annotations

import pytest
from app.infrastructure.agentic.graphs.engine import (
    LangGraphEngine,
    build_parallel_graph,
    build_sequential_graph,
)
from app.infrastructure.agentic.memory import RedisAgenticMemory
from app.infrastructure.agentic.planner import HeuristicPlanner
from app.infrastructure.agentic.prompts import FilePromptRegistry
from app.infrastructure.agentic.tools.builtins import CalculatorTool
from app.infrastructure.agentic.tools.registry import DefaultToolRegistry
from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.intelligence.agentic.models import (
    GraphKind,
    GraphRunRequest,
    MemoryRecord,
    MemoryScope,
    WorkflowStatus,
)
from app.intelligence.tools.ports import ToolRequest
from app.shared.config.settings import AppSettings


@pytest.mark.asyncio
async def test_planner_detects_intents() -> None:
    planner = HeuristicPlanner()
    plan = await planner.plan("Generate a quiz about architecture", {"user_id": "u1"})
    assert plan.intent == "quiz"
    assert "reasoner" in plan.capabilities


@pytest.mark.asyncio
async def test_memory_scopes(settings: AppSettings) -> None:
    memory = RedisAgenticMemory(RedisAdapter(settings))
    await memory.put(MemoryRecord(key="s1", scope=MemoryScope.SESSION, payload={"hello": "world"}))
    loaded = await memory.get("s1", MemoryScope.SESSION)
    assert loaded == {"hello": "world"}
    await memory.append("s1", MemoryScope.WORKING, {"step": 1})
    working = await memory.get("s1", MemoryScope.WORKING)
    assert working is not None
    assert working["items"][0]["step"] == 1


@pytest.mark.asyncio
async def test_prompt_registry_render() -> None:
    registry = FilePromptRegistry("prompts/agentic")
    rendered = await registry.render(
        "chat",
        {"question": "What is RAG?", "context": "x", "history": ""},
    )
    assert "What is RAG?" in rendered.user
    assert rendered.version == "v1"
    assert registry.validate(rendered) == []


@pytest.mark.asyncio
async def test_calculator_tool() -> None:
    registry = DefaultToolRegistry()
    registry.register(CalculatorTool())
    result = await registry.execute(
        ToolRequest(name="calculator", arguments={"expression": "2+3*4"})
    )
    assert result.output["result"] == 14.0


@pytest.mark.asyncio
async def test_langgraph_sequential_and_parallel() -> None:
    engine = LangGraphEngine()

    async def one(state: dict) -> dict:
        payload = dict(state.get("payload") or {})
        payload["a"] = 1
        return {"payload": payload}

    async def two(state: dict) -> dict:
        payload = dict(state.get("payload") or {})
        payload["b"] = 2
        return {"payload": payload}

    engine.register(build_sequential_graph("seq", {"one": one, "two": two}, ["one", "two"]))
    engine.register(build_parallel_graph("par", {"one": one, "two": two}))

    seq = await engine.invoke(
        GraphRunRequest(graph_name="seq", kind=GraphKind.SEQUENTIAL, state={"seed": True})
    )
    assert seq.status == WorkflowStatus.SUCCEEDED
    assert seq.steps == ["one", "two"]
    assert seq.state["payload"]["a"] == 1
    assert seq.state["payload"]["b"] == 2

    par = await engine.invoke(GraphRunRequest(graph_name="par", kind=GraphKind.PARALLEL, state={}))
    assert par.status == WorkflowStatus.SUCCEEDED
    assert set(par.steps) == {"one", "two"}


@pytest.mark.asyncio
async def test_langgraph_approval_resume_cancel() -> None:
    engine = LangGraphEngine()

    async def node(state: dict) -> dict:
        return {"payload": {"done": True}}

    engine.register(build_sequential_graph("approved", {"node": node}, ["node"]))
    waiting = await engine.invoke(
        GraphRunRequest(
            graph_name="approved",
            kind=GraphKind.SEQUENTIAL,
            state={},
            require_approval=True,
        )
    )
    assert waiting.status == WorkflowStatus.WAITING_APPROVAL
    resumed = await engine.resume(waiting.thread_id, approval=True)
    assert resumed.status == WorkflowStatus.SUCCEEDED
    assert resumed.retry_count >= 1

    waiting2 = await engine.invoke(
        GraphRunRequest(
            graph_name="approved",
            kind=GraphKind.SEQUENTIAL,
            state={},
            require_approval=True,
        )
    )
    cancelled = await engine.cancel(waiting2.thread_id)
    assert cancelled.status == WorkflowStatus.CANCELLED
