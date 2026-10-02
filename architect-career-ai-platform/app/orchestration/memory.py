"""Orchestration memory extension point."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any


class OrchestrationMemory(ABC):
    @abstractmethod
    async def load(self, session_id: str) -> dict[str, Any]:
        raise NotImplementedError

    @abstractmethod
    async def save(self, session_id: str, state: dict[str, Any]) -> None:
        raise NotImplementedError
