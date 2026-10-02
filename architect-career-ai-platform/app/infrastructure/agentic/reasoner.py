"""Reasoning capability using model router + LLM port."""

from __future__ import annotations

from typing import Any

from app.intelligence.agentic.models import CapabilityDescriptor, CapabilityKind
from app.intelligence.agentic.reasoner.ports import ReasonerPort
from app.intelligence.agentic.router.ports import ModelRouterPort, RoutingRequest
from app.intelligence.llm.ports import LlmCompletionRequest, LlmMessage, LlmPort


class LlmReasoner(ReasonerPort):
    def __init__(self, llm: LlmPort, router: ModelRouterPort) -> None:
        self._llm = llm
        self._router = router

    @property
    def descriptor(self) -> CapabilityDescriptor:
        return CapabilityDescriptor(
            name="reasoner",
            kind=CapabilityKind.REASONER,
            description="Multi-purpose reasoning over prompts and context",
        )

    async def reason(self, prompt: str, context: dict[str, Any] | None = None) -> str:
        context = context or {}
        routed = await self._router.route(
            RoutingRequest(
                capability="reasoner",
                prompt_tokens_estimate=max(len(prompt.split()), 1),
                prefer_low_cost=True,
            )
        )
        system = str(context.get("system") or "You are a careful enterprise AI reasoner.")
        completion = await self._llm.complete(
            LlmCompletionRequest(
                messages=[
                    LlmMessage(role="system", content=system),
                    LlmMessage(role="user", content=prompt),
                ],
                model=routed.model,
                metadata={"provider": routed.provider, "route_reason": routed.reason},
            )
        )
        return completion.content

    async def invoke(self, payload: dict[str, Any]) -> dict[str, Any]:
        answer = await self.reason(str(payload.get("prompt", "")), payload.get("context"))
        return {"answer": answer}
