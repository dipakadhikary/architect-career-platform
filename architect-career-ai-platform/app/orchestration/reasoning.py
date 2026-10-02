"""Reasoning extension point."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any


class Reasoner(ABC):
    @abstractmethod
    async def reason(self, prompt: str, context: dict[str, Any]) -> str:
        raise NotImplementedError
