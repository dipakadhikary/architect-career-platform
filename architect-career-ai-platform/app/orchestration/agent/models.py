"""Agent execution records. These are not chain-of-thought traces."""

from __future__ import annotations

from dataclasses import dataclass, field
from enum import StrEnum
from typing import Any

from pydantic import BaseModel, ConfigDict, Field


class ExecutionStatus(StrEnum):
    CREATED = "CREATED"
    PLANNING = "PLANNING"
    WAITING_FOR_APPROVAL = "WAITING_FOR_APPROVAL"
    EXECUTING = "EXECUTING"
    COMPLETED = "COMPLETED"
    FAILED = "FAILED"
    CANCELLED = "CANCELLED"


class RiskLevel(StrEnum):
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    CRITICAL = "CRITICAL"


class ToolPermission(StrEnum):
    READ_KNOWLEDGE = "READ_KNOWLEDGE"
    READ_CONVERSATION = "READ_CONVERSATION"
    READ_CONTENT = "READ_CONTENT"
    WRITE_CONTENT = "WRITE_CONTENT"
    PUBLISH_CONTENT = "PUBLISH_CONTENT"
    ADMIN_OPERATION = "ADMIN_OPERATION"
    PAYMENT_OPERATION = "PAYMENT_OPERATION"
    EXECUTE_CODE = "EXECUTE_CODE"


class PolicyKind(StrEnum):
    ALLOW = "ALLOW"
    DENY = "DENY"
    APPROVAL_REQUIRED = "APPROVAL_REQUIRED"


FORBIDDEN_TOOL_NAMES = frozenset(
    {
        "python",
        "exec",
        "eval",
        "shell",
        "bash",
        "sql",
        "http",
        "request",
        "delete_content",
        "publish_content",
        "update_content",
    }
)

CALLER_PERMISSIONS = frozenset(
    {
        ToolPermission.READ_KNOWLEDGE,
        ToolPermission.READ_CONVERSATION,
        ToolPermission.READ_CONTENT,
    }
)


class ModelAction(BaseModel):
    """Structured planner output. Extra fields are rejected."""

    model_config = ConfigDict(extra="forbid")

    action: str
    tool: str = ""
    arguments: dict[str, Any] = Field(default_factory=dict)
    answer: str = ""


@dataclass(slots=True, frozen=True)
class PolicyOutcome:
    kind: PolicyKind
    code: str = ""
    detail: str = ""
    arguments: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class ToolResult:
    tool: str
    success: bool
    result: dict[str, Any]
    error: str = ""


@dataclass(slots=True, frozen=True)
class StepRecord:
    id: str
    execution_id: str
    sequence_number: int
    tool_name: str
    tool_version: str
    status: str
    success: bool | None
    error_code: str
    label: str


@dataclass(slots=True, frozen=True)
class ExecutionRecord:
    id: str
    owner_id: str
    conversation_id: str | None
    goal: str
    status: str
    answer: str
    error_code: str
    model: str
    provider: str
    prompt_version: str
    prompt_tokens: int
    completion_tokens: int
    estimated_cost_usd: float
    started_at: str
    completed_at: str | None
    steps: tuple[StepRecord, ...] = ()
    sources: tuple[dict[str, Any], ...] = ()
    approval_required: bool = False
    proposed_action: str = ""
