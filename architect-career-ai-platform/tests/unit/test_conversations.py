"""Conversation persistence, ownership, context limits, and mocked assistant replies."""

from __future__ import annotations

import pytest
from app.intelligence.assistant.models import (
    AnswerSource,
    CallerContext,
    ChatRequest,
    ChatResponse,
)
from app.orchestration.conversation.context import build_retrieval_query, select_history
from app.orchestration.conversation.service import (
    ConversationGenerationError,
    ConversationService,
)
from app.orchestration.conversation.store import ConversationStore, MessageRow
from app.shared.config.settings import AppSettings
from app.shared.exceptions import AuthorizationError, NotFoundError, ValidationFailedError
from app.shared.observability.metrics import PlatformMetrics
from prometheus_client import CollectorRegistry
from pydantic import SecretStr


def _settings() -> AppSettings:
    return AppSettings(
        app_env="test",
        redis_enabled=False,
        qdrant_enabled=False,
        langfuse_enabled=False,
        otel_enabled=False,
        auth_jwt_secret=SecretStr("change-me"),
        ai_enabled=True,
        rag_enabled=False,
        conversation_database=":memory:",
        conversation_max_messages=4,
        conversation_max_characters=200,
        conversation_message_limit=50,
        conversation_title_max_length=80,
    )  # type: ignore[arg-type]


def _metrics() -> PlatformMetrics:
    return PlatformMetrics(CollectorRegistry())


def _caller(owner: str) -> CallerContext:
    return CallerContext(owner_id=owner, auth_method="jwt")


class _FakeAssistant:
    def __init__(self) -> None:
        self.failures_remaining = 0
        self.requests: list[tuple[ChatRequest, str | None]] = []

    async def chat(
        self,
        request: ChatRequest,
        caller: CallerContext,
        *,
        retrieval_query: str | None = None,
    ) -> ChatResponse:
        del caller
        self.requests.append((request, retrieval_query))
        if self.failures_remaining:
            self.failures_remaining -= 1
            raise RuntimeError("llm down")
        return ChatResponse(
            answer="Kafka has brokers, topics, and partitions.\nSource: https://malicious.example",
            model="fake-model",
            provider="fake",
            grounded=True,
            sources=[
                AnswerSource(
                    content_id="kafka-concept",
                    title="Kafka",
                    content_type="CONCEPT",
                    section="Components",
                    path="Tutorials / Kafka",
                    url="/tutorials/kafka/concept",
                    chunk_id="chunk-kafka",
                    score=0.8,
                )
            ],
        )


def _service(
    assistant: _FakeAssistant | None = None,
) -> tuple[ConversationService, _FakeAssistant]:
    model = assistant or _FakeAssistant()
    service = ConversationService(
        settings=_settings(),
        store=ConversationStore(":memory:"),
        assistant=model,  # type: ignore[arg-type]
        metrics=_metrics(),
    )
    return service, model


def _row(content: str, sequence: int, role: str = "USER") -> MessageRow:
    return MessageRow(
        id=f"m-{sequence}",
        conversation_id="c",
        role=role,
        content=content,
        sequence_number=sequence,
        status="COMPLETED",
        created_at="2026-01-01T00:00:00+00:00",
        model="",
        provider="",
        prompt_tokens=0,
        completion_tokens=0,
        grounded=False,
        retrieval_query=content,
    )


def test_history_keeps_recent_messages_inside_the_budget() -> None:
    messages = [_row(f"message-{index}-abcdefghij", index) for index in range(1, 8)]
    selected = select_history(messages, max_messages=3, max_characters=50)
    assert [item.sequence_number for item in selected] == [6, 7]
    assert selected[0].content.startswith("message-6")


def test_follow_up_retrieval_keeps_the_original_question() -> None:
    original, retrieval = build_retrieval_query(
        "What are its main components?",
        "What is Kafka?",
        max_characters=200,
    )
    assert original == "What are its main components?"
    assert retrieval.startswith("What is Kafka?")
    assert original in retrieval


@pytest.mark.asyncio
async def test_owner_can_create_list_rename_and_delete_only_their_conversations() -> None:
    service, _assistant = _service()
    owner = _caller("user-a")
    other = _caller("user-b")
    created = service.create(owner)
    assert created.title == "New conversation"
    service.create(other)
    listed = service.list_conversations(owner, page=0, size=20)
    assert listed.total == 1
    assert listed.items[0].id == created.id
    renamed = service.rename(owner, created.id, "Kafka Architecture")
    assert renamed.title == "Kafka Architecture"
    with pytest.raises(ValidationFailedError):
        service.rename(owner, created.id, "   ")
    with pytest.raises(ValidationFailedError):
        service.rename(owner, created.id, "x" * 81)
    with pytest.raises(AuthorizationError):
        service.get(other, created.id)
    with pytest.raises(AuthorizationError):
        service.rename(other, created.id, "Stolen")
    with pytest.raises(AuthorizationError):
        service.delete(other, created.id)
    with pytest.raises(NotFoundError):
        service.get(owner, "missing")
    service.delete(owner, created.id)
    with pytest.raises(NotFoundError):
        service.get(owner, created.id)
    assert service.list_conversations(other, page=0, size=20).total == 1


@pytest.mark.asyncio
async def test_list_is_paged_and_newest_first() -> None:
    service, _assistant = _service()
    owner = _caller("user-a")
    first = service.create(owner)
    second = service.create(owner)
    service.rename(owner, second.id, "Newest title")
    page = service.list_conversations(owner, page=0, size=1)
    assert page.total == 2
    assert [item.id for item in page.items] == [second.id]
    next_page = service.list_conversations(owner, page=1, size=1)
    assert [item.id for item in next_page.items] == [first.id]


@pytest.mark.asyncio
async def test_send_persists_turns_context_and_trusted_sources() -> None:
    service, assistant = _service()
    owner = _caller("user-a")
    created = service.create(owner)
    first = await service.send(owner, created.id, "What is Kafka?", idempotency_key="k1")
    assert first.user_message.content == "What is Kafka?"
    assert first.assistant_message.status == "COMPLETED"
    assert first.assistant_message.sources[0].source_url == "/tutorials/kafka/concept"
    assert "malicious.example" not in first.assistant_message.sources[0].source_url
    second = await service.send(
        owner,
        created.id,
        "What are its main components?",
        idempotency_key="k2",
    )
    assert second.user_message.retrieval_query.startswith("What is Kafka?")
    history = assistant.requests[-1][0].messages
    assert history[0].content == "What is Kafka?"
    assert "What are its main components?" in history[-1].content
    detail = service.get(owner, created.id)
    assert [message.role for message in detail.messages] == [
        "USER",
        "ASSISTANT",
        "USER",
        "ASSISTANT",
    ]
    assert detail.conversation.title == "What is Kafka?"
    again = await service.send(owner, created.id, "What is Kafka?", idempotency_key="k1")
    assert again.assistant_message.id == first.assistant_message.id
    with pytest.raises(AuthorizationError):
        await service.send(_caller("user-b"), created.id, "hello", idempotency_key="nope")


@pytest.mark.asyncio
async def test_generation_failure_keeps_the_user_message_and_can_retry() -> None:
    service, assistant = _service()
    assistant.failures_remaining = 1
    owner = _caller("user-a")
    created = service.create(owner)
    with pytest.raises(ConversationGenerationError):
        await service.send(owner, created.id, "What is Kafka?", idempotency_key="retry-me")
    detail = service.get(owner, created.id)
    assert detail.messages[0].content == "What is Kafka?"
    assert detail.messages[1].status == "FAILED"
    retried = await service.send(owner, created.id, "What is Kafka?", idempotency_key="retry-me")
    assert retried.assistant_message.status == "COMPLETED"
    assert retried.user_message.id == detail.messages[0].id
    roles = [message.role for message in service.get(owner, created.id).messages]
    assert roles == ["USER", "ASSISTANT"]
