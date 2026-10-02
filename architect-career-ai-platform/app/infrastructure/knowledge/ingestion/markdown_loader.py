"""Markdown loader."""

from __future__ import annotations

from app.intelligence.knowledge.ingestion.ports import DocumentLoaderPort
from app.intelligence.knowledge.models import DocumentFormat, LoadedDocument, RawDocument


class MarkdownDocumentLoader(DocumentLoaderPort):
    @property
    def supported_formats(self) -> set[DocumentFormat]:
        return {DocumentFormat.MARKDOWN}

    async def load(self, document: RawDocument) -> LoadedDocument:
        text = (
            document.content.decode("utf-8")
            if isinstance(document.content, bytes)
            else document.content
        )
        return LoadedDocument(text=text, format=DocumentFormat.MARKDOWN, source=document.source)
