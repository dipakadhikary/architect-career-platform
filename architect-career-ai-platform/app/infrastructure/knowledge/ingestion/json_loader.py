"""JSON loader — flattens textual values."""

from __future__ import annotations

import json
from typing import Any

from app.intelligence.knowledge.ingestion.ports import DocumentLoaderPort
from app.intelligence.knowledge.models import DocumentFormat, LoadedDocument, RawDocument


class JsonDocumentLoader(DocumentLoaderPort):
    @property
    def supported_formats(self) -> set[DocumentFormat]:
        return {DocumentFormat.JSON}

    async def load(self, document: RawDocument) -> LoadedDocument:
        raw = (
            document.content.decode("utf-8")
            if isinstance(document.content, bytes)
            else document.content
        )
        payload = json.loads(raw)
        text = self._flatten(payload)
        keys = list(payload.keys()) if isinstance(payload, dict) else []
        return LoadedDocument(
            text=text,
            format=DocumentFormat.JSON,
            source=document.source,
            raw_metadata={"json_keys": keys},
        )

    def _flatten(self, value: Any, prefix: str = "") -> str:
        if isinstance(value, dict):
            parts = [self._flatten(v, f"{prefix}{k}.") for k, v in value.items()]
            return "\n".join(p for p in parts if p)
        if isinstance(value, list):
            parts = [self._flatten(v, prefix) for v in value]
            return "\n".join(p for p in parts if p)
        if value is None:
            return ""
        return f"{prefix.rstrip('.')}: {value}" if prefix else str(value)
