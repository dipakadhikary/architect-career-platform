"""Controlled agent API. High-risk tools are not enabled."""

from __future__ import annotations

from functools import lru_cache
from pathlib import Path
from typing import Any

from fastapi import APIRouter, Depends
from pydantic import AliasChoices, BaseModel, ConfigDict, Field, field_validator

from app.api.v1.assistant import _build_retriever, get_caller
from app.infrastructure.llm.chat_factory import build_chat_provider
from app.intelligence.assistant.models import AnswerSource, CallerContext
from app.orchestration.agent.models import ExecutionRecord
from app.orchestration.agent.runtime import AgentRuntime, LlmPlanner
from app.orchestration.agent.store import AgentStore
from app.orchestration.agent.tools import read_only_registry
from app.orchestration.conversation.store import ConversationStore
from app.shared.config.settings import AppSettings, get_settings
from app.shared.observability.metrics import get_metrics

router = APIRouter(tags=["Agent"])


class ExecuteAgentRequest(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    goal: str = Field(min_length=1)
    conversation_id: str = Field(
        default="",
        validation_alias=AliasChoices("conversationId", "conversation_id"),
    )

    @field_validator("conversation_id", mode="before")
    @classmethod
    def _blank_conversation(cls, value: object) -> object:
        if value is None:
            return ""
        return value


class AgentDecisionRequest(BaseModel):
    decision: str = Field(pattern=r"^(APPROVE|REJECT)$")


class AgentStepResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    step_id: str = Field(serialization_alias="stepId")
    tool: str
    label: str
    status: str


class AgentExecutionResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    execution_id: str = Field(serialization_alias="executionId")
    status: str
    answer: str
    error_code: str = Field(serialization_alias="errorCode")
    sources: list[AnswerSource]
    steps: list[AgentStepResponse]
    approval_required: bool = Field(serialization_alias="approvalRequired")
    proposed_action: str = Field(serialization_alias="proposedAction")


@lru_cache
def _agent_store(path: str) -> AgentStore:
    if path != ":memory:":
        Path(path).parent.mkdir(parents=True, exist_ok=True)
    return AgentStore(path)


@lru_cache
def _conversation_store(path: str) -> ConversationStore:
    if path != ":memory:":
        Path(path).parent.mkdir(parents=True, exist_ok=True)
    return ConversationStore(path)


def get_agent_runtime(settings: AppSettings = Depends(get_settings)) -> AgentRuntime:
    path = settings.conversation_database
    conversations = _conversation_store(path)
    retriever = _build_retriever(settings, get_metrics()) if settings.rag_enabled else None
    return AgentRuntime(
        settings=settings,
        store=_agent_store(path),
        conversations=conversations,
        registry=read_only_registry(settings, retriever, conversations),
        planner=LlmPlanner(build_chat_provider(settings), settings),
        metrics=get_metrics(),
    )


@router.post("/api/v1/ai/agents/execute", response_model=AgentExecutionResponse)
async def execute_agent(
    body: ExecuteAgentRequest,
    caller: CallerContext = Depends(get_caller),
    runtime: AgentRuntime = Depends(get_agent_runtime),
) -> AgentExecutionResponse:
    execution = await runtime.execute(
        caller,
        goal=body.goal,
        conversation_id=body.conversation_id or None,
    )
    return _response(execution)


@router.get(
    "/api/v1/ai/agents/executions/{execution_id}",
    response_model=AgentExecutionResponse,
)
def get_execution(
    execution_id: str,
    caller: CallerContext = Depends(get_caller),
    runtime: AgentRuntime = Depends(get_agent_runtime),
) -> AgentExecutionResponse:
    return _response(runtime.get(execution_id, caller.owner_id))


@router.post(
    "/api/v1/ai/agents/executions/{execution_id}/cancel",
    response_model=AgentExecutionResponse,
)
def cancel_execution(
    execution_id: str,
    caller: CallerContext = Depends(get_caller),
    runtime: AgentRuntime = Depends(get_agent_runtime),
) -> AgentExecutionResponse:
    return _response(runtime.cancel(execution_id, caller.owner_id))


@router.post(
    "/api/v1/ai/agents/executions/{execution_id}/decision",
    response_model=AgentExecutionResponse,
)
def decide_execution(
    execution_id: str,
    body: AgentDecisionRequest,
    caller: CallerContext = Depends(get_caller),
    runtime: AgentRuntime = Depends(get_agent_runtime),
) -> AgentExecutionResponse:
    execution = runtime.decide(
        execution_id,
        caller.owner_id,
        approved=body.decision == "APPROVE",
    )
    return _response(execution)


def _response(execution: ExecutionRecord) -> AgentExecutionResponse:
    return AgentExecutionResponse(
        execution_id=execution.id,
        status=execution.status,
        answer=execution.answer,
        error_code=execution.error_code,
        sources=[_source(item) for item in execution.sources],
        steps=[
            AgentStepResponse(
                step_id=step.id,
                tool=step.tool_name,
                label=step.label,
                status=step.status,
            )
            for step in execution.steps
        ],
        approval_required=execution.approval_required,
        proposed_action=execution.proposed_action,
    )


def _source(item: dict[str, Any]) -> AnswerSource:
    score = item.get("score", 0)
    numeric = float(score) if isinstance(score, int | float | str) else 0.0
    return AnswerSource(
        content_id=str(item.get("contentId") or ""),
        title=str(item.get("title") or ""),
        content_type=str(item.get("contentType") or ""),
        section=str(item.get("section") or ""),
        path=str(item.get("path") or ""),
        url=str(item.get("url") or ""),
        chunk_id=str(item.get("chunkId") or ""),
        score=numeric,
    )
