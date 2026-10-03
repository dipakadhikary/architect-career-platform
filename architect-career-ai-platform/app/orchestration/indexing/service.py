"""Index ACOS content into the vector store. This service does not answer questions."""

from __future__ import annotations

import asyncio
import hashlib
import json
import time
from collections.abc import Awaitable, Callable

from app.intelligence.embeddings.ports import EmbeddingPort, EmbeddingRequest
from app.intelligence.indexing.models import (
    ContentType,
    IndexChunk,
    IndexDocument,
    IndexState,
    IndexStatus,
    QaType,
)
from app.intelligence.knowledge.models import VectorRecord
from app.intelligence.knowledge.vectorstore.ports import VectorStorePort
from app.orchestration.indexing.chunker import chunk_document
from app.orchestration.indexing.normalize import normalize_markdown
from app.shared.config.settings import AppSettings
from app.shared.exceptions import AuthorizationError, ValidationFailedError
from app.shared.logging.setup import get_logger
from app.shared.observability.metrics import PlatformMetrics
from app.shared.utils.time import utc_now

logger = get_logger(__name__)

_NON_RETRYABLE = (ValidationFailedError, AuthorizationError)


class IndexingService:
    """Normalize, chunk, embed, and upsert one ACOS document at a time."""

    def __init__(
        self,
        *,
        settings: AppSettings,
        embeddings: EmbeddingPort,
        vector_store: VectorStorePort,
        metrics: PlatformMetrics,
    ) -> None:
        self._settings = settings
        self._embeddings = embeddings
        self._vector_store = vector_store
        self._metrics = metrics
        self._status: dict[str, IndexState] = {}

    async def index(self, document: IndexDocument) -> IndexState:
        """Create or replace the current vectors for one content id."""
        started = time.perf_counter()
        try:
            parts = self._parts(document)
            signature = self._signature(document, parts)
            current = self._status.get(document.content_id)
            if (
                current is not None
                and current.status == IndexStatus.SUCCESS
                and current.checksum == signature
                and current.owner_id == document.owner_id
            ):
                logger.info("index.skipped", content_id=document.content_id, reason="unchanged")
                self._metrics.index_documents.labels(outcome="skipped").inc()
                return current
            self._remember(_processing(document))
            chunks = self._chunks(document, parts)
            vectors, model = await self._embed(chunks)
            records = self._records(document, chunks, vectors, model)
            await self._replace(document.content_id, records)
            state = IndexState(
                content_id=document.content_id,
                owner_id=document.owner_id,
                status=IndexStatus.SUCCESS,
                content_version=document.content_version,
                checksum=signature,
                chunk_count=len(records),
                embedding_model=model,
                embedding_provider=self._settings.embedding_provider,
                index_version=self._settings.index_version,
                updated_at=utc_now().isoformat(),
            )
            self._remember(state)
            self._metrics.index_documents.labels(outcome="success").inc()
            self._metrics.index_chunks.inc(len(records))
            self._metrics.index_duration.observe(time.perf_counter() - started)
            logger.info(
                "index.completed",
                content_id=document.content_id,
                content_type=document.content_type.value,
                chunks=len(records),
                embedding_model=model,
            )
            return state
        except Exception as exc:
            retryable = not isinstance(exc, _NON_RETRYABLE)
            self._remember(_failed(document, error=type(exc).__name__, retryable=retryable))
            self._metrics.index_documents.labels(outcome="failed").inc()
            self._metrics.index_duration.observe(time.perf_counter() - started)
            logger.info(
                "index.failed",
                content_id=document.content_id,
                error_type=type(exc).__name__,
                retryable=retryable,
            )
            raise

    async def delete(self, content_id: str, *, owner_id: str | None) -> IndexState:
        """Remove every vector for a content id."""
        current = self._status.get(content_id)
        if owner_id is not None and (current is None or current.owner_id != owner_id):
            raise AuthorizationError("Caller cannot delete this content")
        await self._attempt(lambda: self._vector_store.delete_by_document(content_id))
        self._metrics.vector_deletes.inc()
        state = IndexState(
            content_id=content_id,
            owner_id=owner_id or (current.owner_id if current else ""),
            status=IndexStatus.DELETED,
            content_version=current.content_version if current else 0,
            checksum="",
            chunk_count=0,
            embedding_model=current.embedding_model if current else "",
            embedding_provider=self._settings.embedding_provider,
            index_version=self._settings.index_version,
            updated_at=utc_now().isoformat(),
        )
        self._remember(state)
        self._metrics.index_documents.labels(outcome="deleted").inc()
        logger.info("index.deleted", content_id=content_id)
        return state

    def status(self, content_id: str, *, owner_id: str | None) -> IndexState | None:
        """Return the last indexing state visible to this caller."""
        current = self._status.get(content_id)
        if current is None:
            return None
        if owner_id is not None and current.owner_id != owner_id:
            raise AuthorizationError("Caller cannot read this content index")
        return current

    def _parts(self, document: IndexDocument) -> list[tuple[str | None, str]]:
        if document.content_type == ContentType.QUESTIONS_ANSWERS:
            question = normalize_markdown(document.question)
            answer = normalize_markdown(document.answer)
            if not question and not answer:
                raise ValidationFailedError("Question and answer are empty")
            parts: list[tuple[str | None, str]] = []
            if question:
                parts.append((QaType.QUESTION.value, question))
            if answer:
                parts.append((QaType.ANSWER.value, answer))
            return parts
        body = normalize_markdown(document.content)
        if not body and not document.title.strip():
            raise ValidationFailedError("Content is empty")
        if not body:
            body = document.title.strip()
        return [(None, body)]

    def _chunks(
        self, document: IndexDocument, parts: list[tuple[str | None, str]]
    ) -> list[IndexChunk]:
        chunks: list[IndexChunk] = []
        for qa_type, text in parts:
            chunks.extend(
                chunk_document(
                    content_id=document.content_id,
                    content_version=document.content_version,
                    title=document.title,
                    markdown=text,
                    qa_type=qa_type,
                    chunk_size=self._settings.chunk_size,
                    chunk_overlap=self._settings.chunk_overlap,
                    start_index=len(chunks),
                )
            )
        if not chunks:
            raise ValidationFailedError("Content produced no chunks")
        return chunks

    async def _embed(self, chunks: list[IndexChunk]) -> tuple[list[list[float]], str]:
        batch_size = max(self._settings.embedding_batch_size, 1)
        vectors: list[list[float]] = []
        model = self._settings.embedding_model
        expected = self._settings.embedding_dimensions
        for start in range(0, len(chunks), batch_size):
            batch = [chunk.text for chunk in chunks[start : start + batch_size]]
            response = await self._attempt(
                lambda texts=batch: self._embeddings.embed(
                    EmbeddingRequest(texts=texts, model=self._settings.embedding_model)
                )
            )
            if len(response.vectors) != len(batch):
                self._metrics.embedding_failures.inc()
                raise ValidationFailedError("Embedding provider returned an incomplete batch")
            mismatched = response.dimensions != expected or any(
                len(item) != expected for item in response.vectors
            )
            if mismatched:
                self._metrics.embedding_failures.inc()
                raise ValidationFailedError("Embedding dimension mismatch")
            vectors.extend(response.vectors)
            model = response.model
            self._metrics.embedding_requests.labels(outcome="success").inc()
        return vectors, model

    async def _replace(self, content_id: str, records: list[VectorRecord]) -> None:
        async def write() -> None:
            await self._vector_store.ensure_collection(self._settings.embedding_dimensions)
            await self._vector_store.delete_by_document(content_id)
            await self._vector_store.upsert(records)

        await self._attempt(write)
        self._metrics.vector_deletes.inc()
        self._metrics.vector_upserts.inc(len(records))

    def _records(
        self,
        document: IndexDocument,
        chunks: list[IndexChunk],
        vectors: list[list[float]],
        model: str,
    ) -> list[VectorRecord]:
        indexed_at = utc_now().isoformat()
        records: list[VectorRecord] = []
        for chunk, vector in zip(chunks, vectors, strict=True):
            metadata: dict[str, object] = {
                "content_id": document.content_id,
                "content_version": document.content_version,
                "content_type": document.content_type.value,
                "owner_id": document.owner_id,
                "title": document.title,
                "section": chunk.section,
                "chunk_index": chunk.index,
                "source_url": document.source_url or "",
                "embedding_model": model,
                "embedding_provider": self._settings.embedding_provider,
                "index_version": self._settings.index_version,
                "updated_at": indexed_at,
            }
            if document.topic_id:
                metadata["topic_id"] = document.topic_id
            if document.slug:
                metadata["slug"] = document.slug
            if document.path:
                metadata["path"] = document.path
            if chunk.qa_type:
                metadata["qa_type"] = chunk.qa_type
            records.append(
                VectorRecord(
                    id=chunk.chunk_id,
                    document_id=document.content_id,
                    text=chunk.text,
                    embedding=vector,
                    metadata=metadata,
                )
            )
        return records

    def _signature(self, document: IndexDocument, parts: list[tuple[str | None, str]]) -> str:
        payload = {
            "parts": parts,
            "title": document.title,
            "provider": self._settings.embedding_provider,
            "model": self._settings.embedding_model,
            "dimensions": self._settings.embedding_dimensions,
            "index_version": self._settings.index_version,
            "chunk_size": self._settings.chunk_size,
            "chunk_overlap": self._settings.chunk_overlap,
            "content_type": document.content_type.value,
        }
        encoded = json.dumps(payload, sort_keys=True, separators=(",", ":"))
        return hashlib.sha256(encoded.encode("utf-8")).hexdigest()

    async def _attempt(self, fn: Callable[[], Awaitable[object]]) -> object:
        attempts = max(self._settings.index_retry_attempts, 1)
        delay = self._settings.index_retry_backoff_seconds
        last: Exception | None = None
        for attempt in range(1, attempts + 1):
            try:
                return await asyncio.wait_for(fn(), self._settings.index_timeout_seconds)
            except _NON_RETRYABLE:
                raise
            except Exception as exc:
                last = exc
                if attempt >= attempts:
                    break
                self._metrics.index_retries.inc()
                logger.info(
                    "index.retry",
                    attempt=attempt,
                    error_type=type(exc).__name__,
                )
                await asyncio.sleep(delay * attempt)
        assert last is not None
        raise last

    def _remember(self, state: IndexState) -> None:
        self._status[state.content_id] = state


def _processing(document: IndexDocument) -> IndexState:
    return IndexState(
        content_id=document.content_id,
        owner_id=document.owner_id,
        status=IndexStatus.PROCESSING,
        content_version=document.content_version,
        checksum="",
        chunk_count=0,
        embedding_model="",
        embedding_provider="",
        index_version=0,
        updated_at=utc_now().isoformat(),
    )


def _failed(document: IndexDocument, *, error: str, retryable: bool) -> IndexState:
    return IndexState(
        content_id=document.content_id,
        owner_id=document.owner_id,
        status=IndexStatus.FAILED,
        content_version=document.content_version,
        checksum="",
        chunk_count=0,
        embedding_model="",
        embedding_provider="",
        index_version=0,
        error=error,
        retryable=retryable,
        updated_at=utc_now().isoformat(),
    )
