"""Memory port."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any


class MemoryPort(ABC):
    @abstractmethod
    async def read(self, key: str) -> dict[str, Any] | None:
        raise NotImplementedError

    @abstractmethod
    async def write(self, key: str, value: dict[str, Any]) -> None:
        raise NotImplementedError
