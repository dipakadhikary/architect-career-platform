"""Index projection models. These are not the ACOS source documents."""

from __future__ import annotations

from dataclasses import dataclass
from enum import StrEnum


class ContentType(StrEnum):
    NOTE = "NOTE"
    CONCEPT = "CONCEPT"
    QUESTIONS_ANSWERS = "QUESTIONS_ANSWERS"


class QaType(StrEnum):
    QUESTION = "QUESTION"
    ANSWER = "ANSWER"


class IndexStatus(StrEnum):
    PENDING = "PENDING"
    PROCESSING = "PROCESSING"
    SUCCESS = "SUCCESS"
    FAILED = "FAILED"
    DELETED = "DELETED"


@dataclass(slots=True, frozen=True)
class IndexDocument:
    """Authoritative fields copied from ACOS for one indexing operation."""

    content_id: str
    owner_id: str
    content_type: ContentType
    title: str
    content: str = ""
    question: str = ""
    answer: str = ""
    content_version: int = 0
    topic_id: str | None = None
    slug: str | None = None
    path: str | None = None
    source_url: str | None = None


@dataclass(slots=True, frozen=True)
class IndexChunk:
    chunk_id: str
    content_id: str
    text: str
    index: int
    section: str
    qa_type: str | None


@dataclass(slots=True)
class IndexState:
    content_id: str
    owner_id: str
    status: IndexStatus
    content_version: int
    checksum: str
    chunk_count: int
    embedding_model: str
    embedding_provider: str
    index_version: int
    error: str | None = None
    retryable: bool = False
    updated_at: str = ""
