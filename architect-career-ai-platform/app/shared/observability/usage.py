"""Token and cost tracking placeholders used by infrastructure adapters."""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(slots=True, frozen=True)
class TokenUsagePlaceholder:
    prompt_tokens: int = 0
    completion_tokens: int = 0
    total_tokens: int = 0


@dataclass(slots=True, frozen=True)
class CostPlaceholder:
    currency: str = "USD"
    amount: float = 0.0
