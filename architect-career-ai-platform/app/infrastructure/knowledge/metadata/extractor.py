"""Metadata extraction and in-memory/redis-backed repository."""

from __future__ import annotations

import hashlib
import json
from datetime import UTC, datetime

from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.intelligence.knowledge.metadata.ports import (
    MetadataExtractorPort,
    MetadataHints,
    MetadataRepositoryPort,
)
from app.intelligence.knowledge.models import DocumentMetadata, LoadedDocument


class DefaultMetadataExtractor(MetadataExtractorPort):
    async def extract(
        self,
        document: LoadedDocument,
        hints: MetadataHints | None = None,
        language: str | None = None,
    ) -> DocumentMetadata:
        hints = hints or MetadataHints()
        checksum = hashlib.sha256(document.text.encode("utf-8")).hexdigest()
        words = [token for token in document.text.split() if token]
        title = hints.title or str(document.raw_metadata.get("title") or "Untitled")
        author = hints.author or (
            str(document.raw_metadata["author"]) if document.raw_metadata.get("author") else None
        )
        return DocumentMetadata(
            title=title,
            author=author,
            category=hints.category,
            tags=list(hints.tags),
            language=language,
            created_at=datetime.now(UTC),
            modified_at=datetime.now(UTC),
            source=hints.source or document.source,
            checksum=checksum,
            word_count=len(words),
            chunk_count=0,
            extra=dict(hints.extra),
        )


class InMemoryMetadataRepository(MetadataRepositoryPort):
    def __init__(self) -> None:
        self._store: dict[str, DocumentMetadata] = {}

    async def save(self, document_id: str, metadata: DocumentMetadata) -> None:
        self._store[document_id] = metadata

    async def get(self, document_id: str) -> DocumentMetadata | None:
        return self._store.get(document_id)

    async def delete(self, document_id: str) -> None:
        self._store.pop(document_id, None)


class RedisMetadataRepository(MetadataRepositoryPort):
    def __init__(self, redis_adapter: RedisAdapter, *, prefix: str = "knowledge:meta:") -> None:
        self._redis = redis_adapter
        self._prefix = prefix
        self._fallback = InMemoryMetadataRepository()

    async def save(self, document_id: str, metadata: DocumentMetadata) -> None:
        payload = {
            "title": metadata.title,
            "author": metadata.author or "",
            "category": metadata.category or "",
            "tags": json.dumps(metadata.tags),
            "language": metadata.language or "",
            "source": metadata.source,
            "checksum": metadata.checksum,
            "word_count": str(metadata.word_count),
            "chunk_count": str(metadata.chunk_count),
            "extra": json.dumps(metadata.extra),
        }
        if self._redis.enabled:
            await self._redis.write(f"{self._prefix}{document_id}", payload)
        await self._fallback.save(document_id, metadata)

    async def get(self, document_id: str) -> DocumentMetadata | None:
        if self._redis.enabled:
            raw = await self._redis.read(f"{self._prefix}{document_id}")
            if raw:
                return DocumentMetadata(
                    title=raw.get("title", "Untitled"),
                    author=raw.get("author") or None,
                    category=raw.get("category") or None,
                    tags=json.loads(raw.get("tags", "[]")),
                    language=raw.get("language") or None,
                    created_at=None,
                    modified_at=None,
                    source=raw.get("source", ""),
                    checksum=raw.get("checksum", ""),
                    word_count=int(raw.get("word_count", "0")),
                    chunk_count=int(raw.get("chunk_count", "0")),
                    extra=json.loads(raw.get("extra", "{}")),
                )
        return await self._fallback.get(document_id)

    async def delete(self, document_id: str) -> None:
        await self._fallback.delete(document_id)
