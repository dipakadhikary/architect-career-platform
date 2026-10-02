"""Kubernetes-style liveness probe."""

from __future__ import annotations

from fastapi import APIRouter
from pydantic import BaseModel, Field

router = APIRouter(tags=["System"])


class LivenessResponse(BaseModel):
    status: str = Field(examples=["UP"])


@router.get("/api/v1/system/liveness", response_model=LivenessResponse)
async def liveness() -> LivenessResponse:
    return LivenessResponse(status="UP")
