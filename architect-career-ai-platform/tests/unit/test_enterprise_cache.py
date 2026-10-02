"""Unit tests for semantic cache."""

from __future__ import annotations

import pytest
from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.infrastructure.enterprise.cache import RedisSemanticCache
from app.shared.config.settings import AppSettings


@pytest.mark.asyncio
async def test_response_cache_roundtrip(settings: AppSettings) -> None:
    cache = RedisSemanticCache(RedisAdapter(settings))
    await cache.set_response("k1", {"message": "hello"}, ttl_seconds=60)
    hit = await cache.get_response("k1")
    assert hit == {"message": "hello"}


@pytest.mark.asyncio
async def test_semantic_similarity_hit(settings: AppSettings) -> None:
    cache = RedisSemanticCache(RedisAdapter(settings))
    vector = [1.0, 0.0, 0.0]
    await cache.put_similar(vector, {"message": "cached"}, namespace="chat", ttl_seconds=60)
    hit = await cache.get_similar(vector, threshold=0.9, namespace="chat")
    assert hit == {"message": "cached"}


@pytest.mark.asyncio
async def test_semantic_similarity_miss(settings: AppSettings) -> None:
    cache = RedisSemanticCache(RedisAdapter(settings))
    await cache.put_similar(
        [1.0, 0.0, 0.0], {"message": "cached"}, namespace="chat", ttl_seconds=60
    )
    hit = await cache.get_similar([0.0, 1.0, 0.0], threshold=0.9, namespace="chat")
    assert hit is None
