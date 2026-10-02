"""RAG caching ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any


class RagCachePort(ABC):
    @abstractmethod
    async def get_embedding(self, key: str) -> list[float] | None:
        raise NotImplementedError

    @abstractmethod
    async def set_embedding(self, key: str, value: list[float], ttl_seconds: int) -> None:
        raise NotImplementedError

    @abstractmethod
    async def get_retrieval(self, key: str) -> list[dict[str, Any]] | None:
        raise NotImplementedError

    @abstractmethod
    async def set_retrieval(self, key: str, value: list[dict[str, Any]], ttl_seconds: int) -> None:
        raise NotImplementedError

    @abstractmethod
    async def get_prompt(self, key: str) -> dict[str, Any] | None:
        raise NotImplementedError

    @abstractmethod
    async def set_prompt(self, key: str, value: dict[str, Any], ttl_seconds: int) -> None:
        raise NotImplementedError

    @abstractmethod
    async def get_llm_response(self, key: str) -> dict[str, Any] | None:
        raise NotImplementedError

    @abstractmethod
    async def set_llm_response(self, key: str, value: dict[str, Any], ttl_seconds: int) -> None:
        raise NotImplementedError
