"""Metadata extraction ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any

from app.intelligence.knowledge.models import DocumentMetadata, LoadedDocument


@dataclass(slots=True, frozen=True)
class MetadataHints:
    title: str | None = None
    author: str | None = None
    category: str | None = None
    tags: list[str] = field(default_factory=list)
    source: str | None = None
    extra: dict[str, Any] = field(default_factory=dict)


class MetadataExtractorPort(ABC):
    @abstractmethod
    async def extract(
        self,
        document: LoadedDocument,
        hints: MetadataHints | None = None,
        language: str | None = None,
    ) -> DocumentMetadata:
        raise NotImplementedError


class MetadataRepositoryPort(ABC):
    @abstractmethod
    async def save(self, document_id: str, metadata: DocumentMetadata) -> None:
        raise NotImplementedError

    @abstractmethod
    async def get(self, document_id: str) -> DocumentMetadata | None:
        raise NotImplementedError

    @abstractmethod
    async def delete(self, document_id: str) -> None:
        raise NotImplementedError
