"""Unit tests for configurable policy engine."""

from __future__ import annotations

import pytest
from app.infrastructure.enterprise.policy import ConfigurablePolicyEngine
from app.shared.config.settings import AppSettings


def test_execution_policy_blocks_high_cost(
    settings: AppSettings, monkeypatch: pytest.MonkeyPatch
) -> None:
    monkeypatch.setattr(settings, "policy_max_cost_usd", 0.01)
    engine = ConfigurablePolicyEngine(settings)
    decision = engine.evaluate(
        policy_type="execution",
        context={"estimated_cost_usd": 1.0, "total_tokens": 10},
    )
    assert not decision.allowed
    assert decision.reason == "max_cost_exceeded"


def test_model_policy_allowlist(settings: AppSettings, monkeypatch: pytest.MonkeyPatch) -> None:
    monkeypatch.setattr(settings, "policy_allowed_providers", "openai")
    engine = ConfigurablePolicyEngine(settings)
    denied = engine.evaluate(
        policy_type="model",
        context={"provider": "ollama", "model": "llama"},
    )
    allowed = engine.evaluate(
        policy_type="model",
        context={"provider": "openai", "model": "gpt"},
    )
    assert not denied.allowed
    assert allowed.allowed


def test_restricted_prompt(settings: AppSettings, monkeypatch: pytest.MonkeyPatch) -> None:
    monkeypatch.setattr(settings, "policy_restricted_prompts", "secret")
    engine = ConfigurablePolicyEngine(settings)
    decision = engine.evaluate(
        policy_type="execution",
        context={"prompt_name": "secret", "estimated_cost_usd": 0.0, "total_tokens": 1},
    )
    assert not decision.allowed
