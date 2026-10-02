"""TXT / plain text loader."""

from __future__ import annotations

from app.intelligence.knowledge.ingestion.ports import DocumentLoaderPort
from app.intelligence.knowledge.models import DocumentFormat, LoadedDocument, RawDocument


class TextDocumentLoader(DocumentLoaderPort):
    @property
    def supported_formats(self) -> set[DocumentFormat]:
        return {DocumentFormat.TXT, DocumentFormat.PLAIN}

    async def load(self, document: RawDocument) -> LoadedDocument:
        text = (
            document.content.decode("utf-8")
            if isinstance(document.content, bytes)
            else document.content
        )
        return LoadedDocument(text=text, format=document.format, source=document.source)
