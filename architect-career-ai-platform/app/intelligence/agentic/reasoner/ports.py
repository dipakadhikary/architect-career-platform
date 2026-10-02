"""Reasoning capability port."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any


class ReasonerPort(ABC):
    @abstractmethod
    async def reason(self, prompt: str, context: dict[str, Any] | None = None) -> str:
        raise NotImplementedError
