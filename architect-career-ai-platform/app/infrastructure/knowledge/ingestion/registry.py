"""Document ingestion facade with pluggable loaders."""

from __future__ import annotations

from app.intelligence.knowledge.ingestion.ports import DocumentIngestionPort, DocumentLoaderPort
from app.intelligence.knowledge.models import DocumentFormat, LoadedDocument, RawDocument
from app.shared.exceptions import ValidationFailedError


class DocumentIngestionService(DocumentIngestionPort):
    def __init__(self, loaders: list[DocumentLoaderPort]) -> None:
        self._loaders = loaders

    def _resolve(self, fmt: DocumentFormat) -> DocumentLoaderPort:
        for loader in self._loaders:
            if fmt in loader.supported_formats:
                return loader
        raise ValidationFailedError(f"No loader registered for format: {fmt}")

    async def ingest(self, document: RawDocument) -> LoadedDocument:
        loader = self._resolve(document.format)
        return await loader.load(document)
