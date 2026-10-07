"""Authoring API. Proposals are drafts. This route does not save ACOS knowledge."""

from __future__ import annotations

from functools import lru_cache
from pathlib import Path

from fastapi import APIRouter, Depends
from pydantic import BaseModel, ConfigDict, Field, model_validator

from app.api.v1.assistant import _build_retriever, get_caller
from app.infrastructure.llm.chat_factory import build_chat_provider
from app.intelligence.assistant.models import AnswerSource, CallerContext
from app.orchestration.authoring.operations import AuthoringOperation
from app.orchestration.authoring.service import AuthoringCommand, ContentAuthoringService
from app.orchestration.authoring.store import ProposalRow, ProposalStore
from app.orchestration.conversation.store import ConversationStore
from app.orchestration.rag.context import ContextBuilder
from app.shared.config.settings import AppSettings, get_settings
from app.shared.observability.metrics import get_metrics

router = APIRouter(tags=["Authoring"])


class QuestionResponse(BaseModel):
    question: str
    answer: str
    difficulty: str
    explanation: str = ""


class ProposalResponse(BaseModel):
    """An AI draft. authoritative is always false."""

    model_config = ConfigDict(populate_by_name=True)

    proposal_id: str = Field(serialization_alias="proposalId")
    operation: str
    status: str
    content: str
    questions: list[QuestionResponse]
    sources: list[AnswerSource]
    warnings: list[str]
    model: str
    provider: str
    prompt_version: str = Field(serialization_alias="promptVersion")
    grounded: bool
    authoritative: bool = False
    content_id: str | None = Field(default=None, serialization_alias="contentId")
    source_version: int | None = Field(default=None, serialization_alias="sourceVersion")


class GenerateProposalRequest(BaseModel):
    """Draft request. Java omits unused options as JSON null."""

    model_config = ConfigDict(populate_by_name=True)

    operation: AuthoringOperation
    topic: str = ""
    instructions: str = ""
    source_content: str = ""
    content_id: str | None = None
    source_version: int | None = None
    difficulty: str = ""
    question_count: int = Field(default=5, ge=1, le=20)
    conversation_id: str | None = None
    use_conversation: bool = False
    use_knowledge: bool = True

    @model_validator(mode="before")
    @classmethod
    def _accept_omitted_options(cls, value: object) -> object:
        if not isinstance(value, dict):
            return value
        data = dict(value)
        for camel, snake in (
            ("sourceContent", "source_content"),
            ("contentId", "content_id"),
            ("sourceVersion", "source_version"),
            ("questionCount", "question_count"),
            ("conversationId", "conversation_id"),
            ("useConversation", "use_conversation"),
            ("useKnowledge", "use_knowledge"),
        ):
            if camel in data and snake not in data:
                data[snake] = data.pop(camel)
            else:
                data.pop(camel, None)
        for key in ("topic", "instructions", "source_content", "difficulty"):
            if data.get(key) is None:
                data[key] = ""
        if data.get("source_version") in (None, ""):
            data["source_version"] = None
        if data.get("use_conversation") is None:
            data["use_conversation"] = False
        if data.get("use_knowledge") is None:
            data["use_knowledge"] = True
        if data.get("question_count") is None:
            data["question_count"] = 5
        return data


class EditProposalRequest(BaseModel):
    content: str = Field(min_length=1)


class RegenerateProposalRequest(BaseModel):
    instructions: str | None = None


@lru_cache
def _proposal_store(path: str) -> ProposalStore:
    if path != ":memory:":
        Path(path).parent.mkdir(parents=True, exist_ok=True)
    return ProposalStore(path)


@lru_cache
def _conversation_store(path: str) -> ConversationStore:
    if path != ":memory:":
        Path(path).parent.mkdir(parents=True, exist_ok=True)
    return ConversationStore(path)


def get_authoring_service(
    settings: AppSettings = Depends(get_settings),
) -> ContentAuthoringService:
    metrics = get_metrics()
    retriever = _build_retriever(settings, metrics) if settings.rag_enabled else None
    return ContentAuthoringService(
        settings=settings,
        store=_proposal_store(settings.conversation_database),
        provider=build_chat_provider(settings),
        metrics=metrics,
        retriever=retriever,
        context_builder=ContextBuilder(settings) if retriever is not None else None,
        conversations=_conversation_store(settings.conversation_database),
    )


@router.post("/api/v1/ai/authoring/proposals", response_model=ProposalResponse)
async def generate_proposal(
    body: GenerateProposalRequest,
    caller: CallerContext = Depends(get_caller),
    service: ContentAuthoringService = Depends(get_authoring_service),
) -> ProposalResponse:
    """Create a draft proposal. This does not create or update ACOS knowledge."""
    row = await service.generate(caller, _command(body))
    return _proposal(row)


@router.get("/api/v1/ai/authoring/proposals/{proposal_id}", response_model=ProposalResponse)
async def get_proposal(
    proposal_id: str,
    caller: CallerContext = Depends(get_caller),
    service: ContentAuthoringService = Depends(get_authoring_service),
) -> ProposalResponse:
    """Return one proposal owned by the caller."""
    return _proposal(service.get(caller, proposal_id))


@router.patch("/api/v1/ai/authoring/proposals/{proposal_id}", response_model=ProposalResponse)
async def edit_proposal(
    proposal_id: str,
    body: EditProposalRequest,
    caller: CallerContext = Depends(get_caller),
    service: ContentAuthoringService = Depends(get_authoring_service),
) -> ProposalResponse:
    """Replace draft text. The ACOS note is unchanged."""
    return _proposal(service.edit(caller, proposal_id, body.content))


@router.post(
    "/api/v1/ai/authoring/proposals/{proposal_id}/regenerate",
    response_model=ProposalResponse,
)
async def regenerate_proposal(
    proposal_id: str,
    body: RegenerateProposalRequest | None = None,
    caller: CallerContext = Depends(get_caller),
    service: ContentAuthoringService = Depends(get_authoring_service),
) -> ProposalResponse:
    """Create a new proposal from the same source. The previous proposal remains."""
    instructions = body.instructions if body is not None else None
    row = await service.regenerate(caller, proposal_id, instructions=instructions)
    return _proposal(row)


@router.post(
    "/api/v1/ai/authoring/proposals/{proposal_id}/accept",
    response_model=ProposalResponse,
)
async def accept_proposal(
    proposal_id: str,
    caller: CallerContext = Depends(get_caller),
    service: ContentAuthoringService = Depends(get_authoring_service),
) -> ProposalResponse:
    """Mark a proposal accepted for a later explicit ACOS save. Nothing is published."""
    return _proposal(service.accept(caller, proposal_id))


@router.post(
    "/api/v1/ai/authoring/proposals/{proposal_id}/reject",
    response_model=ProposalResponse,
)
async def reject_proposal(
    proposal_id: str,
    caller: CallerContext = Depends(get_caller),
    service: ContentAuthoringService = Depends(get_authoring_service),
) -> ProposalResponse:
    """Discard a proposal from the active draft. The ACOS note is unchanged."""
    return _proposal(service.reject(caller, proposal_id))


def _command(body: GenerateProposalRequest) -> AuthoringCommand:
    return AuthoringCommand(
        operation=body.operation,
        topic=body.topic.strip(),
        instructions=body.instructions.strip(),
        source_content=body.source_content,
        content_id=body.content_id,
        source_version=body.source_version,
        difficulty=body.difficulty.strip().upper(),
        question_count=body.question_count,
        conversation_id=body.conversation_id,
        use_conversation=body.use_conversation,
        use_knowledge=body.use_knowledge,
    )


def _proposal(row: ProposalRow) -> ProposalResponse:
    return ProposalResponse(
        proposal_id=row.id,
        operation=row.operation,
        status=row.status,
        content=row.content,
        questions=[QuestionResponse.model_validate(item) for item in row.questions],
        sources=list(row.sources),
        warnings=list(row.warnings),
        model=row.model,
        provider=row.provider,
        prompt_version=row.prompt_version,
        grounded=row.grounded,
        content_id=row.content_id,
        source_version=row.source_version,
    )
