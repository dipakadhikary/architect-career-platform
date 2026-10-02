"""Shared agentic domain models."""

from __future__ import annotations

from dataclasses import dataclass, field
from enum import StrEnum
from typing import Any


class CapabilityKind(StrEnum):
    PLANNER = "planner"
    RETRIEVER = "retriever"
    REASONER = "reasoner"
    MEMORY = "memory"
    PROMPT_BUILDER = "prompt_builder"
    TOOL_EXECUTOR = "tool_executor"
    EVALUATOR = "evaluator"
    RESPONSE_FORMATTER = "response_formatter"
    CONVERSATION_MANAGER = "conversation_manager"
    WORKFLOW_ENGINE = "workflow_engine"
    MODEL_ROUTER = "model_router"


class MemoryScope(StrEnum):
    CONVERSATION = "conversation"
    SESSION = "session"
    LONG_TERM = "long_term"
    SHORT_TERM = "short_term"
    WORKING = "working"


class GraphKind(StrEnum):
    SEQUENTIAL = "sequential"
    CONDITIONAL = "conditional"
    PARALLEL = "parallel"


class WorkflowStatus(StrEnum):
    SUCCEEDED = "SUCCEEDED"
    FAILED = "FAILED"
    CANCELLED = "CANCELLED"
    WAITING_APPROVAL = "WAITING_APPROVAL"


@dataclass(slots=True, frozen=True)
class CapabilityDescriptor:
    name: str
    kind: CapabilityKind
    description: str = ""
    version: str = "v1"
    metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class PlanStep:
    capability: str
    action: str
    arguments: dict[str, Any] = field(default_factory=dict)
    depends_on: tuple[str, ...] = ()


@dataclass(slots=True, frozen=True)
class ExecutionPlan:
    intent: str
    steps: list[PlanStep]
    capabilities: list[str] = field(default_factory=list)
    metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class RetrievalCapabilityQuery:
    text: str
    user_id: str | None = None
    mode: str = "knowledge"
    top_k: int = 5
    filters: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class RetrievalCapabilityHit:
    id: str
    text: str
    score: float
    source: str
    metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class MemoryRecord:
    key: str
    scope: MemoryScope
    payload: dict[str, Any]
    ttl_seconds: int | None = None


@dataclass(slots=True, frozen=True)
class PromptTemplateSpec:
    name: str
    version: str
    system: str
    user: str
    metadata: dict[str, Any] = field(default_factory=dict)
    few_shot: list[dict[str, str]] = field(default_factory=list)


@dataclass(slots=True, frozen=True)
class ToolSpec:
    name: str
    description: str
    parameters_schema: dict[str, Any] = field(default_factory=dict)
    metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class RoutedModel:
    provider: str
    model: str
    reason: str
    estimated_cost_per_1k: float = 0.0
    max_context: int = 8192


@dataclass(slots=True, frozen=True)
class ConversationTurn:
    role: str
    content: str


@dataclass(slots=True, frozen=True)
class ConversationState:
    conversation_id: str
    user_id: str
    turns: list[ConversationTurn] = field(default_factory=list)
    metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class FormattedResponse:
    message: str
    structured: dict[str, Any] = field(default_factory=dict)
    sources: list[dict[str, Any]] = field(default_factory=list)
    model: str | None = None
    prompt_version: str | None = None


@dataclass(slots=True, frozen=True)
class AgenticEvaluation:
    faithfulness: float
    answer_relevance: float
    context_relevance: float
    groundedness: float
    latency_ms: float
    prompt_tokens: int
    completion_tokens: int
    total_tokens: int
    estimated_cost_usd: float
    prompt_version: str
    retriever_quality: float
    llm_quality: float
    details: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class GraphRunRequest:
    graph_name: str
    kind: GraphKind
    state: dict[str, Any]
    thread_id: str | None = None
    resume: bool = False
    cancel: bool = False
    require_approval: bool = False


@dataclass(slots=True, frozen=True)
class GraphRunResult:
    graph_name: str
    status: WorkflowStatus
    state: dict[str, Any]
    thread_id: str
    steps: list[str] = field(default_factory=list)
    failure_reason: str | None = None
    retry_count: int = 0
