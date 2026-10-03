"""Indexing pipeline tests with fake embeddings and the in-memory vector store."""

from __future__ import annotations

import asyncio

import pytest
from app.infrastructure.knowledge.embeddings.hashing_embeddings import HashingEmbeddingAdapter
from app.infrastructure.knowledge.vectorstore.memory_store import InMemoryVectorStore
from app.intelligence.embeddings.ports import EmbeddingPort, EmbeddingRequest, EmbeddingResponse
from app.intelligence.indexing.models import ContentType, IndexDocument, IndexStatus
from app.intelligence.knowledge.models import VectorRecord
from app.orchestration.indexing.service import IndexingService
from app.shared.config.settings import AppSettings
from app.shared.exceptions import RateLimitError, ValidationFailedError
from app.shared.observability.metrics import PlatformMetrics
from prometheus_client import CollectorRegistry
from pydantic import SecretStr


def _settings(**overrides: object) -> AppSettings:
    values: dict[str, object] = dict(
        app_env="test",
        redis_enabled=False,
        qdrant_enabled=False,
        langfuse_enabled=False,
        otel_enabled=False,
        auth_jwt_secret=SecretStr("change-me"),
        embedding_provider="hashing",
        embedding_model="hashing-local",
        embedding_dimensions=8,
        embedding_batch_size=2,
        vector_store_provider="memory",
        chunk_size=120,
        chunk_overlap=20,
        index_version=1,
        index_retry_attempts=3,
        index_retry_backoff_seconds=0,
        index_timeout_seconds=1,
    )
    values.update(overrides)
    return AppSettings(**values)  # type: ignore[arg-type]


def _note(content: str, *, version: int = 1, content_id: str = "note-1") -> IndexDocument:
    return IndexDocument(
        content_id=content_id,
        owner_id="owner-1",
        content_type=ContentType.NOTE,
        title="Factory Pattern",
        content=content,
        content_version=version,
        source_url="/knowledge/note-1",
    )


def _service(
    settings: AppSettings,
    embeddings: EmbeddingPort,
    store: InMemoryVectorStore | None = None,
) -> tuple[IndexingService, InMemoryVectorStore, PlatformMetrics]:
    vector_store = store or InMemoryVectorStore()
    metrics = PlatformMetrics(CollectorRegistry())
    service = IndexingService(
        settings=settings,
        embeddings=embeddings,
        vector_store=vector_store,
        metrics=metrics,
    )
    return service, vector_store, metrics


class FakeEmbeddings(EmbeddingPort):
    def __init__(self, settings: AppSettings) -> None:
        self.settings = settings
        self.calls: list[list[str]] = []
        self.failures_remaining = 0
        self.dimensions = settings.embedding_dimensions
        self.short_batch = False
        self.delay_seconds = 0.0

    async def embed(self, request: EmbeddingRequest) -> EmbeddingResponse:
        if self.delay_seconds:
            await asyncio.sleep(self.delay_seconds)
        if self.failures_remaining:
            self.failures_remaining -= 1
            raise RateLimitError("slow down")
        self.calls.append(list(request.texts))
        if self.short_batch:
            return EmbeddingResponse(vectors=[], model="fake", dimensions=self.dimensions)
        vectors = [[0.1] * self.dimensions for _ in request.texts]
        return EmbeddingResponse(vectors=vectors, model="fake-model", dimensions=self.dimensions)


class FlakyStore(InMemoryVectorStore):
    def __init__(self, failures: int) -> None:
        super().__init__()
        self.failures = failures

    async def upsert(self, records: list[VectorRecord]) -> None:
        if self.failures:
            self.failures -= 1
            raise ConnectionError("vector store unavailable")
        await super().upsert(records)


@pytest.mark.asyncio
async def test_pipeline_indexes_note_with_owner_metadata_and_deterministic_ids() -> None:
    settings = _settings()
    service, store, _metrics = _service(settings, HashingEmbeddingAdapter(settings))
    state = await service.index(
        _note("# Factory\n\nThe factory creates objects.\n\n```java\nclass Factory {}\n```")
    )
    assert state.status == IndexStatus.SUCCESS
    assert state.chunk_count == len(store.values())
    assert {record.id for record in store.values()} == {record.id for record in store.values()}
    record = store.values()[0]
    assert record.document_id == "note-1"
    assert record.metadata["owner_id"] == "owner-1"
    assert record.metadata["content_type"] == "NOTE"
    assert record.metadata["source_url"] == "/knowledge/note-1"
    assert record.metadata["embedding_model"] == "hashing-local"
    assert record.metadata["index_version"] == 1
    assert "class Factory" in record.text


@pytest.mark.asyncio
async def test_duplicate_index_is_idempotent() -> None:
    settings = _settings()
    embeddings = FakeEmbeddings(settings)
    service, store, _metrics = _service(settings, embeddings)
    document = _note("One short paragraph about factories.")
    first = await service.index(document)
    second = await service.index(document)
    assert first.chunk_count == second.chunk_count == len(store.values())
    assert len(embeddings.calls) == 1


@pytest.mark.asyncio
async def test_update_replaces_stale_vectors() -> None:
    settings = _settings()
    service, store, _metrics = _service(settings, FakeEmbeddings(settings))
    await service.index(_note("Alpha content that must disappear."))
    await service.index(_note("Beta content is the current note.", version=2))
    texts = [record.text for record in store.values()]
    assert all("Alpha content" not in text for text in texts)
    assert any("Beta content" in text for text in texts)
    assert {record.metadata["content_version"] for record in store.values()} == {2}


@pytest.mark.asyncio
async def test_delete_removes_vectors_for_content_id() -> None:
    settings = _settings()
    service, store, _metrics = _service(settings, FakeEmbeddings(settings))
    await service.index(_note("Keep me until delete."))
    await service.index(_note("Other note", content_id="note-2"))
    state = await service.delete("note-1", owner_id=None)
    assert state.status == IndexStatus.DELETED
    assert all(record.document_id != "note-1" for record in store.values())
    assert any(record.document_id == "note-2" for record in store.values())


@pytest.mark.asyncio
async def test_concept_and_question_answer_metadata() -> None:
    settings = _settings(chunk_size=400)
    service, store, _metrics = _service(settings, FakeEmbeddings(settings))
    await service.index(
        IndexDocument(
            content_id="concept-1",
            owner_id="owner-1",
            content_type=ContentType.CONCEPT,
            title="Factory",
            content="## Idea\n\nA concept explanation.",
            topic_id="topic-1",
            slug="factory",
            path="design/factory",
            source_url="/tutorials/design/factory/concept",
        )
    )
    await service.index(
        IndexDocument(
            content_id="question-1",
            owner_id="owner-1",
            content_type=ContentType.QUESTIONS_ANSWERS,
            title="Factory",
            question="What is a factory?",
            answer="## Answer\n\nIt creates objects.",
            topic_id="topic-1",
            source_url="/tutorials/design/factory/questions",
        )
    )
    concept = next(record for record in store.values() if record.document_id == "concept-1")
    assert concept.metadata["content_type"] == "CONCEPT"
    assert concept.metadata["path"] == "design/factory"
    qa = [record for record in store.values() if record.document_id == "question-1"]
    assert {record.metadata["qa_type"] for record in qa} == {"QUESTION", "ANSWER"}


@pytest.mark.asyncio
async def test_embedding_batches_and_retries_rate_limit() -> None:
    settings = _settings(embedding_batch_size=2, chunk_size=40, chunk_overlap=0)
    embeddings = FakeEmbeddings(settings)
    embeddings.failures_remaining = 1
    paragraphs = "\n\n".join(f"Paragraph {index} explains a distinct idea." for index in range(5))
    service, _store, metrics = _service(settings, embeddings)
    state = await service.index(_note(f"## Parts\n\n{paragraphs}"))
    assert state.status == IndexStatus.SUCCESS
    assert len(embeddings.calls) >= 2
    assert all(len(batch) <= 2 for batch in embeddings.calls)
    assert metrics.index_retries._value.get() >= 1


@pytest.mark.asyncio
async def test_embedding_failure_does_not_mark_success() -> None:
    settings = _settings(index_retry_attempts=1)
    embeddings = FakeEmbeddings(settings)
    embeddings.short_batch = True
    service, store, _metrics = _service(settings, embeddings)
    with pytest.raises(ValidationFailedError):
        await service.index(_note("Some content."))
    assert store.values() == []
    state = service.status("note-1", owner_id=None)
    assert state is not None
    assert state.status == IndexStatus.FAILED
    assert state.retryable is False


@pytest.mark.asyncio
async def test_dimension_mismatch_and_timeout_fail_closed() -> None:
    settings = _settings(index_retry_attempts=1, index_timeout_seconds=0.01)
    mismatch = FakeEmbeddings(settings)
    mismatch.dimensions = 3
    service, store, _metrics = _service(settings, mismatch)
    with pytest.raises(ValidationFailedError):
        await service.index(_note("Dimension check."))
    assert store.values() == []

    slow = FakeEmbeddings(_settings())
    slow.delay_seconds = 0.2
    timeout_service, _timeout_store, _timeout_metrics = _service(settings, slow)
    with pytest.raises(TimeoutError):
        await timeout_service.index(_note("Slow embedding."))
    failed = timeout_service.status("note-1", owner_id=None)
    assert failed is not None
    assert failed.status == IndexStatus.FAILED
    assert failed.retryable is True


@pytest.mark.asyncio
async def test_vector_store_failure_is_retried_then_can_fail() -> None:
    settings = _settings(index_retry_attempts=2)
    store = FlakyStore(failures=1)
    service, _store, metrics = _service(settings, FakeEmbeddings(settings), store)
    state = await service.index(_note("Retry the vector write."))
    assert state.status == IndexStatus.SUCCESS
    assert metrics.index_retries._value.get() >= 1

    failing = FlakyStore(failures=5)
    failed_service, _ignored, _failed_metrics = _service(
        _settings(index_retry_attempts=1), FakeEmbeddings(settings), failing
    )
    with pytest.raises(ConnectionError):
        await failed_service.index(_note("This write fails."))
    status = failed_service.status("note-1", owner_id=None)
    assert status is not None
    assert status.status == IndexStatus.FAILED
    assert status.retryable is True


def test_memory_store_rejects_dimension_mismatch_and_replaces_same_id() -> None:
    store = InMemoryVectorStore()

    async def scenario() -> None:
        await store.ensure_collection(2)
        await store.upsert(
            [VectorRecord(id="a", document_id="doc", text="one", embedding=[0.1, 0.2])]
        )
        await store.upsert(
            [VectorRecord(id="a", document_id="doc", text="two", embedding=[0.3, 0.4])]
        )
        assert len(store.values()) == 1
        assert store.values()[0].text == "two"
        with pytest.raises(ValueError, match="dimension"):
            await store.upsert(
                [VectorRecord(id="b", document_id="doc", text="bad", embedding=[1.0])]
            )

    asyncio.run(scenario())


def test_chat_module_does_not_retrieve_the_index() -> None:
    from pathlib import Path

    source = Path("app/api/v1/assistant.py").read_text(encoding="utf-8")
    assert "IndexingService" not in source
    assert "vector_store" not in source
    assert "embed" not in source
