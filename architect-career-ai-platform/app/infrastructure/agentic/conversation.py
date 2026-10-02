"""Conversation manager using layered memory."""

from __future__ import annotations

import uuid
from typing import Any

from app.intelligence.agentic.conversation.ports import ConversationManagerPort
from app.intelligence.agentic.memory.ports import AgenticMemoryPort
from app.intelligence.agentic.models import (
    CapabilityDescriptor,
    CapabilityKind,
    ConversationState,
    ConversationTurn,
    MemoryRecord,
    MemoryScope,
)


class MemoryConversationManager(ConversationManagerPort):
    def __init__(self, memory: AgenticMemoryPort) -> None:
        self._memory = memory

    @property
    def descriptor(self) -> CapabilityDescriptor:
        return CapabilityDescriptor(
            name="conversation_manager",
            kind=CapabilityKind.CONVERSATION_MANAGER,
            description="Conversation lifecycle and turn tracking",
        )

    async def get_or_create(
        self, *, user_id: str, conversation_id: str | None = None
    ) -> ConversationState:
        conversation_id = conversation_id or str(uuid.uuid4())
        existing = await self.load(conversation_id)
        if existing is not None:
            return existing
        state = ConversationState(conversation_id=conversation_id, user_id=user_id, turns=[])
        await self._persist(state)
        return state

    async def append_turn(self, conversation_id: str, turn: ConversationTurn) -> ConversationState:
        state = await self.load(conversation_id)
        if state is None:
            state = ConversationState(conversation_id=conversation_id, user_id="", turns=[])
        turns = list(state.turns)
        turns.append(turn)
        updated = ConversationState(
            conversation_id=state.conversation_id,
            user_id=state.user_id,
            turns=turns,
            metadata=dict(state.metadata),
        )
        await self._persist(updated)
        return updated

    async def load(self, conversation_id: str) -> ConversationState | None:
        payload = await self._memory.get(conversation_id, MemoryScope.CONVERSATION)
        if payload is None:
            return None
        turns = [
            ConversationTurn(role=str(item["role"]), content=str(item["content"]))
            for item in payload.get("turns", [])
        ]
        return ConversationState(
            conversation_id=conversation_id,
            user_id=str(payload.get("user_id") or ""),
            turns=turns,
            metadata=dict(payload.get("metadata") or {}),
        )

    async def invoke(self, payload: dict[str, Any]) -> dict[str, Any]:
        action = str(payload.get("action") or "get_or_create")
        if action == "append_turn":
            state = await self.append_turn(
                str(payload["conversation_id"]),
                ConversationTurn(role=str(payload["role"]), content=str(payload["content"])),
            )
        else:
            state = await self.get_or_create(
                user_id=str(payload.get("user_id") or ""),
                conversation_id=payload.get("conversation_id"),
            )
        return {
            "conversation_id": state.conversation_id,
            "user_id": state.user_id,
            "turns": [{"role": turn.role, "content": turn.content} for turn in state.turns],
        }

    async def _persist(self, state: ConversationState) -> None:
        await self._memory.put(
            MemoryRecord(
                key=state.conversation_id,
                scope=MemoryScope.CONVERSATION,
                payload={
                    "user_id": state.user_id,
                    "turns": [{"role": turn.role, "content": turn.content} for turn in state.turns],
                    "metadata": state.metadata,
                },
            )
        )
