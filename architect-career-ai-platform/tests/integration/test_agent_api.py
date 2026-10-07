"""Agent HTTP flow. The planner and retriever are fakes."""

from __future__ import annotations

import pytest
from app.api.v1.agents import get_agent_runtime
from app.intelligence.assistant.models import ChatMessage, NormalizedCompletion
from app.orchestration.agent.runtime import AgentRuntime
from app.orchestration.agent.store import AgentStore
from app.orchestration.agent.tools import read_only_registry
from app.orchestration.conversation.store import ConversationStore
from app.orchestration.rag.models import RetrievedChunk
from app.shared.config.settings import AppSettings
from app.shared.observability.metrics import PlatformMetrics
from fastapi.testclient import TestClient
from jose import jwt


class _Planner:
    async def propose(self, messages: list[ChatMessage]) -> NormalizedCompletion:
        del messages
        if not hasattr(self, "called"):
            self.called = True
            return NormalizedCompletion(
                answer=(
                    '{"action":"tool","tool":"search_knowledge",'
                    '"arguments":{"query":"Kafka transactions","top_k":5}}'
                ),
                model="fake",
                provider="fake",
                prompt_tokens=12,
                completion_tokens=8,
            )
        return NormalizedCompletion(
            answer='{"action":"final","answer":"Kafka transactions append atomically to a log."}',
            model="fake",
            provider="fake",
            prompt_tokens=12,
            completion_tokens=8,
        )


class _Retriever:
    async def retrieve(
        self, query: str, *, owner_id: str, top_k: int | None = None
    ) -> list[RetrievedChunk]:
        del query, top_k
        return [
            RetrievedChunk(
                chunk_id="chunk-1",
                content_id="note-1",
                content="Kafka transactions append atomically to a log.",
                score=0.91,
                title="Kafka transactions",
                section="Overview",
                content_type="note",
                source_url="/knowledge/note-1",
                path="/knowledge/note-1",
                topic_id=None,
                owner_id=owner_id,
            )
        ]


def _auth(subject: str) -> dict[str, str]:
    token = jwt.encode({"sub": subject}, "change-me", algorithm="HS256")
    return {"Authorization": f"Bearer {token}"}


def test_kafka_lookup_returns_sources(client: TestClient, settings: AppSettings) -> None:
    conversations = ConversationStore(settings.conversation_database)
    runtime = AgentRuntime(
        settings=settings,
        store=AgentStore(settings.conversation_database),
        conversations=conversations,
        registry=read_only_registry(settings, _Retriever(), conversations),
        planner=_Planner(),
        metrics=PlatformMetrics(),
    )
    client.app.dependency_overrides[get_agent_runtime] = lambda: runtime  # type: ignore[attr-defined]
    conversation = conversations.create("user-a", "Kafka")
    response = client.post(
        "/api/v1/ai/agents/execute",
        headers=_auth("user-a"),
        json={
            "goal": "Find information about Kafka transactions in ACOS Knowledge.",
            "conversationId": conversation.id,
        },
    )
    assert response.status_code == 200
    body = response.json()
    assert body["status"] == "COMPLETED"
    assert body["sources"][0]["contentId"] == "note-1"
    assert body["sources"][0]["url"] == "/knowledge/note-1"
    assert body["steps"][0]["tool"] == "search_knowledge"
    stored = runtime._store.get(body["executionId"])
    assert stored is not None
    assert stored.conversation_id == conversation.id
    loaded = client.get(
        f"/api/v1/ai/agents/executions/{body['executionId']}",
        headers=_auth("user-a"),
    )
    assert loaded.status_code == 200
    denied = client.get(
        f"/api/v1/ai/agents/executions/{body['executionId']}",
        headers=_auth("user-b"),
    )
    assert denied.status_code == 403


def test_null_conversation_id_is_accepted(client: TestClient, settings: AppSettings) -> None:
    conversations = ConversationStore(settings.conversation_database)
    runtime = AgentRuntime(
        settings=settings,
        store=AgentStore(settings.conversation_database),
        conversations=conversations,
        registry=read_only_registry(settings, _Retriever(), conversations),
        planner=_Planner(),
        metrics=PlatformMetrics(),
    )
    client.app.dependency_overrides[get_agent_runtime] = lambda: runtime  # type: ignore[attr-defined]
    response = client.post(
        "/api/v1/ai/agents/execute",
        headers=_auth("user-a"),
        json={
            "goal": "Find information about Kafka transactions in ACOS Knowledge.",
            "conversationId": None,
        },
    )
    assert response.status_code == 200
    assert response.json()["status"] == "COMPLETED"


def test_invalid_agent_body_logs_the_field(
    client: TestClient, caplog: pytest.LogCaptureFixture
) -> None:
    with caplog.at_level("WARNING"):
        response = client.post(
            "/api/v1/ai/agents/execute",
            headers=_auth("user-a"),
            json={"conversationId": None},
        )
    assert response.status_code == 400
    assert response.json()["errors"][0]["field"] == "body.goal"
    logged = " ".join(str(record.msg) for record in caplog.records)
    assert "request_validation_failed" in logged
    assert "http_request" in logged
    assert "body.goal" in logged


def test_missing_authentication_is_rejected(client: TestClient) -> None:
    response = client.post(
        "/api/v1/ai/agents/execute",
        json={"goal": "Find Kafka transactions"},
    )
    assert response.status_code == 401
