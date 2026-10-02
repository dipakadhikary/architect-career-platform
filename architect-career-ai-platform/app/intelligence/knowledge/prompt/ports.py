"""Prompt builder ports — templates loaded from files."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any

from app.intelligence.knowledge.models import PromptBundle


@dataclass(slots=True, frozen=True)
class PromptRenderRequest:
    name: str
    variables: dict[str, Any] = field(default_factory=dict)
    version: str | None = None
    few_shot_examples: list[dict[str, str]] = field(default_factory=list)


class PromptBuilderPort(ABC):
    @abstractmethod
    async def render(self, request: PromptRenderRequest) -> PromptBundle:
        raise NotImplementedError
