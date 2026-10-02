"""Platform orchestration facade for contract endpoints."""

from __future__ import annotations

import time
import uuid
from typing import Any

from app.intelligence.agentic.conversation.ports import ConversationManagerPort
from app.intelligence.agentic.graphs.ports import GraphEnginePort
from app.intelligence.agentic.models import ConversationTurn, GraphKind, GraphRunRequest
from app.intelligence.agentic.registry.ports import CapabilityRegistryPort
from app.intelligence.agentic.workflows.ports import WorkflowEnginePort
from app.orchestration.workflows import WorkflowRequest
from app.shared.logging.setup import get_logger
from app.shared.observability.metrics import PlatformMetrics

logger = get_logger(__name__)


class AgenticOrchestrationService:
    def __init__(
        self,
        *,
        workflows: WorkflowEnginePort,
        graphs: GraphEnginePort,
        conversations: ConversationManagerPort,
        registry: CapabilityRegistryPort,
        metrics: PlatformMetrics,
        langfuse: object | None = None,
    ) -> None:
        self._workflows = workflows
        self._graphs = graphs
        self._conversations = conversations
        self._registry = registry
        self._metrics = metrics
        self._langfuse = langfuse

    async def chat_completion(
        self,
        *,
        user_id: str,
        message: str,
        conversation_id: str | None = None,
        history: list[dict[str, str]] | None = None,
    ) -> dict[str, Any]:
        started = time.perf_counter()
        state = await self._conversations.get_or_create(
            user_id=user_id, conversation_id=conversation_id
        )
        if history:
            for item in history:
                await self._conversations.append_turn(
                    state.conversation_id,
                    ConversationTurn(role=str(item["role"]), content=str(item["content"])),
                )
        await self._conversations.append_turn(
            state.conversation_id, ConversationTurn(role="user", content=message)
        )
        result = await self._workflows.execute(
            WorkflowRequest(
                name="question_answering",
                payload={
                    "user_id": user_id,
                    "message": message,
                    "conversation_id": state.conversation_id,
                    "history": history or [],
                },
            )
        )
        answer = str(result.output.get("message") or "")
        await self._conversations.append_turn(
            state.conversation_id, ConversationTurn(role="assistant", content=answer)
        )
        self._metrics.agentic_capability_usage.labels("conversation_manager").inc()
        self._record_run("chat", started, result.status)
        return {
            "conversation_id": state.conversation_id,
            "message": answer,
            "model": "acos-chat-v1",
        }

    async def run_workflow(self, name: str, payload: dict[str, Any]) -> dict[str, Any]:
        started = time.perf_counter()
        result = await self._workflows.execute(WorkflowRequest(name=name, payload=payload))
        self._record_run(name, started, result.status)
        return result.output

    async def run_graph(
        self,
        *,
        graph_name: str,
        state: dict[str, Any],
        kind: GraphKind = GraphKind.SEQUENTIAL,
        thread_id: str | None = None,
        require_approval: bool = False,
    ) -> dict[str, Any]:
        started = time.perf_counter()
        result = await self._graphs.invoke(
            GraphRunRequest(
                graph_name=graph_name,
                kind=kind,
                state=state,
                thread_id=thread_id,
                require_approval=require_approval,
            )
        )
        self._metrics.agentic_graph_runs.labels(graph_name, result.status.value).inc()
        self._record_run(graph_name, started, result.status.value)
        return {
            "thread_id": result.thread_id,
            "status": result.status.value,
            "state": result.state,
            "steps": result.steps,
            "failure_reason": result.failure_reason,
            "retry_count": result.retry_count,
        }

    def list_capabilities(self) -> list[str]:
        return [item.name for item in self._registry.list()]

    def _record_run(self, name: str, started: float, status: str) -> None:
        elapsed = time.perf_counter() - started
        self._metrics.agentic_workflow_latency.labels(name).observe(elapsed)
        self._metrics.agentic_workflow_runs.labels(name, status).inc()
        logger.info(
            "agentic.run",
            workflow=name,
            status=status,
            latency_ms=elapsed * 1000,
            run_id=str(uuid.uuid4()),
        )
