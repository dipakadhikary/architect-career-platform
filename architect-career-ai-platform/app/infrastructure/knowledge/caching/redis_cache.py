"""Redis-backed RAG cache with in-memory fallback."""

from __future__ import annotations

import json
from typing import Any

from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.intelligence.knowledge.caching.ports import RagCachePort


class RedisRagCache(RagCachePort):
    def __init__(self, redis_adapter: RedisAdapter) -> None:
        self._redis = redis_adapter
        self._memory: dict[str, Any] = {}

    async def get_embedding(self, key: str) -> list[float] | None:
        raw = await self._get(f"emb:{key}")
        return list(raw) if isinstance(raw, list) else None

    async def set_embedding(self, key: str, value: list[float], ttl_seconds: int) -> None:
        await self._set(f"emb:{key}", value, ttl_seconds)

    async def get_retrieval(self, key: str) -> list[dict[str, Any]] | None:
        raw = await self._get(f"ret:{key}")
        return list(raw) if isinstance(raw, list) else None

    async def set_retrieval(self, key: str, value: list[dict[str, Any]], ttl_seconds: int) -> None:
        await self._set(f"ret:{key}", value, ttl_seconds)

    async def get_prompt(self, key: str) -> dict[str, Any] | None:
        raw = await self._get(f"prm:{key}")
        return dict(raw) if isinstance(raw, dict) else None

    async def set_prompt(self, key: str, value: dict[str, Any], ttl_seconds: int) -> None:
        await self._set(f"prm:{key}", value, ttl_seconds)

    async def get_llm_response(self, key: str) -> dict[str, Any] | None:
        raw = await self._get(f"llm:{key}")
        return dict(raw) if isinstance(raw, dict) else None

    async def set_llm_response(self, key: str, value: dict[str, Any], ttl_seconds: int) -> None:
        await self._set(f"llm:{key}", value, ttl_seconds)

    async def _get(self, key: str) -> Any | None:
        if key in self._memory:
            return self._memory[key]
        if not self._redis.enabled:
            return None
        payload = await self._redis.read(key)
        if not payload or "json" not in payload:
            return None
        return json.loads(payload["json"])

    async def _set(self, key: str, value: Any, ttl_seconds: int) -> None:
        self._memory[key] = value
        if self._redis.enabled:
            await self._redis.write(key, {"json": json.dumps(value), "ttl": str(ttl_seconds)})
