"""Intent detection and multi-step planning capability."""

from __future__ import annotations

from typing import Any

from app.intelligence.agentic.models import (
    CapabilityDescriptor,
    CapabilityKind,
    ExecutionPlan,
    PlanStep,
)
from app.intelligence.agentic.planner.ports import PlannerPort


class HeuristicPlanner(PlannerPort):
    """Deterministic planner suitable for offline and MCP-ready extension."""

    @property
    def descriptor(self) -> CapabilityDescriptor:
        return CapabilityDescriptor(
            name="planner",
            kind=CapabilityKind.PLANNER,
            description="Intent detection, decomposition, and capability selection",
        )

    async def plan(self, goal: str, context: dict[str, Any] | None = None) -> ExecutionPlan:
        context = context or {}
        intent = _detect_intent(goal, context)
        steps = _decompose(intent, goal, context)
        return ExecutionPlan(
            intent=intent,
            steps=steps,
            capabilities=[step.capability for step in steps],
            metadata={"goal": goal, "mcp_ready": True},
        )

    async def invoke(self, payload: dict[str, Any]) -> dict[str, Any]:
        plan = await self.plan(str(payload.get("goal", "")), payload.get("context"))
        return {
            "intent": plan.intent,
            "steps": [
                {
                    "capability": step.capability,
                    "action": step.action,
                    "arguments": step.arguments,
                    "depends_on": list(step.depends_on),
                }
                for step in plan.steps
            ],
            "capabilities": plan.capabilities,
            "metadata": plan.metadata,
        }


def _detect_intent(goal: str, context: dict[str, Any]) -> str:
    forced = context.get("intent")
    if isinstance(forced, str) and forced:
        return forced
    text = goal.lower()
    mapping = (
        ("quiz", "quiz"),
        ("summar", "summarize"),
        ("resume", "resume"),
        ("interview", "interview"),
        ("cover letter", "cover_letter"),
        ("portfolio", "portfolio"),
        ("skill", "skill_gap"),
        ("recommend", "recommend"),
        ("progress", "progress"),
        ("search", "retrieve"),
        ("retriev", "retrieve"),
        ("reason", "reason"),
    )
    for needle, intent in mapping:
        if needle in text:
            return intent
    return "question_answering"


def _decompose(intent: str, goal: str, context: dict[str, Any]) -> list[PlanStep]:
    user_id = str(context.get("user_id") or "")
    common = {"goal": goal, "user_id": user_id}
    templates: dict[str, list[PlanStep]] = {
        "retrieve": [
            PlanStep("retriever", "retrieve", {**common, "mode": "knowledge"}),
            PlanStep("response_formatter", "format", {}, ("retriever",)),
        ],
        "summarize": [
            PlanStep("retriever", "retrieve", {**common, "mode": "knowledge"}),
            PlanStep("reasoner", "summarize", common, ("retriever",)),
            PlanStep("evaluator", "evaluate", {}, ("reasoner",)),
            PlanStep("response_formatter", "format", {}, ("reasoner",)),
        ],
        "question_answering": [
            PlanStep("memory", "load_conversation", common),
            PlanStep("retriever", "retrieve", {**common, "mode": "knowledge"}, ("memory",)),
            PlanStep("prompt_builder", "render", {"name": "chat"}, ("retriever",)),
            PlanStep("reasoner", "answer", common, ("prompt_builder",)),
            PlanStep("evaluator", "evaluate", {}, ("reasoner",)),
            PlanStep("response_formatter", "format", {}, ("reasoner",)),
            PlanStep("memory", "save_turn", common, ("response_formatter",)),
        ],
        "reason": [
            PlanStep("reasoner", "reason", common),
            PlanStep("response_formatter", "format", {}, ("reasoner",)),
        ],
        "quiz": [
            PlanStep("retriever", "retrieve", {**common, "mode": "knowledge"}),
            PlanStep("reasoner", "quiz", common, ("retriever",)),
            PlanStep("response_formatter", "format", {}, ("reasoner",)),
        ],
        "resume": [
            PlanStep("tool_executor", "resume_generation", common),
            PlanStep("response_formatter", "format", {}, ("tool_executor",)),
        ],
        "interview": [
            PlanStep("reasoner", "interview", common),
            PlanStep("evaluator", "evaluate", {}, ("reasoner",)),
            PlanStep("response_formatter", "format", {}, ("reasoner",)),
        ],
        "cover_letter": [
            PlanStep("reasoner", "cover_letter", common),
            PlanStep("response_formatter", "format", {}, ("reasoner",)),
        ],
        "portfolio": [
            PlanStep("reasoner", "portfolio", common),
            PlanStep("response_formatter", "format", {}, ("reasoner",)),
        ],
        "skill_gap": [
            PlanStep("reasoner", "skill_gap", common),
            PlanStep("response_formatter", "format", {}, ("reasoner",)),
        ],
        "recommend": [
            PlanStep("reasoner", "recommend", common),
            PlanStep("response_formatter", "format", {}, ("reasoner",)),
        ],
        "progress": [
            PlanStep("reasoner", "progress", common),
            PlanStep("response_formatter", "format", {}, ("reasoner",)),
        ],
    }
    return templates.get(intent, templates["question_answering"])
