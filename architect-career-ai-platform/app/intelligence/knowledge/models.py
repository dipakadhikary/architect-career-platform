"""Shared Knowledge AI domain models used across ports."""

from __future__ import annotations

from dataclasses import dataclass, field
from datetime import datetime
from enum import StrEnum
from typing import Any


class DocumentFormat(StrEnum):
    PDF = "pdf"
    DOCX = "docx"
    TXT = "txt"
    MARKDOWN = "markdown"
    JSON = "json"
    HTML = "html"
    PLAIN = "plain"


class ChunkingStrategy(StrEnum):
    RECURSIVE = "recursive"
    TOKEN = "token"  # noqa: S105
    SENTENCE = "sentence"
    MARKDOWN = "markdown"
    SEMANTIC = "semantic"


class RetrievalMode(StrEnum):
    DENSE = "dense"
    KEYWORD = "keyword"
    HYBRID = "hybrid"


@dataclass(slots=True, frozen=True)
class RawDocument:
    content: bytes | str
    format: DocumentFormat
    source: str
    filename: str | None = None
    media_type: str | None = None


@dataclass(slots=True, frozen=True)
class LoadedDocument:
    text: str
    format: DocumentFormat
    source: str
    raw_metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class DocumentMetadata:
    title: str
    author: str | None
    category: str | None
    tags: list[str]
    language: str | None
    created_at: datetime | None
    modified_at: datetime | None
    source: str
    checksum: str
    word_count: int
    chunk_count: int = 0
    extra: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class TextChunk:
    chunk_id: str
    text: str
    index: int
    start_offset: int
    end_offset: int
    metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class EmbeddedChunk:
    chunk: TextChunk
    embedding: list[float]
    model: str
    dimensions: int


@dataclass(slots=True, frozen=True)
class VectorRecord:
    id: str
    document_id: str
    text: str
    embedding: list[float]
    metadata: dict[str, Any] = field(default_factory=dict)
    score: float | None = None


@dataclass(slots=True, frozen=True)
class RetrievalHit:
    id: str
    document_id: str
    text: str
    score: float
    metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class BuiltContext:
    text: str
    citations: list[dict[str, Any]]
    token_estimate: int
    documents: list[RetrievalHit]


@dataclass(slots=True, frozen=True)
class PromptBundle:
    name: str
    version: str
    system: str
    user: str
    metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class GenerationResult:
    answer: str
    model: str
    provider: str
    prompt_version: str
    latency_ms: float
    prompt_tokens: int = 0
    completion_tokens: int = 0
    total_tokens: int = 0
    estimated_cost_usd: float = 0.0


@dataclass(slots=True, frozen=True)
class KnowledgeAnswer:
    answer: str
    sources: list[dict[str, Any]]
    confidence: float
    retrieved_documents: list[RetrievalHit]
    latency_ms: float
    token_usage: dict[str, int]
    model: str
    provider: str
    prompt_version: str
    estimated_cost_usd: float = 0.0
