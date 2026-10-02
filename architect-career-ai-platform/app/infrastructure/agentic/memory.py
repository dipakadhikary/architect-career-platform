"""Redis-backed layered agentic memory with in-memory fallback."""

from __future__ import annotations

import json
from typing import Any

from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.intelligence.agentic.memory.ports import AgenticMemoryPort
from app.intelligence.agentic.models import (
    CapabilityDescriptor,
    CapabilityKind,
    MemoryRecord,
    MemoryScope,
)


class RedisAgenticMemory(AgenticMemoryPort):
    def __init__(self, redis_adapter: RedisAdapter) -> None:
        self._redis = redis_adapter
        self._local: dict[str, dict[str, Any]] = {}

    @property
    def descriptor(self) -> CapabilityDescriptor:
        return CapabilityDescriptor(
            name="memory",
            kind=CapabilityKind.MEMORY,
            description="Conversation/session/long/short/working memory",
        )

    def _key(self, key: str, scope: MemoryScope) -> str:
        return f"agentic:memory:{scope.value}:{key}"

    async def get(self, key: str, scope: MemoryScope) -> dict[str, Any] | None:
        storage_key = self._key(key, scope)
        if storage_key in self._local:
            return dict(self._local[storage_key])
        if not self._redis.enabled:
            return None
        raw = await self._redis.read(storage_key)
        if not raw or "json" not in raw:
            return None
        return json.loads(raw["json"])

    async def put(self, record: MemoryRecord) -> None:
        storage_key = self._key(record.key, record.scope)
        self._local[storage_key] = dict(record.payload)
        if self._redis.enabled:
            await self._redis.write(
                storage_key,
                {"json": json.dumps(record.payload), "ttl": str(record.ttl_seconds or 0)},
            )

    async def append(self, key: str, scope: MemoryScope, item: dict[str, Any]) -> None:
        current = await self.get(key, scope) or {"items": []}
        items = list(current.get("items") or [])
        items.append(item)
        await self.put(MemoryRecord(key=key, scope=scope, payload={"items": items}))

    async def clear(self, key: str, scope: MemoryScope) -> None:
        storage_key = self._key(key, scope)
        self._local.pop(storage_key, None)

    async def invoke(self, payload: dict[str, Any]) -> dict[str, Any]:
        action = str(payload.get("action") or "get")
        key = str(payload.get("key") or payload.get("user_id") or "default")
        scope = MemoryScope(str(payload.get("scope") or MemoryScope.WORKING.value))
        if action in {"load_conversation", "get"}:
            data = await self.get(key, scope)
            return {"payload": data or {}}
        if action in {"save_turn", "put"}:
            await self.put(
                MemoryRecord(
                    key=key,
                    scope=scope,
                    payload=dict(payload.get("payload") or payload),
                )
            )
            return {"saved": True}
        if action == "append":
            await self.append(key, scope, dict(payload.get("item") or {}))
            return {"appended": True}
        if action == "clear":
            await self.clear(key, scope)
            return {"cleared": True}
        return {"unsupported": action}
