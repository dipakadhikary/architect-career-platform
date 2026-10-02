"""Prompt template port (no prompt content here)."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any


class PromptPort(ABC):
    @abstractmethod
    async def render(self, name: str, variables: dict[str, Any]) -> str:
        raise NotImplementedError
