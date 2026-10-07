"""Authoring HTTP contract. Java sends omitted options as JSON null."""

from __future__ import annotations

import gzip
import json

from app.api.v1.authoring import get_authoring_service
from app.intelligence.assistant.models import CallerContext
from app.orchestration.authoring.operations import AuthoringOperation
from app.orchestration.authoring.service import AuthoringCommand
from app.orchestration.authoring.store import ProposalRow
from fastapi.testclient import TestClient
from jose import jwt


class _Authoring:
    def __init__(self) -> None:
        self.command: AuthoringCommand | None = None

    async def generate(self, caller: CallerContext, command: AuthoringCommand) -> ProposalRow:
        del caller
        self.command = command
        return ProposalRow(
            id="proposal-1",
            owner_id="user-a",
            operation=command.operation.value,
            status="GENERATED",
            topic=command.topic,
            instructions=command.instructions,
            source_content=command.source_content,
            content_id=command.content_id,
            source_version=command.source_version,
            conversation_id=None,
            use_knowledge=command.use_knowledge,
            difficulty=command.difficulty,
            question_count=command.question_count,
            content="## Draft\n\nImproved note.",
            questions=(),
            warnings=(),
            sources=(),
            model="fake",
            provider="fake",
            prompt_version="v1",
            prompt_tokens=1,
            completion_tokens=1,
            grounded=False,
            created_at="2026-10-06T00:00:00+00:00",
            updated_at="2026-10-06T00:00:00+00:00",
        )


def _auth(subject: str) -> dict[str, str]:
    token = jwt.encode({"sub": subject}, "change-me", algorithm="HS256")
    return {"Authorization": f"Bearer {token}"}


def test_knowledge_note_null_options_are_accepted(client: TestClient) -> None:
    service = _Authoring()
    client.app.dependency_overrides[get_authoring_service] = lambda: service  # type: ignore[attr-defined]
    response = client.post(
        "/api/v1/ai/authoring/proposals",
        headers=_auth("user-a"),
        json={
            "operation": "IMPROVE",
            "topic": "Kafka",
            "instructions": "Tighten the opening.",
            "sourceContent": "Authoritative note",
            "contentId": "11111111-1111-1111-1111-111111111111",
            "sourceVersion": 11,
            "difficulty": None,
            "questionCount": 5,
            "conversationId": None,
            "useConversation": None,
            "useKnowledge": True,
        },
    )
    assert response.status_code == 200
    body = response.json()
    assert body["proposalId"] == "proposal-1"
    assert body["content"] == "## Draft\n\nImproved note."
    assert service.command is not None
    assert service.command.operation is AuthoringOperation.IMPROVE
    assert service.command.topic == "Kafka"
    assert service.command.source_content == "Authoritative note"
    assert service.command.content_id == "11111111-1111-1111-1111-111111111111"
    assert service.command.source_version == 11
    assert service.command.difficulty == ""
    assert service.command.use_conversation is False
    assert service.command.use_knowledge is True


async def test_chunked_knowledge_note_body_is_accepted(client: TestClient) -> None:
    service = _Authoring()
    client.app.dependency_overrides[get_authoring_service] = lambda: service  # type: ignore[attr-defined]
    note = "REST resources.\n\n" + ("Follow the uniform interface. " * 400)
    payload = json.dumps(
        {
            "operation": "IMPROVE",
            "topic": "REST Principles and URI Design Rules",
            "instructions": "",
            "sourceContent": note,
            "contentId": "11111111-1111-1111-1111-111111111111",
            "sourceVersion": 3,
            "difficulty": None,
            "questionCount": 5,
            "conversationId": None,
            "useConversation": None,
            "useKnowledge": True,
        }
    ).encode()
    midpoint = len(payload) // 2
    pieces = [payload[:midpoint], payload[midpoint:]]
    index = 0
    messages: list[dict[str, object]] = []

    async def receive() -> dict[str, object]:
        nonlocal index
        if index < len(pieces):
            body = pieces[index]
            index += 1
            return {
                "type": "http.request",
                "body": body,
                "more_body": index < len(pieces),
            }
        return {"type": "http.disconnect"}

    async def send(message: dict[str, object]) -> None:
        messages.append(message)

    token = _auth("user-a")["Authorization"].encode()
    await client.app(  # type: ignore[attr-defined]
        {
            "type": "http",
            "asgi": {"version": "3.0"},
            "http_version": "1.1",
            "method": "POST",
            "scheme": "http",
            "path": "/api/v1/ai/authoring/proposals",
            "raw_path": b"/api/v1/ai/authoring/proposals",
            "query_string": b"",
            "headers": [
                (b"content-type", b"application/json"),
                (b"authorization", token),
            ],
            "client": ("127.0.0.1", 123),
            "server": ("test", 80),
        },
        receive,
        send,
    )
    start = next(message for message in messages if message["type"] == "http.response.start")
    assert start["status"] == 200
    assert service.command is not None
    assert service.command.source_content == note


def test_gzip_knowledge_note_body_is_accepted(client: TestClient) -> None:
    service = _Authoring()
    client.app.dependency_overrides[get_authoring_service] = lambda: service  # type: ignore[attr-defined]
    note = "REST resources.\n\n" + ("Follow the uniform interface. " * 80)
    payload = json.dumps(
        {
            "operation": "IMPROVE",
            "topic": "REST Principles and URI Design Rules",
            "instructions": "",
            "sourceContent": note,
            "contentId": "11111111-1111-1111-1111-111111111111",
            "sourceVersion": 3,
            "difficulty": None,
            "conversationId": None,
            "useConversation": None,
            "useKnowledge": True,
        }
    ).encode()
    compressed = gzip.compress(payload)
    assert compressed[1] == 0x8B
    response = client.post(
        "/api/v1/ai/authoring/proposals",
        content=compressed,
        headers={
            **_auth("user-a"),
            "Content-Type": "application/json",
            "Content-Encoding": "gzip",
        },
    )
    assert response.status_code == 200
    assert service.command is not None
    assert service.command.source_content == note
