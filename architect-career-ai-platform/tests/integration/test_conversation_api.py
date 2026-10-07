"""Conversation HTTP tests. The assistant is replaced so no provider is called."""

from __future__ import annotations

from app.api.v1.assistant import get_assistant_service
from app.intelligence.assistant.models import CallerContext, ChatRequest, ChatResponse
from app.orchestration.assistant.service import AssistantService
from app.shared.config.settings import AppSettings
from fastapi.testclient import TestClient
from jose import jwt


def _token(subject: str) -> str:
    return jwt.encode({"sub": subject}, "change-me", algorithm="HS256")


class _FakeAssistant(AssistantService):
    async def chat(
        self,
        request: ChatRequest,
        caller: CallerContext,
        *,
        retrieval_query: str | None = None,
    ) -> ChatResponse:
        del request, caller, retrieval_query
        return ChatResponse(answer="Kafka is a log.", model="fake", provider="fake", grounded=False)


def _auth(subject: str) -> dict[str, str]:
    return {"Authorization": f"Bearer {_token(subject)}"}


def test_missing_authentication_is_rejected(client: TestClient) -> None:
    response = client.post("/api/v1/ai/conversations")
    assert response.status_code == 401


def test_owner_isolation_on_the_http_api(client: TestClient, settings: AppSettings) -> None:
    client.app.dependency_overrides[get_assistant_service] = lambda: _FakeAssistant(settings, None)  # type: ignore[arg-type]
    created = client.post("/api/v1/ai/conversations", headers=_auth("user-a"))
    assert created.status_code == 200
    conversation_id = created.json()["id"]

    denied = client.get(
        f"/api/v1/ai/conversations/{conversation_id}",
        headers=_auth("user-b"),
    )
    assert denied.status_code == 403

    listed = client.get("/api/v1/ai/conversations", headers=_auth("user-b"))
    assert listed.status_code == 200
    assert listed.json()["content"] == []

    renamed = client.patch(
        f"/api/v1/ai/conversations/{conversation_id}",
        headers=_auth("user-b"),
        json={"title": "Stolen"},
    )
    assert renamed.status_code == 403

    removed = client.delete(
        f"/api/v1/ai/conversations/{conversation_id}",
        headers=_auth("user-b"),
    )
    assert removed.status_code == 403

    sent = client.post(
        f"/api/v1/ai/conversations/{conversation_id}/messages",
        headers={**_auth("user-b"), "Idempotency-Key": "nope"},
        json={"content": "hello"},
    )
    assert sent.status_code == 403

    allowed = client.post(
        f"/api/v1/ai/conversations/{conversation_id}/messages",
        headers={**_auth("user-a"), "Idempotency-Key": "turn-1"},
        json={"content": "What is Kafka?"},
    )
    assert allowed.status_code == 200
    body = allowed.json()
    assert body["userMessage"]["content"] == "What is Kafka?"
    assert body["assistantMessage"]["content"] == "Kafka is a log."
    assert body["assistantMessage"]["sources"] == []
