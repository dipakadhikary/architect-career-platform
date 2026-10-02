"""Prometheus metrics endpoint."""

from __future__ import annotations

from fastapi import APIRouter, Response

from app.shared.observability.metrics import get_metrics

router = APIRouter(tags=["System"])


@router.get("/api/v1/system/metrics")
async def metrics() -> Response:
    payload, content_type = get_metrics().render()
    return Response(content=payload, media_type=content_type)
