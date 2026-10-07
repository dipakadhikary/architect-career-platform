"""Bounded agent loop. A denied step is not executed."""

from __future__ import annotations

import json
import time
from typing import Any, Protocol

from pydantic import ValidationError

from app.infrastructure.observability.otel import get_tracer
from app.intelligence.assistant.models import (
    CallerContext,
    ChatMessage,
    ChatRole,
    NormalizedCompletion,
)
from app.intelligence.assistant.provider import LlmProvider
from app.orchestration.agent.approval import ApprovalManager
from app.orchestration.agent.budget import BudgetManager
from app.orchestration.agent.executor import ToolExecutor
from app.orchestration.agent.loop_detect import LoopDetector
from app.orchestration.agent.models import (
    ExecutionRecord,
    ExecutionStatus,
    ModelAction,
    PolicyKind,
)
from app.orchestration.agent.policy import ToolPolicyEngine
from app.orchestration.agent.prompts import system_prompt, user_prompt
from app.orchestration.agent.registry import ToolContext, ToolRegistry
from app.orchestration.agent.store import AgentStore
from app.orchestration.conversation.store import ConversationStore
from app.shared.config.settings import AppSettings
from app.shared.exceptions import AuthorizationError, NotFoundError, ValidationFailedError
from app.shared.observability.metrics import PlatformMetrics

_tracer = get_tracer("acos.ai.agent")

_LABELS = {
    "search_knowledge": "Searching ACOS knowledge",
    "retrieve_knowledge": "Retrieving a knowledge item",
    "search_conversations": "Searching your conversations",
    "search_content": "Searching ACOS content",
}


class Planner(Protocol):
    async def propose(self, messages: list[ChatMessage]) -> NormalizedCompletion:
        """Return the next structured action as completion text."""


class LlmPlanner:
    def __init__(self, provider: LlmProvider, settings: AppSettings) -> None:
        self._provider = provider
        self._settings = settings

    async def propose(self, messages: list[ChatMessage]) -> NormalizedCompletion:
        return await self._provider.chat(
            messages,
            model=self._settings.resolve_chat_model(),
            temperature=0.0,
            max_tokens=400,
        )


class AgentRuntime:
    def __init__(
        self,
        *,
        settings: AppSettings,
        store: AgentStore,
        conversations: ConversationStore,
        registry: ToolRegistry,
        planner: Planner,
        metrics: PlatformMetrics,
    ) -> None:
        self._settings = settings
        self._store = store
        self._conversations = conversations
        self._registry = registry
        self._planner = planner
        self._metrics = metrics
        self._policy = ToolPolicyEngine(registry)
        self._executor = ToolExecutor(settings.agent_tool_timeout_seconds)
        self._approvals = ApprovalManager(store)

    async def execute(
        self,
        caller: CallerContext,
        *,
        goal: str,
        conversation_id: str | None,
    ) -> ExecutionRecord:
        cleaned = _goal(goal, self._settings.ai_max_message_characters)
        history = self._history(caller.owner_id, conversation_id)
        execution = self._store.create(
            owner_id=caller.owner_id,
            conversation_id=conversation_id,
            goal=cleaned,
            prompt_version=self._settings.agent_prompt_version,
        )
        budget = BudgetManager.start(
            max_steps=self._settings.agent_max_steps,
            max_tool_calls=self._settings.agent_max_tool_calls,
            max_tokens=self._settings.agent_max_tokens,
            max_seconds=self._settings.agent_timeout_seconds,
            max_cost_usd=self._settings.agent_max_estimated_cost_usd,
            usd_per_1k_tokens=self._settings.agent_usd_per_1k_tokens,
        )
        started = time.perf_counter()
        with _tracer.start_as_current_span("ai.agent") as span:
            span.set_attribute("execution_id", execution.id)
            span.set_attribute("conversation_id", conversation_id or "")
            span.set_attribute("message_id", "")
            finished = await self._loop(execution, caller, cleaned, history, budget)
        self._metrics.agent_latency.observe(time.perf_counter() - started)
        return finished

    def cancel(self, execution_id: str, owner_id: str) -> ExecutionRecord:
        execution = self._owned(execution_id, owner_id)
        if execution.status in {
            ExecutionStatus.COMPLETED,
            ExecutionStatus.FAILED,
            ExecutionStatus.CANCELLED,
        }:
            return execution
        if execution.status == ExecutionStatus.WAITING_FOR_APPROVAL:
            return self._store.mark(
                execution_id,
                status=ExecutionStatus.CANCELLED,
                error_code="CANCELLED",
                answer="The agent execution was cancelled.",
                finished=True,
            )
        self._store.request_cancel(execution_id, owner_id)
        return self._store.get(execution_id) or execution

    def decide(self, execution_id: str, owner_id: str, *, approved: bool) -> ExecutionRecord:
        if not approved:
            self._metrics.agent_approval_rejected.inc()
        decided = self._approvals.decide(execution_id, approver_id=owner_id, approved=approved)
        self._metrics.agent_executions.labels(status=decided.status).inc()
        return decided

    def get(self, execution_id: str, owner_id: str) -> ExecutionRecord:
        return self._owned(execution_id, owner_id)

    async def _loop(
        self,
        execution: ExecutionRecord,
        caller: CallerContext,
        goal: str,
        history: str,
        budget: BudgetManager,
    ) -> ExecutionRecord:
        observations: list[str] = []
        sources: list[dict[str, Any]] = []
        asked_for_tool = False
        detector = LoopDetector(self._settings.agent_loop_repeat_limit)
        model = ""
        provider = ""
        visible = self._registry.visible()
        messages = [
            ChatMessage(role=ChatRole.SYSTEM, content=system_prompt(visible)),
        ]
        while True:
            if self._store.cancel_requested(execution.id):
                return self._close(
                    execution.id,
                    ExecutionStatus.CANCELLED,
                    "CANCELLED",
                    "The agent execution was cancelled.",
                    model,
                    provider,
                    budget,
                    sources,
                )
            if budget.steps >= budget.max_steps:
                return self._close_limit(
                    execution.id, "STEP_BUDGET", model, provider, budget, sources
                )
            limit = _budget_block(budget)
            if limit:
                return self._close_limit(execution.id, limit, model, provider, budget, sources)
            self._store.mark(execution.id, status=ExecutionStatus.PLANNING)
            user = user_prompt(goal=goal, history=history, observations=observations)
            planned = await self._plan([*messages, ChatMessage(role=ChatRole.USER, content=user)])
            model = planned.model or model
            provider = planned.provider or provider
            budget.add_tokens(planned.prompt_tokens, planned.completion_tokens)
            self._metrics.agent_steps.inc()
            self._metrics.agent_tokens.labels(direction="input").inc(planned.prompt_tokens)
            self._metrics.agent_tokens.labels(direction="output").inc(planned.completion_tokens)
            limit = _budget_block(budget)
            if limit:
                return self._close_limit(execution.id, limit, model, provider, budget, sources)
            action = _parse_action(planned.answer)
            if action is None:
                return self._close(
                    execution.id,
                    ExecutionStatus.FAILED,
                    "INVALID_PLAN",
                    "The agent plan was not valid.",
                    model,
                    provider,
                    budget,
                    sources,
                )
            if action.action == "final":
                answer = action.answer.strip()
                if not answer:
                    return self._close(
                        execution.id,
                        ExecutionStatus.FAILED,
                        "INVALID_PLAN",
                        "The agent plan was not valid.",
                        model,
                        provider,
                        budget,
                        sources,
                    )
                if not observations and visible and not asked_for_tool:
                    asked_for_tool = True
                    observations.append(
                        "No tool has run. Call search_knowledge before the final answer."
                    )
                    continue
                with _tracer.start_as_current_span("ai.agent.complete"):
                    return self._close(
                        execution.id,
                        ExecutionStatus.COMPLETED,
                        "",
                        answer,
                        model,
                        provider,
                        budget,
                        sources,
                    )
            if self._store.cancel_requested(execution.id):
                return self._close(
                    execution.id,
                    ExecutionStatus.CANCELLED,
                    "CANCELLED",
                    "The agent execution was cancelled.",
                    model,
                    provider,
                    budget,
                    sources,
                )
            if budget.tool_calls >= budget.max_tool_calls:
                return self._close_limit(
                    execution.id, "TOOL_BUDGET", model, provider, budget, sources
                )
            outcome = self._policy_check(action.tool, action.arguments, caller.owner_id)
            if outcome.kind == PolicyKind.DENY:
                self._metrics.agent_tool_denied.labels(code=outcome.code).inc()
                return self._close(
                    execution.id,
                    ExecutionStatus.FAILED,
                    outcome.code,
                    "That action is not allowed.",
                    model,
                    provider,
                    budget,
                    sources,
                )
            if outcome.kind == PolicyKind.APPROVAL_REQUIRED:
                return self._park(execution, action.tool, outcome.arguments, caller.owner_id)
            if detector.repeated(action.tool, outcome.arguments):
                self._metrics.agent_loop_detected.inc()
                return self._close(
                    execution.id,
                    ExecutionStatus.FAILED,
                    "LOOP_DETECTED",
                    "The agent repeated the same search. Please clarify the goal.",
                    model,
                    provider,
                    budget,
                    sources,
                )
            budget.add_tool_call()
            self._store.mark(execution.id, status=ExecutionStatus.EXECUTING)
            result = await self._call_tool(
                execution.id,
                sequence=budget.tool_calls,
                tool_name=action.tool,
                arguments=outcome.arguments,
                owner_id=caller.owner_id,
                conversation_id=execution.conversation_id,
            )
            if not result.success:
                return self._close(
                    execution.id,
                    ExecutionStatus.FAILED,
                    result.error or "TOOL_FAILED",
                    "A tool could not complete.",
                    model,
                    provider,
                    budget,
                    sources,
                )
            sources.extend(_sources(result.result))
            observations.append(_observation(action.tool, result.result, self._settings))
            with _tracer.start_as_current_span("ai.agent.observe"):
                pass

    async def _plan(self, messages: list[ChatMessage]) -> NormalizedCompletion:
        started = time.perf_counter()
        with _tracer.start_as_current_span("ai.agent.plan"):
            completion = await self._planner.propose(messages)
        self._metrics.agent_plan_latency.observe(time.perf_counter() - started)
        return completion

    def _policy_check(self, tool_name: str, arguments: dict[str, Any], owner_id: str) -> Any:
        with _tracer.start_as_current_span("ai.agent.policy"):
            return self._policy.evaluate(
                tool_name=tool_name,
                arguments=arguments,
                owner_id=owner_id,
            )

    async def _call_tool(
        self,
        execution_id: str,
        *,
        sequence: int,
        tool_name: str,
        arguments: dict[str, Any],
        owner_id: str,
        conversation_id: str | None,
    ) -> Any:
        tool = self._registry.get(tool_name)
        if tool is None:
            raise RuntimeError("policy allowed an unknown tool")
        started = time.perf_counter()
        with _tracer.start_as_current_span("ai.agent.tool") as span:
            span.set_attribute("tool", tool.spec.name)
            span.set_attribute("tool_version", tool.spec.version)
            with _tracer.start_as_current_span(f"ai.tool.{tool.spec.name}"):
                result = await self._executor.execute(
                    tool,
                    ToolContext(owner_id, execution_id, conversation_id),
                    arguments,
                )
        self._metrics.agent_tool_latency.labels(tool=tool.spec.name).observe(
            time.perf_counter() - started
        )
        self._metrics.agent_tool_calls.labels(tool=tool.spec.name).inc()
        self._store.add_step(
            execution_id=execution_id,
            sequence_number=sequence,
            tool_name=tool.spec.name,
            tool_version=tool.spec.version,
            arguments=arguments,
            status="COMPLETED" if result.success else "FAILED",
            success=result.success,
            error_code=result.error,
            result=_metadata(result.result),
            label=_LABELS.get(tool.spec.name, "Working"),
        )
        return result

    def _park(
        self,
        execution: ExecutionRecord,
        tool_name: str,
        arguments: dict[str, Any],
        owner_id: str,
    ) -> ExecutionRecord:
        self._metrics.agent_approval_required.inc()
        label = f"Approval required before {tool_name}"
        step = self._store.add_step(
            execution_id=execution.id,
            sequence_number=1,
            tool_name=tool_name,
            tool_version="v1",
            arguments=arguments,
            status=ExecutionStatus.WAITING_FOR_APPROVAL,
            success=None,
            error_code="APPROVAL_REQUIRED",
            result={},
            label=label,
        )
        self._approvals.request(
            execution_id=execution.id,
            step_id=step.id,
            tool_name=tool_name,
            requester_id=owner_id,
        )
        self._metrics.agent_executions.labels(status=ExecutionStatus.WAITING_FOR_APPROVAL).inc()
        return self._store.mark(
            execution.id,
            status=ExecutionStatus.WAITING_FOR_APPROVAL,
            approval_required=True,
            proposed_action=label,
        )

    def _history(self, owner_id: str, conversation_id: str | None) -> str:
        if not conversation_id:
            return ""
        conversation = self._conversations.get(conversation_id)
        if conversation is None:
            raise NotFoundError("Conversation was not found")
        if conversation.owner_id != owner_id:
            raise AuthorizationError("Conversation belongs to another user")
        lines: list[str] = []
        for message in self._conversations.messages(conversation_id, limit=6):
            if message.status != "COMPLETED":
                continue
            lines.append(f"{message.role}: {message.content[:500]}")
        return "\n".join(lines)

    def _owned(self, execution_id: str, owner_id: str) -> ExecutionRecord:
        execution = self._store.get(execution_id)
        if execution is None:
            raise NotFoundError("Agent execution was not found")
        if execution.owner_id != owner_id:
            raise AuthorizationError("Agent execution belongs to another user")
        return execution

    def _close_limit(
        self,
        execution_id: str,
        code: str,
        model: str,
        provider: str,
        budget: BudgetManager,
        sources: list[dict[str, Any]],
    ) -> ExecutionRecord:
        message = "The agent stopped because its execution budget was reached."
        return self._close(
            execution_id,
            ExecutionStatus.FAILED,
            code,
            message,
            model,
            provider,
            budget,
            sources,
        )

    def _close(
        self,
        execution_id: str,
        status: ExecutionStatus,
        error_code: str,
        answer: str,
        model: str,
        provider: str,
        budget: BudgetManager,
        sources: list[dict[str, Any]],
    ) -> ExecutionRecord:
        if error_code in {"AGENT_TIMEOUT", "TOOL_TIMEOUT"}:
            self._metrics.agent_timeout.inc()
        self._metrics.agent_cost.inc(budget.estimated_cost_usd)
        self._metrics.agent_executions.labels(status=status).inc()
        if status == ExecutionStatus.COMPLETED:
            self._metrics.agent_success.inc()
        elif status == ExecutionStatus.FAILED:
            self._metrics.agent_failure.labels(code=error_code or "FAILED").inc()
        return self._store.mark(
            execution_id,
            status=status,
            answer=answer,
            error_code=error_code,
            model=model,
            provider=provider,
            prompt_tokens=budget.tokens,
            completion_tokens=0,
            estimated_cost_usd=budget.estimated_cost_usd,
            sources=sources,
            finished=True,
        )


def _goal(goal: str, limit: int) -> str:
    cleaned = goal.strip()
    if not cleaned:
        raise ValidationFailedError("Goal is required")
    if len(cleaned) > limit:
        raise ValidationFailedError("Goal is too long")
    return cleaned


def _budget_block(budget: BudgetManager) -> str:
    return budget.blocked()


def _parse_action(text: str) -> ModelAction | None:
    raw = text.strip()
    if raw.startswith("```"):
        raw = raw.strip("`").strip()
        if raw.lower().startswith("json"):
            raw = raw[4:].strip()
    try:
        payload = json.loads(raw)
        action = ModelAction.model_validate(payload)
    except (json.JSONDecodeError, ValidationError, TypeError):
        return None
    if action.action not in {"tool", "final"}:
        return None
    if action.action == "tool" and not action.tool.strip():
        return None
    return action


def _sources(result: dict[str, Any]) -> list[dict[str, Any]]:
    collected: list[dict[str, Any]] = []
    rows = result.get("results")
    if not isinstance(rows, list):
        return collected
    for item in rows:
        if not isinstance(item, dict) or "contentId" not in item:
            continue
        collected.append(
            {
                "contentId": item.get("contentId", ""),
                "title": item.get("title", ""),
                "contentType": item.get("contentType", ""),
                "section": item.get("section", ""),
                "path": item.get("path", ""),
                "url": item.get("url", ""),
                "chunkId": item.get("chunkId", ""),
                "score": item.get("score", 0),
            }
        )
    return collected


def _metadata(result: dict[str, Any]) -> dict[str, Any]:
    rows = result.get("results")
    count = len(rows) if isinstance(rows, list) else 0
    return {"count": count}


def _observation(tool: str, result: dict[str, Any], settings: AppSettings) -> str:
    text = json.dumps({"tool": tool, "success": True, "result": result})
    return text[: settings.agent_max_result_characters]
