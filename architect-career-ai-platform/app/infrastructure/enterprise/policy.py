"""Configurable policy engine (no hardcoded business rules in callers)."""

from __future__ import annotations

from typing import Any

from app.intelligence.enterprise.models import PolicyDecision
from app.intelligence.enterprise.policy.ports import PolicyEnginePort
from app.shared.config.settings import AppSettings


class ConfigurablePolicyEngine(PolicyEnginePort):
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings

    def evaluate(self, *, policy_type: str, context: dict[str, Any]) -> PolicyDecision:
        if policy_type == "execution":
            return self._execution_policy(context)
        if policy_type == "capability":
            return self._capability_policy(context)
        if policy_type == "tenant":
            return self._tenant_policy(context)
        if policy_type == "model":
            return self._model_policy(context)
        return PolicyDecision(allowed=True, reason="no_matching_policy")

    def _execution_policy(self, context: dict[str, Any]) -> PolicyDecision:
        estimated_cost = float(context.get("estimated_cost_usd") or 0.0)
        tokens = int(context.get("total_tokens") or 0)
        latency_ms = float(context.get("latency_ms") or 0.0)
        if estimated_cost > self._settings.policy_max_cost_usd:
            return PolicyDecision(
                allowed=False,
                reason="max_cost_exceeded",
                constraints={"max_cost_usd": self._settings.policy_max_cost_usd},
            )
        if tokens > self._settings.policy_max_tokens:
            return PolicyDecision(
                allowed=False,
                reason="max_tokens_exceeded",
                constraints={"max_tokens": self._settings.policy_max_tokens},
            )
        if latency_ms > self._settings.policy_max_latency_ms > 0:
            return PolicyDecision(
                allowed=False,
                reason="max_latency_exceeded",
                constraints={"max_latency_ms": self._settings.policy_max_latency_ms},
            )
        tool = context.get("tool")
        restricted_tools = set(self._settings.policy_restricted_tools_set)
        if tool and tool in restricted_tools:
            return PolicyDecision(allowed=False, reason="restricted_tool")
        prompt = context.get("prompt_name")
        restricted_prompts = set(self._settings.policy_restricted_prompts_set)
        if prompt and prompt in restricted_prompts:
            return PolicyDecision(allowed=False, reason="restricted_prompt")
        return PolicyDecision(
            allowed=True,
            reason="execution_allowed",
            constraints={
                "max_cost_usd": self._settings.policy_max_cost_usd,
                "max_tokens": self._settings.policy_max_tokens,
            },
        )

    def _capability_policy(self, context: dict[str, Any]) -> PolicyDecision:
        capability = str(context.get("capability") or "")
        allowed = self._settings.policy_allowed_capabilities_set
        if allowed and capability not in allowed:
            return PolicyDecision(allowed=False, reason="capability_not_allowed")
        return PolicyDecision(allowed=True, reason="capability_allowed")

    def _tenant_policy(self, context: dict[str, Any]) -> PolicyDecision:
        if not self._settings.tenant_isolation_enabled:
            return PolicyDecision(allowed=True, reason="tenant_isolation_disabled")
        tenant_id = context.get("tenant_id")
        user_id = context.get("user_id")
        principal = context.get("principal_subject")
        if tenant_id and user_id and tenant_id != user_id and principal != "internal-service":
            # Soft isolation: tenant_id should align with user scope when provided.
            return PolicyDecision(allowed=False, reason="tenant_mismatch")
        return PolicyDecision(allowed=True, reason="tenant_ok")

    def _model_policy(self, context: dict[str, Any]) -> PolicyDecision:
        provider = str(context.get("provider") or "")
        model = str(context.get("model") or "")
        allowed_providers = self._settings.policy_allowed_providers_set
        allowed_models = self._settings.policy_allowed_models_set
        if allowed_providers and provider and provider not in allowed_providers:
            return PolicyDecision(allowed=False, reason="provider_not_allowed")
        if allowed_models and model and model not in allowed_models:
            return PolicyDecision(allowed=False, reason="model_not_allowed")
        return PolicyDecision(allowed=True, reason="model_allowed")
