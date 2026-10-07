"""Decides whether a proposed tool call may run. Model text is not authority."""

from __future__ import annotations

from typing import Any

from pydantic import ValidationError

from app.orchestration.agent.models import (
    CALLER_PERMISSIONS,
    PolicyKind,
    PolicyOutcome,
    RiskLevel,
)
from app.orchestration.agent.registry import ToolRegistry, ToolSpec

_OWNER_KEYS = ("owner_id", "ownerId", "tenant_id", "tenantId", "user_id", "userId")


class ToolPolicyEngine:
    def __init__(self, registry: ToolRegistry) -> None:
        self._registry = registry

    def evaluate(
        self,
        *,
        tool_name: str,
        arguments: dict[str, Any],
        owner_id: str,
    ) -> PolicyOutcome:
        mismatch = _owner_mismatch(arguments, owner_id)
        if mismatch:
            return PolicyOutcome(PolicyKind.DENY, "TENANT_MISMATCH", mismatch)
        tool = self._registry.get(tool_name)
        if tool is None:
            return PolicyOutcome(PolicyKind.DENY, "UNKNOWN_TOOL", "That tool is not registered")
        spec = tool.spec
        if spec.permission not in CALLER_PERMISSIONS:
            return PolicyOutcome(
                PolicyKind.DENY,
                "UNAUTHORIZED_TOOL",
                "The caller cannot use this tool",
            )
        parsed = _parse_arguments(spec, arguments)
        if isinstance(parsed, PolicyOutcome):
            return parsed
        if spec.risk in {RiskLevel.HIGH, RiskLevel.CRITICAL} or not spec.read_only:
            return PolicyOutcome(
                PolicyKind.APPROVAL_REQUIRED,
                "APPROVAL_REQUIRED",
                f"Approval is required before {spec.name}",
                parsed,
            )
        return PolicyOutcome(PolicyKind.ALLOW, arguments=parsed)


def _owner_mismatch(arguments: dict[str, Any], owner_id: str) -> str:
    for key in _OWNER_KEYS:
        if key not in arguments:
            continue
        supplied = str(arguments[key]).strip()
        if supplied and supplied != owner_id:
            return "Owner or tenant identity does not match the caller"
    return ""


def _parse_arguments(spec: ToolSpec, arguments: dict[str, Any]) -> dict[str, Any] | PolicyOutcome:
    try:
        model = spec.input_model.model_validate(arguments)
    except ValidationError:
        return PolicyOutcome(PolicyKind.DENY, "INVALID_ARGUMENTS", "Tool arguments are invalid")
    return model.model_dump()
