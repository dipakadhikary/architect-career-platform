"""Shared pytest fixtures and dependency overrides."""

from __future__ import annotations

from collections.abc import Iterator

import pytest
from app.main import create_app
from app.shared.config.settings import AppSettings, get_settings
from app.shared.di.container import container
from dependency_injector import providers
from fastapi.testclient import TestClient


@pytest.fixture
def settings(monkeypatch: pytest.MonkeyPatch) -> AppSettings:
    monkeypatch.setenv("APP_ENV", "test")
    monkeypatch.setenv("APP_DEBUG", "true")
    monkeypatch.setenv("REDIS_ENABLED", "false")
    monkeypatch.setenv("QDRANT_ENABLED", "false")
    monkeypatch.setenv("OPENAI_ENABLED", "false")
    monkeypatch.setenv("AZURE_OPENAI_ENABLED", "false")
    monkeypatch.setenv("OLLAMA_ENABLED", "false")
    monkeypatch.setenv("LANGFUSE_ENABLED", "false")
    monkeypatch.setenv("OTEL_ENABLED", "false")
    monkeypatch.setenv("AUTH_JWT_ENABLED", "false")
    monkeypatch.setenv("AUTH_API_KEY_ENABLED", "false")
    monkeypatch.setenv("RATE_LIMIT_REQUESTS_PER_MINUTE", "0")
    monkeypatch.setenv("EMBEDDING_PROVIDER", "hashing")
    monkeypatch.setenv("VECTOR_STORE_PROVIDER", "memory")
    monkeypatch.setenv("RERANKER_PROVIDER", "identity")
    monkeypatch.setenv("CHUNKING_STRATEGY", "recursive")
    monkeypatch.setenv("CHUNK_SIZE", "200")
    monkeypatch.setenv("CHUNK_OVERLAP", "40")
    monkeypatch.setenv("AGENTIC_PROMPTS_ROOT", "prompts/agentic")
    get_settings.cache_clear()
    return get_settings()


@pytest.fixture
def client(settings: AppSettings) -> Iterator[TestClient]:
    get_settings.cache_clear()
    container.reset_singletons()
    with container.config.override(providers.Object(settings)):
        application = create_app()
        application.dependency_overrides[get_settings] = lambda: settings
        with TestClient(application) as test_client:
            yield test_client
        application.dependency_overrides.clear()
    container.reset_singletons()
    get_settings.cache_clear()
