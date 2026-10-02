"""DI wiring for orchestration extension points."""

from __future__ import annotations

from typing import Any

from dependency_injector import containers, providers

from app.infrastructure.agentic.planner import HeuristicPlanner
from app.infrastructure.agentic.reasoner import LlmReasoner
from app.orchestration.evaluation import OrchestrationEvaluator
from app.orchestration.graphs import GraphOrchestrator
from app.orchestration.memory import OrchestrationMemory
from app.orchestration.multi_agent import MultiAgentExecutor
from app.orchestration.planning import Planner
from app.orchestration.reasoning import Reasoner
from app.orchestration.tool_execution import ToolExecutionOrchestrator
from app.orchestration.workflows import WorkflowOrchestrator, WorkflowRequest, WorkflowResult
from app.shared.di.container import container as app_container


class _WorkflowOrchestratorAdapter(WorkflowOrchestrator):
    async def execute(self, request: WorkflowRequest) -> WorkflowResult:
        return await app_container.workflow_engine().execute(request)


class _GraphOrchestratorAdapter(GraphOrchestrator):
    async def invoke(self, graph_name: str, state: dict[str, Any]) -> dict[str, Any]:
        from app.intelligence.agentic.models import GraphKind, GraphRunRequest

        result = await app_container.graph_engine().invoke(
            GraphRunRequest(graph_name=graph_name, kind=GraphKind.SEQUENTIAL, state=state)
        )
        return {
            "status": result.status.value,
            "state": result.state,
            "thread_id": result.thread_id,
            "steps": result.steps,
        }


class _MultiAgentAdapter(MultiAgentExecutor):
    async def run(self, goal: str, context: dict[str, Any]) -> dict[str, Any]:
        plan = await HeuristicPlanner().plan(goal, context)
        return {
            "intent": plan.intent,
            "steps": [step.capability for step in plan.steps],
            "capabilities": plan.capabilities,
        }


class _PlannerAdapter(Planner):
    async def plan(self, goal: str, context: dict[str, Any]) -> list[dict[str, Any]]:
        plan = await HeuristicPlanner().plan(goal, context)
        return [
            {
                "capability": step.capability,
                "action": step.action,
                "arguments": step.arguments,
            }
            for step in plan.steps
        ]


class _ReasonerAdapter(Reasoner):
    async def reason(self, prompt: str, context: dict[str, Any]) -> str:
        reasoner = app_container.agentic_reasoner()
        assert isinstance(reasoner, LlmReasoner)
        return await reasoner.reason(prompt, context)


class _ToolExecutionAdapter(ToolExecutionOrchestrator):
    async def execute(self, tool_name: str, arguments: dict[str, Any]) -> dict[str, Any]:
        from app.intelligence.tools.ports import ToolRequest

        response = await app_container.tool_registry().execute(
            ToolRequest(name=tool_name, arguments=arguments)
        )
        return response.output


class _MemoryAdapter(OrchestrationMemory):
    async def load(self, session_id: str) -> dict[str, Any]:
        from app.intelligence.agentic.models import MemoryScope

        payload = await app_container.agentic_memory().get(session_id, MemoryScope.SESSION)
        return payload or {}

    async def save(self, session_id: str, state: dict[str, Any]) -> None:
        from app.intelligence.agentic.models import MemoryRecord, MemoryScope

        await app_container.agentic_memory().put(
            MemoryRecord(key=session_id, scope=MemoryScope.SESSION, payload=state)
        )


class _EvaluatorAdapter(OrchestrationEvaluator):
    async def evaluate(self, run_id: str, payload: dict[str, Any]) -> dict[str, Any]:
        from app.intelligence.agentic.evaluation.ports import AgenticEvaluationRequest

        result = await app_container.agentic_evaluator().evaluate(
            AgenticEvaluationRequest(
                query=str(payload.get("query") or ""),
                answer=str(payload.get("answer") or ""),
                metadata={"run_id": run_id},
            )
        )
        return {
            "faithfulness": result.faithfulness,
            "answer_relevance": result.answer_relevance,
            "groundedness": result.groundedness,
            "latency_ms": result.latency_ms,
            "prompt_version": result.prompt_version,
        }


class OrchestrationContainer(containers.DeclarativeContainer):
    """Extension container for workflow/agent wiring."""

    workflow_orchestrator = providers.Singleton(_WorkflowOrchestratorAdapter)
    graph_orchestrator = providers.Singleton(_GraphOrchestratorAdapter)
    multi_agent_executor = providers.Singleton(_MultiAgentAdapter)
    planner = providers.Singleton(_PlannerAdapter)
    reasoner = providers.Singleton(_ReasonerAdapter)
    tool_execution = providers.Singleton(_ToolExecutionAdapter)
    memory = providers.Singleton(_MemoryAdapter)
    evaluator = providers.Singleton(_EvaluatorAdapter)
