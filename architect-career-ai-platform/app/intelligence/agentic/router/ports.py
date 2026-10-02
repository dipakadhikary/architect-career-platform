"""LLM model router port."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any

from app.intelligence.agentic.models import RoutedModel


@dataclass(slots=True, frozen=True)
class RoutingRequest:
    capability: str
    prompt_tokens_estimate: int = 0
    prefer_low_latency: bool = False
    prefer_low_cost: bool = True
    metadata: dict[str, Any] = field(default_factory=dict)


class ModelRouterPort(ABC):
    @abstractmethod
    async def route(self, request: RoutingRequest) -> RoutedModel:
        raise NotImplementedError

    async def invoke(self, payload: dict[str, Any]) -> dict[str, Any]:
        routed = await self.route(
            RoutingRequest(
                capability=str(payload.get("capability") or "reasoner"),
                prompt_tokens_estimate=int(payload.get("prompt_tokens_estimate") or 0),
                prefer_low_latency=bool(payload.get("prefer_low_latency")),
                prefer_low_cost=bool(payload.get("prefer_low_cost", True)),
                metadata=dict(payload.get("metadata") or {}),
            )
        )
        return {
            "provider": routed.provider,
            "model": routed.model,
            "reason": routed.reason,
            "estimated_cost_per_1k": routed.estimated_cost_per_1k,
            "max_context": routed.max_context,
        }
