"""Root API router composition."""

from __future__ import annotations

from fastapi import APIRouter

from app.api.v1 import (
    career,
    chat,
    health,
    knowledge,
    learning,
    liveness,
    metrics,
    portfolio,
    readiness,
)

api_router = APIRouter()
api_router.include_router(health.router)
api_router.include_router(knowledge.router)
api_router.include_router(chat.router)
api_router.include_router(learning.router)
api_router.include_router(career.router)
api_router.include_router(portfolio.router)
api_router.include_router(liveness.router)
api_router.include_router(readiness.router)
api_router.include_router(metrics.router)
