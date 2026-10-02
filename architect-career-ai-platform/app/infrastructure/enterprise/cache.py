"""Semantic similarity cache with Redis + in-memory fallback and real TTL support."""

from __future__ import annotations

import json
import math
from typing import Any

from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.intelligence.enterprise.cache.ports import SemanticCachePort


class RedisSemanticCache(SemanticCachePort):
    def __init__(self, redis_adapter: RedisAdapter) -> None:
        self._redis = redis_adapter
        self._memory: dict[str, Any] = {}
        self._vectors: dict[str, list[tuple[list[float], dict[str, Any]]]] = {}

    async def get_embedding(self, key: str) -> list[float] | None:
        raw = await self._get(f"sem:emb:{key}")
        return list(raw) if isinstance(raw, list) else None

    async def set_embedding(self, key: str, value: list[float], ttl_seconds: int) -> None:
        await self._set(f"sem:emb:{key}", value, ttl_seconds)

    async def get_prompt(self, key: str) -> dict[str, Any] | None:
        raw = await self._get(f"sem:prm:{key}")
        return dict(raw) if isinstance(raw, dict) else None

    async def set_prompt(self, key: str, value: dict[str, Any], ttl_seconds: int) -> None:
        await self._set(f"sem:prm:{key}", value, ttl_seconds)

    async def get_response(self, key: str) -> dict[str, Any] | None:
        raw = await self._get(f"sem:rsp:{key}")
        return dict(raw) if isinstance(raw, dict) else None

    async def set_response(self, key: str, value: dict[str, Any], ttl_seconds: int) -> None:
        await self._set(f"sem:rsp:{key}", value, ttl_seconds)

    async def get_similar(
        self, embedding: list[float], *, threshold: float, namespace: str
    ) -> dict[str, Any] | None:
        entries = self._vectors.get(namespace, [])
        best: tuple[float, dict[str, Any]] | None = None
        for vector, payload in entries:
            score = _cosine(embedding, vector)
            if score >= threshold and (best is None or score > best[0]):
                best = (score, payload)
        return dict(best[1]) if best else None

    async def put_similar(
        self,
        embedding: list[float],
        value: dict[str, Any],
        *,
        namespace: str,
        ttl_seconds: int,
    ) -> None:
        self._vectors.setdefault(namespace, []).append((list(embedding), dict(value)))
        await self._set(
            f"sem:sim:{namespace}:{len(self._vectors[namespace])}",
            {"embedding": embedding, "value": value},
            ttl_seconds,
        )

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
            await self._redis.expire(key, ttl_seconds)


def _cosine(left: list[float], right: list[float]) -> float:
    size = min(len(left), len(right))
    if size == 0:
        return 0.0
    dot = sum(left[i] * right[i] for i in range(size))
    left_norm = math.sqrt(sum(left[i] * left[i] for i in range(size))) or 1.0
    right_norm = math.sqrt(sum(right[i] * right[i] for i in range(size))) or 1.0
    return dot / (left_norm * right_norm)
