"""FastAPI application entrypoint."""

from __future__ import annotations

from collections.abc import AsyncIterator
from contextlib import asynccontextmanager

import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app import __version__
from app.api.exceptions import register_exception_handlers
from app.api.middleware.rate_limit import RateLimitMiddleware
from app.api.middleware.request_context import RequestContextMiddleware
from app.api.router import api_router
from app.shared.config.settings import get_settings
from app.shared.di.container import bootstrap_observability, container
from app.shared.logging.setup import configure_logging, get_logger


@asynccontextmanager
async def lifespan(_app: FastAPI) -> AsyncIterator[None]:
    settings = get_settings()
    configure_logging(level=settings.log_level, json_logs=settings.log_json)
    logger = get_logger(__name__)
    settings.validate_for_runtime()
    bootstrap_observability()
    logger.info("platform.startup", version=__version__, env=settings.app_env)
    try:
        yield
    finally:
        redis_adapter = container.redis_adapter()
        await redis_adapter.close()
        langfuse_adapter = container.langfuse_adapter()
        langfuse_adapter.flush()
        logger.info("platform.shutdown")


def create_app() -> FastAPI:
    settings = get_settings()
    application = FastAPI(
        title="ACOS AI Platform",
        version=settings.app_version,
        description="Reusable, domain-agnostic AI Platform foundation",
        lifespan=lifespan,
        docs_url="/docs",
        redoc_url="/redoc",
        openapi_url="/openapi.json",
    )
    application.add_middleware(
        CORSMiddleware,
        allow_origins=settings.cors_origins,
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )
    application.add_middleware(RequestContextMiddleware)
    application.add_middleware(RateLimitMiddleware, settings=settings)
    register_exception_handlers(application)
    application.include_router(api_router)
    return application


app = create_app()


def run() -> None:
    settings = get_settings()
    uvicorn.run(
        "app.main:app",
        host=settings.app_host,
        port=settings.app_port,
        reload=settings.app_debug and not settings.is_production,
    )


if __name__ == "__main__":
    run()
