"""Persistent conversation API. Identity comes from the caller, not the body."""

from __future__ import annotations

import math
from functools import lru_cache
from pathlib import Path

from fastapi import APIRouter, Depends, Header, Query
from pydantic import BaseModel, ConfigDict, Field

from app.api.v1.assistant import get_assistant_service, get_caller
from app.intelligence.assistant.models import AnswerSource, CallerContext
from app.orchestration.assistant.service import AssistantService
from app.orchestration.conversation.service import ConversationService
from app.orchestration.conversation.store import (
    ConversationRow,
    ConversationStore,
    MessageRow,
    SourceRow,
)
from app.shared.config.settings import AppSettings, get_settings
from app.shared.observability.metrics import get_metrics

router = APIRouter(tags=["Conversations"])


class ConversationResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    id: str
    title: str
    created_at: str = Field(serialization_alias="createdAt")
    updated_at: str = Field(serialization_alias="updatedAt")


class MessageResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    id: str
    role: str
    content: str
    sequence_number: int = Field(serialization_alias="sequenceNumber")
    status: str
    created_at: str = Field(serialization_alias="createdAt")
    model: str
    provider: str
    grounded: bool
    sources: list[AnswerSource]


class ConversationPageResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    content: list[ConversationResponse]
    page: int
    size: int
    total_elements: int = Field(serialization_alias="totalElements")
    total_pages: int = Field(serialization_alias="totalPages")
    first: bool
    last: bool


class ConversationDetailResponse(ConversationResponse):
    messages: list[MessageResponse]
    truncated: bool


class RenameRequest(BaseModel):
    title: str = Field(min_length=1, max_length=120)


class SendMessageRequest(BaseModel):
    content: str = Field(min_length=1)


class SendMessageResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    user_message: MessageResponse = Field(serialization_alias="userMessage")
    assistant_message: MessageResponse = Field(serialization_alias="assistantMessage")


@lru_cache
def _store_for(path: str) -> ConversationStore:
    if path != ":memory:":
        Path(path).parent.mkdir(parents=True, exist_ok=True)
    return ConversationStore(path)


def get_conversation_service(
    settings: AppSettings = Depends(get_settings),
    assistant: AssistantService = Depends(get_assistant_service),
) -> ConversationService:
    return ConversationService(
        settings=settings,
        store=_store_for(settings.conversation_database),
        assistant=assistant,
        metrics=get_metrics(),
    )


@router.post("/api/v1/ai/conversations", response_model=ConversationResponse)
async def create_conversation(
    caller: CallerContext = Depends(get_caller),
    service: ConversationService = Depends(get_conversation_service),
) -> ConversationResponse:
    """Start an empty conversation owned by the authenticated caller."""
    return _conversation(service.create(caller))


@router.get("/api/v1/ai/conversations", response_model=ConversationPageResponse)
async def list_conversations(
    page: int = Query(default=0, ge=0),
    size: int = Query(default=20, ge=1, le=50),
    caller: CallerContext = Depends(get_caller),
    service: ConversationService = Depends(get_conversation_service),
) -> ConversationPageResponse:
    """List the caller's conversations, newest update first."""
    listed = service.list_conversations(caller, page=page, size=size)
    total_pages = math.ceil(listed.total / size) if listed.total else 0
    return ConversationPageResponse(
        content=[_conversation(item) for item in listed.items],
        page=page,
        size=size,
        total_elements=listed.total,
        total_pages=total_pages,
        first=page == 0,
        last=total_pages == 0 or page >= total_pages - 1,
    )


@router.get(
    "/api/v1/ai/conversations/{conversation_id}",
    response_model=ConversationDetailResponse,
)
async def get_conversation(
    conversation_id: str,
    caller: CallerContext = Depends(get_caller),
    service: ConversationService = Depends(get_conversation_service),
) -> ConversationDetailResponse:
    """Return one owned conversation and its latest messages."""
    detail = service.get(caller, conversation_id)
    base = _conversation(detail.conversation)
    return ConversationDetailResponse(
        id=base.id,
        title=base.title,
        created_at=base.created_at,
        updated_at=base.updated_at,
        messages=[_message(item) for item in detail.messages],
        truncated=detail.truncated,
    )


@router.patch(
    "/api/v1/ai/conversations/{conversation_id}",
    response_model=ConversationResponse,
)
async def rename_conversation(
    conversation_id: str,
    body: RenameRequest,
    caller: CallerContext = Depends(get_caller),
    service: ConversationService = Depends(get_conversation_service),
) -> ConversationResponse:
    """Rename an owned conversation."""
    return _conversation(service.rename(caller, conversation_id, body.title))


@router.delete(
    "/api/v1/ai/conversations/{conversation_id}",
    status_code=204,
    response_model=None,
)
async def delete_conversation(
    conversation_id: str,
    caller: CallerContext = Depends(get_caller),
    service: ConversationService = Depends(get_conversation_service),
) -> None:
    """Delete an owned conversation and its messages."""
    service.delete(caller, conversation_id)


@router.post(
    "/api/v1/ai/conversations/{conversation_id}/messages",
    response_model=SendMessageResponse,
)
async def send_message(
    conversation_id: str,
    body: SendMessageRequest,
    caller: CallerContext = Depends(get_caller),
    service: ConversationService = Depends(get_conversation_service),
    idempotency_key: str | None = Header(default=None, alias="Idempotency-Key"),
) -> SendMessageResponse:
    """Persist the user turn, answer with the existing assistant, and store the reply."""
    sent = await service.send(
        caller,
        conversation_id,
        body.content,
        idempotency_key=idempotency_key,
    )
    return SendMessageResponse(
        user_message=_message(sent.user_message),
        assistant_message=_message(sent.assistant_message),
    )


def _conversation(row: ConversationRow) -> ConversationResponse:
    return ConversationResponse(
        id=row.id,
        title=row.title,
        created_at=row.created_at,
        updated_at=row.updated_at,
    )


def _message(row: MessageRow) -> MessageResponse:
    return MessageResponse(
        id=row.id,
        role=row.role,
        content=row.content,
        sequence_number=row.sequence_number,
        status=row.status,
        created_at=row.created_at,
        model=row.model,
        provider=row.provider,
        grounded=row.grounded,
        sources=[_source(item) for item in row.sources],
    )


def _source(row: SourceRow) -> AnswerSource:
    return AnswerSource(
        content_id=row.content_id,
        title=row.title,
        content_type=row.content_type,
        section=row.section,
        path=row.path,
        url=row.source_url,
        chunk_id=row.chunk_id,
        score=row.score,
    )
