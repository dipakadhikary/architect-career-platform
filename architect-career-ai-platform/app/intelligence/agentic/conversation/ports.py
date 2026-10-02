"""Conversation manager capability port."""

from __future__ import annotations

from abc import ABC, abstractmethod

from app.intelligence.agentic.models import ConversationState, ConversationTurn


class ConversationManagerPort(ABC):
    @abstractmethod
    async def get_or_create(
        self, *, user_id: str, conversation_id: str | None = None
    ) -> ConversationState:
        raise NotImplementedError

    @abstractmethod
    async def append_turn(self, conversation_id: str, turn: ConversationTurn) -> ConversationState:
        raise NotImplementedError

    @abstractmethod
    async def load(self, conversation_id: str) -> ConversationState | None:
        raise NotImplementedError
