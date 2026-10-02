"""LLM provider port (Adapter Pattern)."""

from __future__ import annotations

from abc import ABC, abstractmethod
from collections.abc import AsyncIterator
from dataclasses import dataclass, field
from typing import Any

from app.shared.observability.usage import CostPlaceholder, TokenUsagePlaceholder


@dataclass(slots=True, frozen=True)
class LlmMessage:
    role: str
    content: str


@dataclass(slots=True, frozen=True)
class LlmCompletionRequest:
    messages: list[LlmMessage]
    model: str | None = None
    temperature: float | None = None
    max_tokens: int | None = None
    metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class LlmCompletionResponse:
    content: str
    model: str
    provider: str
    usage: TokenUsagePlaceholder = field(default_factory=TokenUsagePlaceholder)
    cost: CostPlaceholder = field(default_factory=CostPlaceholder)
    raw: dict[str, Any] = field(default_factory=dict)


class LlmPort(ABC):
    """Inbound port for language model completions."""

    @property
    @abstractmethod
    def provider_name(self) -> str:
        raise NotImplementedError

    @abstractmethod
    async def complete(self, request: LlmCompletionRequest) -> LlmCompletionResponse:
        raise NotImplementedError

    @abstractmethod
    def stream(self, request: LlmCompletionRequest) -> AsyncIterator[str]:
        raise NotImplementedError

    @abstractmethod
    async def health_check(self) -> bool:
        raise NotImplementedError
