"""Contract-aligned platform health endpoint."""

from __future__ import annotations

from acos_ai_contracts.models.get_ai_platform_health200_response import (
    GetAiPlatformHealth200Response,
)
from acos_ai_contracts.models.get_ai_platform_health200_response_dependencies_value import (
    GetAiPlatformHealth200ResponseDependenciesValue,
)
from fastapi import APIRouter, Depends

from app.api.dependencies import get_qdrant_adapter, get_redis_adapter
from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.infrastructure.vector.qdrant_adapter import QdrantAdapter
from app.shared.config.settings import AppSettings, get_settings
from app.shared.utils.time import utc_now

router = APIRouter(tags=["Health"])


@router.get(
    "/api/v1/ai/health",
    response_model=GetAiPlatformHealth200Response,
    response_model_by_alias=True,
    summary="AI Platform health",
)
async def get_ai_platform_health(
    settings: AppSettings = Depends(get_settings),
    redis_adapter: RedisAdapter = Depends(get_redis_adapter),
    qdrant_adapter: QdrantAdapter = Depends(get_qdrant_adapter),
) -> GetAiPlatformHealth200Response:
    dependencies: dict[str, GetAiPlatformHealth200ResponseDependenciesValue] = {}

    if redis_adapter.enabled:
        redis_ok = await redis_adapter.ping()
        dependencies["redis"] = GetAiPlatformHealth200ResponseDependenciesValue(
            status="AVAILABLE" if redis_ok else "UNAVAILABLE",
            message="ok" if redis_ok else "redis ping failed",
        )

    if qdrant_adapter.enabled:
        try:
            qdrant_ok = qdrant_adapter.health_check()
        except Exception:
            qdrant_ok = False
        dependencies["qdrant"] = GetAiPlatformHealth200ResponseDependenciesValue(
            status="AVAILABLE" if qdrant_ok else "UNAVAILABLE",
            message="ok" if qdrant_ok else "qdrant unavailable",
        )

    statuses = [item.status for item in dependencies.values()]
    if not statuses:
        aggregate = "AVAILABLE"
    elif all(status == "AVAILABLE" for status in statuses):
        aggregate = "AVAILABLE"
    elif any(status == "AVAILABLE" for status in statuses):
        aggregate = "DEGRADED"
    else:
        aggregate = "UNAVAILABLE"

    return GetAiPlatformHealth200Response(
        status=aggregate,
        message="AI Platform is healthy" if aggregate == "AVAILABLE" else "AI Platform degraded",
        checkedAt=utc_now(),
        enabled=True,
        version=settings.app_version,
        dependencies=dependencies or None,
    )
