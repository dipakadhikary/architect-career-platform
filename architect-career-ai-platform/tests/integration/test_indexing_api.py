"""Indexing API tests. Embeddings and the vector store stay in process."""

from __future__ import annotations

from collections.abc import Iterator

import pytest
from app.api.v1.assistant import router as assistant_router
from app.main import create_app
from app.shared.config.settings import get_settings
from app.shared.di.container import container
from dependency_injector import providers
from fastapi.testclient import TestClient


@pytest.fixture
def index_client(monkeypatch: pytest.MonkeyPatch) -> Iterator[TestClient]:
    monkeypatch.setenv("APP_ENV", "test")
    monkeypatch.setenv("REDIS_ENABLED", "false")
    monkeypatch.setenv("QDRANT_ENABLED", "false")
    monkeypatch.setenv("OPENAI_ENABLED", "false")
    monkeypatch.setenv("OLLAMA_ENABLED", "false")
    monkeypatch.setenv("LANGFUSE_ENABLED", "false")
    monkeypatch.setenv("OTEL_ENABLED", "false")
    monkeypatch.setenv("RATE_LIMIT_REQUESTS_PER_MINUTE", "0")
    monkeypatch.setenv("EMBEDDING_PROVIDER", "hashing")
    monkeypatch.setenv("EMBEDDING_MODEL", "hashing-local")
    monkeypatch.setenv("EMBEDDING_DIMENSIONS", "64")
    monkeypatch.setenv("VECTOR_STORE_PROVIDER", "memory")
    monkeypatch.setenv("INDEX_RETRY_BACKOFF_SECONDS", "0")
    monkeypatch.setenv("AUTH_INTERNAL_SERVICE_TOKENS", "index-token")
    monkeypatch.setenv("CHUNK_SIZE", "500")
    get_settings.cache_clear()
    settings = get_settings()
    container.reset_singletons()
    with container.config.override(providers.Object(settings)):
        application = create_app()
        application.dependency_overrides[get_settings] = lambda: settings
        with TestClient(application) as test_client:
            yield test_client
        application.dependency_overrides.clear()
    container.reset_singletons()
    get_settings.cache_clear()


def _payload() -> dict[str, object]:
    return {
        "contentId": "note-9",
        "ownerId": "owner-9",
        "contentType": "NOTE",
        "title": "Factory Pattern",
        "content": "## Idea\n\nFactories create objects.",
        "contentVersion": 4,
        "sourceUrl": "/knowledge/note-9",
    }


def test_index_requires_service_credentials(index_client: TestClient) -> None:
    response = index_client.post("/api/v1/ai/index/content", json=_payload())
    assert response.status_code == 401


def test_index_status_and_delete_round_trip(index_client: TestClient) -> None:
    headers = {"X-Internal-Service": "index-token"}
    created = index_client.post("/api/v1/ai/index/content", json=_payload(), headers=headers)
    assert created.status_code == 200
    body = created.json()
    assert body["status"] == "SUCCESS"
    assert body["chunkCount"] >= 1
    assert body["contentVersion"] == 4

    again = index_client.post("/api/v1/ai/index/content", json=_payload(), headers=headers)
    assert again.status_code == 200
    assert again.json()["chunkCount"] == body["chunkCount"]

    status = index_client.get("/api/v1/ai/index/content/note-9", headers=headers)
    assert status.status_code == 200
    assert status.json()["embeddingProvider"] == "hashing"

    removed = index_client.delete("/api/v1/ai/index/content/note-9", headers=headers)
    assert removed.status_code == 200
    assert removed.json()["status"] == "DELETED"


def test_rebuild_is_protected_and_chat_route_remains_separate(index_client: TestClient) -> None:
    denied = index_client.post("/api/v1/ai/index/rebuild")
    assert denied.status_code == 401
    accepted = index_client.post(
        "/api/v1/ai/index/rebuild",
        headers={"X-Internal-Service": "index-token"},
    )
    assert accepted.status_code == 200
    assert accepted.json()["status"] == "ACCEPTED"
    chat_paths = {route.path for route in assistant_router.routes}
    assert "/api/v1/ai/chat" in chat_paths
    assert all("/index" not in path for path in chat_paths)
