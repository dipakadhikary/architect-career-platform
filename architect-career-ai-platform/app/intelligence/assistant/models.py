"""Chat contracts shared by the API, orchestrator, and providers."""

from __future__ import annotations

from enum import StrEnum

from pydantic import BaseModel, ConfigDict, Field, field_validator


class ChatRole(StrEnum):
    SYSTEM = "system"
    USER = "user"
    ASSISTANT = "assistant"


class ChatMessage(BaseModel):
    """One turn supplied by the caller."""

    role: ChatRole
    content: str = Field(min_length=1)

    @field_validator("content")
    @classmethod
    def content_must_not_be_blank(cls, value: str) -> str:
        if not value.strip():
            raise ValueError("content must not be empty")
        return value


class ChatRequest(BaseModel):
    """User-facing chat request. Identity comes from the caller context, not this body."""

    messages: list[ChatMessage] = Field(min_length=1)
    user_id: str | None = Field(
        default=None,
        description="Optional owner id. When present it must match the authenticated caller.",
    )


class AnswerSource(BaseModel):
    """Citation built from retrieved ACOS metadata, not from model text."""

    model_config = ConfigDict(populate_by_name=True)

    content_id: str = Field(alias="contentId")
    title: str
    content_type: str = Field(alias="contentType")
    section: str
    path: str
    url: str
    chunk_id: str = Field(alias="chunkId")
    score: float
    topic_id: str | None = Field(default=None, alias="topicId")


class ChatResponse(BaseModel):
    """Provider-neutral assistant answer."""

    answer: str
    model: str
    provider: str
    correlation_id: str = ""
    grounded: bool = False
    sources: list[AnswerSource] = Field(default_factory=list)


class CallerContext(BaseModel):
    """Authenticated caller carried forward for later authorization checks."""

    owner_id: str
    auth_method: str
    correlation_id: str = ""


class NormalizedCompletion(BaseModel):
    """Provider response after mapping into the ACOS chat model."""

    answer: str
    model: str
    provider: str
    prompt_tokens: int = 0
    completion_tokens: int = 0
