"""Kubernetes-style readiness probe."""

from __future__ import annotations

from fastapi import APIRouter, Depends
from pydantic import BaseModel, Field

from app.api.dependencies import get_qdrant_adapter, get_redis_adapter
from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.infrastructure.vector.qdrant_adapter import QdrantAdapter

router = APIRouter(tags=["System"])


class ReadinessResponse(BaseModel):
    status: str = Field(examples=["READY"])
    checks: dict[str, str]


@router.get("/api/v1/system/readiness", response_model=ReadinessResponse)
async def readiness(
    redis_adapter: RedisAdapter = Depends(get_redis_adapter),
    qdrant_adapter: QdrantAdapter = Depends(get_qdrant_adapter),
) -> ReadinessResponse:
    checks: dict[str, str] = {"process": "UP"}

    if redis_adapter.enabled:
        checks["redis"] = "UP" if await redis_adapter.ping() else "DOWN"

    if qdrant_adapter.enabled:
        try:
            checks["qdrant"] = "UP" if qdrant_adapter.health_check() else "DOWN"
        except Exception:
            checks["qdrant"] = "DOWN"

    status = "READY" if all(value == "UP" for value in checks.values()) else "NOT_READY"
    return ReadinessResponse(status=status, checks=checks)
