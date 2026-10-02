"""Unit tests for policy-driven model router."""

from __future__ import annotations

import pytest
from app.infrastructure.enterprise.router import ConfigurablePolicyModelRouter
from app.intelligence.agentic.router.ports import RoutingRequest
from app.shared.config.settings import AppSettings


@pytest.mark.asyncio
async def test_router_fallback_when_no_providers(settings: AppSettings) -> None:
    router = ConfigurablePolicyModelRouter(settings)
    routed = await router.route(
        RoutingRequest(capability="chat", prompt_tokens_estimate=10, prefer_low_cost=True)
    )
    assert routed.provider == settings.routing_fallback_provider or routed.provider == "extractive"


@pytest.mark.asyncio
async def test_router_lowest_cost_prefers_cheaper(
    settings: AppSettings, monkeypatch: pytest.MonkeyPatch
) -> None:
    monkeypatch.setattr(settings, "openai_enabled", True)
    monkeypatch.setattr(settings, "ollama_enabled", True)
    monkeypatch.setattr(settings, "cost_openai_per_1k_tokens", 0.5)
    monkeypatch.setattr(settings, "cost_ollama_per_1k_tokens", 0.0)
    monkeypatch.setattr(settings, "routing_policy", "lowest_cost")
    router = ConfigurablePolicyModelRouter(settings)
    routed = await router.route(
        RoutingRequest(
            capability="chat",
            prompt_tokens_estimate=10,
            prefer_low_cost=True,
            metadata={"routing_policy": "lowest_cost"},
        )
    )
    assert routed.provider == "ollama"


@pytest.mark.asyncio
async def test_router_preferred_provider(
    settings: AppSettings, monkeypatch: pytest.MonkeyPatch
) -> None:
    monkeypatch.setattr(settings, "openai_enabled", True)
    monkeypatch.setattr(settings, "ollama_enabled", True)
    monkeypatch.setattr(settings, "routing_preferred_provider", "openai")
    monkeypatch.setattr(settings, "routing_policy", "preferred_provider")
    router = ConfigurablePolicyModelRouter(settings)
    routed = await router.route(
        RoutingRequest(
            capability="chat",
            prompt_tokens_estimate=10,
            metadata={"routing_policy": "preferred_provider"},
        )
    )
    assert routed.provider == "openai"
