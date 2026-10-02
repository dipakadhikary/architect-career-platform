"""Document ingestion ports."""

from __future__ import annotations

from abc import ABC, abstractmethod

from app.intelligence.knowledge.models import DocumentFormat, LoadedDocument, RawDocument


class DocumentLoaderPort(ABC):
    @property
    @abstractmethod
    def supported_formats(self) -> set[DocumentFormat]:
        raise NotImplementedError

    @abstractmethod
    async def load(self, document: RawDocument) -> LoadedDocument:
        raise NotImplementedError


class DocumentIngestionPort(ABC):
    @abstractmethod
    async def ingest(self, document: RawDocument) -> LoadedDocument:
        raise NotImplementedError
