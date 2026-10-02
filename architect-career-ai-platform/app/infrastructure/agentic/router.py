"""Model router selecting providers by capability, latency, cost, context, availability."""

from __future__ import annotations

from app.intelligence.agentic.models import CapabilityDescriptor, CapabilityKind, RoutedModel
from app.intelligence.agentic.router.ports import ModelRouterPort, RoutingRequest
from app.shared.config.settings import AppSettings


class PolicyModelRouter(ModelRouterPort):
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings

    @property
    def descriptor(self) -> CapabilityDescriptor:
        return CapabilityDescriptor(
            name="model_router",
            kind=CapabilityKind.MODEL_ROUTER,
            description="Routes LLM requests by capability/cost/latency/context/availability",
        )

    async def route(self, request: RoutingRequest) -> RoutedModel:
        candidates = self._candidates()
        if not candidates:
            return RoutedModel(
                provider="extractive",
                model="extractive-v1",
                reason="no remote providers available",
                estimated_cost_per_1k=0.0,
                max_context=8192,
            )

        scored: list[tuple[float, RoutedModel]] = []
        for item in candidates:
            score = 0.0
            if request.prefer_low_cost:
                score += max(0.0, 1.0 - item.estimated_cost_per_1k)
            if request.prefer_low_latency and item.provider == "ollama":
                score += 0.4
            if request.prompt_tokens_estimate > item.max_context:
                score -= 5.0
            if request.capability in {"reasoner", "quiz", "resume"} and item.provider == "openai":
                score += 0.2
            scored.append((score, item))
        scored.sort(key=lambda pair: pair[0], reverse=True)
        return scored[0][1]

    async def invoke(self, payload: dict) -> dict:
        routed = await self.route(
            RoutingRequest(
                capability=str(payload.get("capability") or "reasoner"),
                prompt_tokens_estimate=int(payload.get("prompt_tokens_estimate") or 0),
                prefer_low_latency=bool(payload.get("prefer_low_latency")),
                prefer_low_cost=bool(payload.get("prefer_low_cost", True)),
            )
        )
        return {
            "provider": routed.provider,
            "model": routed.model,
            "reason": routed.reason,
            "estimated_cost_per_1k": routed.estimated_cost_per_1k,
            "max_context": routed.max_context,
        }

    def _candidates(self) -> list[RoutedModel]:
        items: list[RoutedModel] = []
        if self._settings.openai_enabled:
            items.append(
                RoutedModel(
                    provider="openai",
                    model=self._settings.openai_default_model,
                    reason="openai available",
                    estimated_cost_per_1k=0.15,
                    max_context=128000,
                )
            )
        if self._settings.azure_openai_enabled:
            items.append(
                RoutedModel(
                    provider="azure_openai",
                    model=self._settings.azure_openai_deployment or "azure-deployment",
                    reason="azure openai available",
                    estimated_cost_per_1k=0.14,
                    max_context=128000,
                )
            )
        if self._settings.ollama_enabled:
            items.append(
                RoutedModel(
                    provider="ollama",
                    model=self._settings.ollama_default_model,
                    reason="ollama available",
                    estimated_cost_per_1k=0.0,
                    max_context=8192,
                )
            )
        return items
