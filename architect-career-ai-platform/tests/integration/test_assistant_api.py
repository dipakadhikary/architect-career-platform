"""Phase 1 assistant HTTP tests. The language model is replaced with a fake."""

from __future__ import annotations

import pytest
from app.api.v1.assistant import get_assistant_service
from app.intelligence.assistant.errors import (
    ProviderAuthenticationError,
    ProviderUnavailableError,
    ProviderUnexpectedError,
)
from app.intelligence.assistant.models import ChatMessage, NormalizedCompletion
from app.orchestration.assistant.service import AssistantService
from app.shared.config.settings import AppSettings
from app.shared.exceptions import UpstreamTimeoutError
from fastapi.testclient import TestClient
from jose import jwt


def _token(subject: str = "3fa85f64-5717-4562-b3fc-2c963f66afa6") -> str:
    return jwt.encode({"sub": subject}, "change-me", algorithm="HS256")


class FakeProvider:
    provider_name = "fake"

    def __init__(self, error: Exception | None = None) -> None:
        self.error = error

    async def chat(
        self,
        messages: list[ChatMessage],
        *,
        model: str,
        temperature: float,
        max_tokens: int,
    ) -> NormalizedCompletion:
        if self.error is not None:
            raise self.error
        return NormalizedCompletion(
            answer="The Factory pattern separates construction from use.",
            model=model,
            provider=self.provider_name,
        )


def _use_provider(client: TestClient, settings: AppSettings, provider: FakeProvider) -> None:
    service = AssistantService(settings, provider)
    client.app.dependency_overrides[get_assistant_service] = lambda: service


def _chat(client: TestClient, payload: dict[str, object], token: str | None = "user") -> object:
    headers = {}
    if token == "user":
        headers["Authorization"] = f"Bearer {_token()}"
    elif token is not None:
        headers["Authorization"] = token
    return client.post("/api/v1/ai/chat", json=payload, headers=headers)


def test_openapi_documents_chat(client: TestClient) -> None:
    response = client.get("/openapi.json")
    assert response.status_code == 200
    schema = response.json()
    operation = schema["paths"]["/api/v1/ai/chat"]["post"]
    assert operation["summary"]
    assert "ChatRequest" in schema["components"]["schemas"]
    assert "ChatResponse" in schema["components"]["schemas"]


def test_health(client: TestClient) -> None:
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "UP"}


def test_ready_without_provider_credentials(client: TestClient, settings: AppSettings) -> None:
    settings.ai_enabled = True
    settings.llm_provider = "openai"
    settings.openai_api_key = None
    response = client.get("/ready")
    assert response.status_code == 200
    body = response.json()
    assert body["status"] == "NOT_READY"
    assert body["checks"]["process"] == "UP"
    assert body["checks"]["configuration"] == "DOWN"


def test_ready_when_assistant_disabled(client: TestClient, settings: AppSettings) -> None:
    settings.ai_enabled = False
    response = client.get("/ready")
    assert response.status_code == 200
    assert response.json()["status"] == "READY"


def test_chat_authorized(client: TestClient, settings: AppSettings) -> None:
    _use_provider(client, settings, FakeProvider())
    response = _chat(
        client,
        {"messages": [{"role": "user", "content": "What is the Factory Pattern?"}]},
    )
    assert response.status_code == 200
    body = response.json()
    assert body["answer"].startswith("The Factory pattern")
    assert body["provider"] == "fake"
    assert "X-Correlation-Id" in response.headers
    assert body["correlation_id"] == response.headers["X-Correlation-Id"]


def test_chat_propagates_correlation_id(client: TestClient, settings: AppSettings) -> None:
    _use_provider(client, settings, FakeProvider())
    response = client.post(
        "/api/v1/ai/chat",
        json={"messages": [{"role": "user", "content": "Hello"}]},
        headers={"Authorization": f"Bearer {_token()}", "X-Correlation-Id": "corr-from-acos"},
    )
    assert response.status_code == 200
    assert response.headers["X-Correlation-Id"] == "corr-from-acos"
    assert response.json()["correlation_id"] == "corr-from-acos"


def test_chat_unauthorized(client: TestClient, settings: AppSettings) -> None:
    _use_provider(client, settings, FakeProvider())
    response = _chat(client, {"messages": [{"role": "user", "content": "Hello"}]}, token=None)
    assert response.status_code == 401
    assert response.json()["code"] == "AI_AUTHENTICATION_FAILED"


def test_chat_rejects_invalid_token(client: TestClient, settings: AppSettings) -> None:
    _use_provider(client, settings, FakeProvider())
    response = client.post(
        "/api/v1/ai/chat",
        json={"messages": [{"role": "user", "content": "Hello"}]},
        headers={"Authorization": "Bearer not-a-jwt"},
    )
    assert response.status_code == 401


def test_chat_rejects_mismatched_user_id(client: TestClient, settings: AppSettings) -> None:
    _use_provider(client, settings, FakeProvider())
    response = _chat(
        client,
        {
            "messages": [{"role": "user", "content": "Hello"}],
            "user_id": "other-user",
        },
    )
    assert response.status_code == 403


def test_chat_empty_messages(client: TestClient) -> None:
    response = _chat(client, {"messages": []})
    assert response.status_code == 400
    assert response.json()["code"] == "AI_VALIDATION_FAILED"


def test_chat_invalid_role(client: TestClient) -> None:
    response = _chat(client, {"messages": [{"role": "tool", "content": "Hello"}]})
    assert response.status_code == 400


def test_chat_empty_content(client: TestClient) -> None:
    response = _chat(client, {"messages": [{"role": "user", "content": "  "}]})
    assert response.status_code == 400


@pytest.mark.parametrize(
    ("error", "status", "code"),
    [
        (ProviderUnavailableError(), 503, "AI_PROVIDER_UNAVAILABLE"),
        (UpstreamTimeoutError(), 504, "AI_UPSTREAM_TIMEOUT"),
        (ProviderUnexpectedError(), 502, "AI_PROVIDER_ERROR"),
        (ProviderAuthenticationError(), 502, "AI_PROVIDER_AUTHENTICATION_FAILED"),
    ],
)
def test_chat_provider_failures(
    client: TestClient,
    settings: AppSettings,
    error: Exception,
    status: int,
    code: str,
) -> None:
    _use_provider(client, settings, FakeProvider(error=error))
    response = _chat(client, {"messages": [{"role": "user", "content": "Hello"}]})
    assert response.status_code == status
    body = response.json()
    assert body["code"] == code
    assert "sk-" not in response.text


def test_chat_when_disabled(client: TestClient, settings: AppSettings) -> None:
    settings.ai_enabled = False
    _use_provider(client, settings, FakeProvider())
    response = _chat(client, {"messages": [{"role": "user", "content": "Hello"}]})
    assert response.status_code == 503
    assert response.json()["code"] == "AI_DISABLED"
