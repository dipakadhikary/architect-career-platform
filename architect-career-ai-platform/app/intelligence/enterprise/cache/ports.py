"""Semantic and response cache ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any


class SemanticCachePort(ABC):
    @abstractmethod
    async def get_embedding(self, key: str) -> list[float] | None:
        raise NotImplementedError

    @abstractmethod
    async def set_embedding(self, key: str, value: list[float], ttl_seconds: int) -> None:
        raise NotImplementedError

    @abstractmethod
    async def get_prompt(self, key: str) -> dict[str, Any] | None:
        raise NotImplementedError

    @abstractmethod
    async def set_prompt(self, key: str, value: dict[str, Any], ttl_seconds: int) -> None:
        raise NotImplementedError

    @abstractmethod
    async def get_response(self, key: str) -> dict[str, Any] | None:
        raise NotImplementedError

    @abstractmethod
    async def set_response(self, key: str, value: dict[str, Any], ttl_seconds: int) -> None:
        raise NotImplementedError

    @abstractmethod
    async def get_similar(
        self, embedding: list[float], *, threshold: float, namespace: str
    ) -> dict[str, Any] | None:
        raise NotImplementedError

    @abstractmethod
    async def put_similar(
        self,
        embedding: list[float],
        value: dict[str, Any],
        *,
        namespace: str,
        ttl_seconds: int,
    ) -> None:
        raise NotImplementedError
