"""Shared enterprise platform models."""

from __future__ import annotations

from dataclasses import dataclass, field
from enum import StrEnum
from typing import Any


class RoutingPolicy(StrEnum):
    LOWEST_COST = "lowest_cost"
    LOWEST_LATENCY = "lowest_latency"
    HIGHEST_QUALITY = "highest_quality"
    PREFERRED_PROVIDER = "preferred_provider"
    CAPABILITY_SPECIFIC = "capability_specific"
    CONTEXT_LENGTH = "context_length"
    AVAILABILITY = "availability"
    FALLBACK = "fallback"


class PromptLifecycleStatus(StrEnum):
    DRAFT = "draft"
    APPROVED = "approved"
    DEPRECATED = "deprecated"
    ROLLED_BACK = "rolled_back"


class GuardrailVerdict(StrEnum):
    ALLOW = "allow"
    REDACT = "redact"
    BLOCK = "block"


@dataclass(slots=True, frozen=True)
class PipelineRequest:
    workflow: str
    capability: str
    payload: dict[str, Any]
    user_id: str | None = None
    tenant_id: str | None = None
    principal_subject: str | None = None
    prompt_name: str | None = None
    prompt_version: str | None = None
    input_text: str = ""
    metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class PipelineResult:
    output: dict[str, Any]
    workflow_id: str
    model: str | None = None
    provider: str | None = None
    prompt_version: str | None = None
    evaluation: dict[str, Any] = field(default_factory=dict)
    cost: dict[str, Any] = field(default_factory=dict)
    guardrail: dict[str, Any] = field(default_factory=dict)
    retries: int = 0
    latency_ms: float = 0.0
    failure_reason: str | None = None


@dataclass(slots=True, frozen=True)
class GuardrailFinding:
    rule: str
    severity: str
    message: str
    span: tuple[int, int] | None = None


@dataclass(slots=True, frozen=True)
class GuardrailResult:
    verdict: GuardrailVerdict
    text: str
    findings: list[GuardrailFinding] = field(default_factory=list)
    redactions: list[str] = field(default_factory=list)


@dataclass(slots=True, frozen=True)
class PolicyDecision:
    allowed: bool
    reason: str
    constraints: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class CostRecord:
    provider: str
    model: str
    capability: str
    prompt_tokens: int
    completion_tokens: int
    embedding_tokens: int
    estimated_cost_usd: float
    metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class EnterpriseEvaluationRecord:
    workflow_id: str
    workflow: str
    faithfulness: float
    groundedness: float
    context_precision: float
    context_recall: float
    answer_relevance: float
    retriever_quality: float
    latency_ms: float
    prompt_tokens: int
    completion_tokens: int
    estimated_cost_usd: float
    prompt_version: str
    model: str
    provider: str
    details: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class AuditEvent:
    action: str
    workflow: str
    workflow_id: str
    principal: str | None
    tenant_id: str | None
    success: bool
    detail: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class McpToolDescriptor:
    name: str
    description: str
    input_schema: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class McpResourceDescriptor:
    uri: str
    name: str
    mime_type: str | None = None


@dataclass(slots=True, frozen=True)
class AgentDescriptor:
    agent_id: str
    name: str
    capabilities: list[str] = field(default_factory=list)
    metadata: dict[str, Any] = field(default_factory=dict)
