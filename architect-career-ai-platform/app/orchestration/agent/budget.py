"""Hard stops for steps, tool calls, tokens, time, and estimated cost."""

from __future__ import annotations

import time
from dataclasses import dataclass


@dataclass(slots=True)
class BudgetManager:
    max_steps: int
    max_tool_calls: int
    max_tokens: int
    max_seconds: float
    max_cost_usd: float
    usd_per_1k_tokens: float
    started: float
    steps: int = 0
    tool_calls: int = 0
    tokens: int = 0

    @classmethod
    def start(
        cls,
        *,
        max_steps: int,
        max_tool_calls: int,
        max_tokens: int,
        max_seconds: float,
        max_cost_usd: float,
        usd_per_1k_tokens: float,
    ) -> BudgetManager:
        return cls(
            max_steps=max_steps,
            max_tool_calls=max_tool_calls,
            max_tokens=max_tokens,
            max_seconds=max_seconds,
            max_cost_usd=max_cost_usd,
            usd_per_1k_tokens=usd_per_1k_tokens,
            started=time.monotonic(),
        )

    def add_tokens(self, prompt_tokens: int, completion_tokens: int) -> None:
        self.tokens += max(prompt_tokens, 0) + max(completion_tokens, 0)
        self.steps += 1

    def add_tool_call(self) -> None:
        self.tool_calls += 1

    @property
    def estimated_cost_usd(self) -> float:
        return (self.tokens / 1000) * self.usd_per_1k_tokens

    def blocked(self) -> str:
        if time.monotonic() - self.started > self.max_seconds:
            return "AGENT_TIMEOUT"
        if self.steps > self.max_steps:
            return "STEP_BUDGET"
        if self.tool_calls > self.max_tool_calls:
            return "TOOL_BUDGET"
        if self.tokens > self.max_tokens:
            return "TOKEN_BUDGET"
        if self.estimated_cost_usd > self.max_cost_usd:
            return "COST_BUDGET"
        return ""
