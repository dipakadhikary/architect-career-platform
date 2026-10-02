"""Policy-driven model router with fallback and configurable strategies."""

from __future__ import annotations

from app.intelligence.agentic.models import CapabilityDescriptor, CapabilityKind, RoutedModel
from app.intelligence.agentic.router.ports import ModelRouterPort, RoutingRequest
from app.intelligence.enterprise.models import RoutingPolicy
from app.shared.config.settings import AppSettings


class ConfigurablePolicyModelRouter(ModelRouterPort):
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings

    @property
    def descriptor(self) -> CapabilityDescriptor:
        return CapabilityDescriptor(
            name="model_router",
            kind=CapabilityKind.MODEL_ROUTER,
            description="Configurable policy-driven model router",
        )

    async def route(self, request: RoutingRequest) -> RoutedModel:
        policy = RoutingPolicy(
            str(request.metadata.get("routing_policy") or self._settings.routing_policy)
        )
        candidates = self._candidates()
        if not candidates:
            return self._fallback()

        preferred = self._settings.routing_preferred_provider
        capability_map = self._settings.routing_capability_provider_map

        if policy == RoutingPolicy.PREFERRED_PROVIDER and preferred:
            for item in candidates:
                if item.provider == preferred:
                    return item

        if policy == RoutingPolicy.CAPABILITY_SPECIFIC:
            mapped = capability_map.get(request.capability)
            for item in candidates:
                if mapped and item.provider == mapped:
                    return item

        if policy == RoutingPolicy.CONTEXT_LENGTH:
            fit = [c for c in candidates if c.max_context >= request.prompt_tokens_estimate]
            pool = fit or candidates
            return max(pool, key=lambda item: item.max_context)

        if policy == RoutingPolicy.LOWEST_LATENCY:
            return sorted(
                candidates,
                key=lambda item: (
                    0 if item.provider == "ollama" else 1,
                    item.estimated_cost_per_1k,
                ),
            )[0]

        if policy == RoutingPolicy.HIGHEST_QUALITY:
            order = {"openai": 0, "azure_openai": 1, "ollama": 2, "extractive": 3}
            return sorted(candidates, key=lambda item: order.get(item.provider, 9))[0]

        if policy == RoutingPolicy.AVAILABILITY:
            return candidates[0]

        if policy == RoutingPolicy.FALLBACK:
            return candidates[-1]

        # lowest_cost default
        return sorted(candidates, key=lambda item: item.estimated_cost_per_1k)[0]

    async def invoke(self, payload: dict) -> dict:
        routed = await self.route(
            RoutingRequest(
                capability=str(payload.get("capability") or "reasoner"),
                prompt_tokens_estimate=int(payload.get("prompt_tokens_estimate") or 0),
                prefer_low_latency=bool(payload.get("prefer_low_latency")),
                prefer_low_cost=bool(payload.get("prefer_low_cost", True)),
                metadata={"routing_policy": payload.get("routing_policy")},
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
                    estimated_cost_per_1k=self._settings.cost_openai_per_1k_tokens,
                    max_context=128000,
                )
            )
        if self._settings.azure_openai_enabled:
            items.append(
                RoutedModel(
                    provider="azure_openai",
                    model=self._settings.azure_openai_deployment or "azure-deployment",
                    reason="azure openai available",
                    estimated_cost_per_1k=self._settings.cost_azure_per_1k_tokens,
                    max_context=128000,
                )
            )
        if self._settings.ollama_enabled:
            items.append(
                RoutedModel(
                    provider="ollama",
                    model=self._settings.ollama_default_model,
                    reason="ollama available",
                    estimated_cost_per_1k=self._settings.cost_ollama_per_1k_tokens,
                    max_context=8192,
                )
            )
        return items

    def _fallback(self) -> RoutedModel:
        provider = self._settings.routing_fallback_provider or "extractive"
        return RoutedModel(
            provider=provider,
            model="extractive-v1" if provider == "extractive" else "fallback",
            reason="fallback provider",
            estimated_cost_per_1k=0.0,
            max_context=8192,
        )
