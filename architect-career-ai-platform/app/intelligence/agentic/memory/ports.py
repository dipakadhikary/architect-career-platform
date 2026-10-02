"""Layered memory capability port."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any

from app.intelligence.agentic.models import MemoryRecord, MemoryScope


class AgenticMemoryPort(ABC):
    @abstractmethod
    async def get(self, key: str, scope: MemoryScope) -> dict[str, Any] | None:
        raise NotImplementedError

    @abstractmethod
    async def put(self, record: MemoryRecord) -> None:
        raise NotImplementedError

    @abstractmethod
    async def append(self, key: str, scope: MemoryScope, item: dict[str, Any]) -> None:
        raise NotImplementedError

    @abstractmethod
    async def clear(self, key: str, scope: MemoryScope) -> None:
        raise NotImplementedError
