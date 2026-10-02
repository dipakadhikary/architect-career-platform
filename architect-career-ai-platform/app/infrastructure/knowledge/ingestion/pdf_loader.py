"""PDF loader adapter (pypdf)."""

from __future__ import annotations

import io

from app.intelligence.knowledge.ingestion.ports import DocumentLoaderPort
from app.intelligence.knowledge.models import DocumentFormat, LoadedDocument, RawDocument
from app.shared.exceptions import ValidationFailedError


class PdfDocumentLoader(DocumentLoaderPort):
    @property
    def supported_formats(self) -> set[DocumentFormat]:
        return {DocumentFormat.PDF}

    async def load(self, document: RawDocument) -> LoadedDocument:
        try:
            from pypdf import PdfReader
        except ImportError as exc:  # pragma: no cover
            raise ValidationFailedError("pypdf is required for PDF ingestion") from exc

        payload = (
            document.content
            if isinstance(document.content, bytes)
            else document.content.encode("utf-8")
        )
        reader = PdfReader(io.BytesIO(payload))
        pages = [(page.extract_text() or "") for page in reader.pages]
        text = "\n\n".join(pages).strip()
        meta: dict[str, str | None] = {}
        if reader.metadata:
            meta = {
                "author": str(reader.metadata.author) if reader.metadata.author else None,
                "title": str(reader.metadata.title) if reader.metadata.title else None,
            }
        return LoadedDocument(
            text=text,
            format=DocumentFormat.PDF,
            source=document.source,
            raw_metadata=meta,
        )
