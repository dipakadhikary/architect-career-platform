"""Redis adapter behind a narrow platform interface."""

from __future__ import annotations

from typing import Any

import redis.asyncio as redis

from app.intelligence.memory.ports import MemoryPort
from app.shared.config.settings import AppSettings


class RedisAdapter(MemoryPort):
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings
        self._client: redis.Redis | None = None
        if settings.redis_enabled:
            self._client = redis.from_url(settings.redis_url, decode_responses=True)

    @property
    def enabled(self) -> bool:
        return self._client is not None

    async def ping(self) -> bool:
        if self._client is None:
            return False
        return bool(await self._client.ping())

    async def read(self, key: str) -> dict[str, Any] | None:
        if self._client is None:
            return None
        value = await self._client.hgetall(key)
        return dict(value) if value else None

    async def write(self, key: str, value: dict[str, Any]) -> None:
        if self._client is None:
            return
        await self._client.hset(key, mapping={str(k): str(v) for k, v in value.items()})

    async def expire(self, key: str, ttl_seconds: int) -> None:
        if self._client is None or ttl_seconds <= 0:
            return
        await self._client.expire(key, ttl_seconds)

    async def close(self) -> None:
        if self._client is not None:
            await self._client.aclose()
