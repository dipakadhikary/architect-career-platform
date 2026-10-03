"""Builds the provider message list. User text never enters the trusted system instruction."""

from __future__ import annotations

from app.intelligence.assistant.models import ChatMessage, ChatRole


class PromptBuilder:
    """Separates the application instruction from untrusted caller messages."""

    def build(self, instruction: str, messages: list[ChatMessage]) -> list[ChatMessage]:
        """Return the trusted system message followed by caller turns.

        Caller messages with role ``system`` are sent as user input so they cannot
        replace the application instruction. Retrieved documents are not included.
        """
        prompt: list[ChatMessage] = []
        trusted = instruction.strip()
        if trusted:
            prompt.append(ChatMessage(role=ChatRole.SYSTEM, content=trusted))
        prompt.extend(self._as_untrusted(message) for message in messages)
        return prompt

    def _as_untrusted(self, message: ChatMessage) -> ChatMessage:
        if message.role == ChatRole.SYSTEM:
            return ChatMessage(role=ChatRole.USER, content=message.content)
        return message
