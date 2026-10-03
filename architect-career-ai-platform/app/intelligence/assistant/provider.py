"""Provider port used by the assistant orchestrator."""

from __future__ import annotations

from typing import Protocol

from app.intelligence.assistant.models import ChatMessage, NormalizedCompletion


class LlmProvider(Protocol):
    """Minimum chat capability for Phase 1. Business code depends on this port only."""

    provider_name: str

    async def chat(
        self,
        messages: list[ChatMessage],
        *,
        model: str,
        temperature: float,
        max_tokens: int,
    ) -> NormalizedCompletion:
        """Return a normalized completion. Implementations must not leak SDK types."""
