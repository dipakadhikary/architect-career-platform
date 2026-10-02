"""HTML loader using stdlib HTML parser."""

from __future__ import annotations

import re
from html.parser import HTMLParser

from app.intelligence.knowledge.ingestion.ports import DocumentLoaderPort
from app.intelligence.knowledge.models import DocumentFormat, LoadedDocument, RawDocument


class _TextExtractor(HTMLParser):
    def __init__(self) -> None:
        super().__init__()
        self.chunks: list[str] = []
        self._skip = False

    def handle_starttag(self, tag: str, attrs: list[tuple[str, str | None]]) -> None:
        if tag in {"script", "style", "noscript"}:
            self._skip = True

    def handle_endtag(self, tag: str) -> None:
        if tag in {"script", "style", "noscript"}:
            self._skip = False
        if tag in {"p", "div", "br", "li", "h1", "h2", "h3", "h4", "tr"}:
            self.chunks.append("\n")

    def handle_data(self, data: str) -> None:
        if not self._skip:
            text = data.strip()
            if text:
                self.chunks.append(text)


class HtmlDocumentLoader(DocumentLoaderPort):
    @property
    def supported_formats(self) -> set[DocumentFormat]:
        return {DocumentFormat.HTML}

    async def load(self, document: RawDocument) -> LoadedDocument:
        raw = (
            document.content.decode("utf-8")
            if isinstance(document.content, bytes)
            else document.content
        )
        parser = _TextExtractor()
        parser.feed(raw)
        text = re.sub(r"\n{3,}", "\n\n", " ".join(parser.chunks)).strip()
        return LoadedDocument(text=text, format=DocumentFormat.HTML, source=document.source)
