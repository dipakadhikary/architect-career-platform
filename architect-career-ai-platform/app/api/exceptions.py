"""Global exception handlers producing RFC 9457 Problem Details."""

from __future__ import annotations

from typing import Any

from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from starlette.exceptions import HTTPException as StarletteHTTPException

from app.shared.constants.headers import HeaderNames
from app.shared.constants.media import MediaTypes
from app.shared.context.request_context import get_request_context
from app.shared.exceptions import PlatformError


def _problem_headers() -> dict[str, str]:
    context = get_request_context()
    headers: dict[str, str] = {}
    if context is not None:
        headers[HeaderNames.CORRELATION_ID] = context.correlation_id
        headers[HeaderNames.REQUEST_ID] = context.request_id
        headers[HeaderNames.TRACE_ID] = context.trace_id
    return headers


def _problem_body(
    *,
    type_uri: str,
    title: str,
    status: int,
    detail: str,
    code: str,
    errors: list[dict[str, Any]] | None = None,
    extensions: dict[str, Any] | None = None,
) -> dict[str, Any]:
    body: dict[str, Any] = {
        "type": type_uri,
        "title": title,
        "status": status,
        "detail": detail,
        "code": code,
    }
    context = get_request_context()
    if context is not None:
        body["correlationId"] = context.correlation_id
        body["requestId"] = context.request_id
        body["traceId"] = context.trace_id
    if errors:
        body["errors"] = errors
    if extensions:
        body.update(extensions)
    return body


def register_exception_handlers(app: FastAPI) -> None:
    @app.exception_handler(PlatformError)
    async def platform_error_handler(_request: Request, exc: PlatformError) -> JSONResponse:
        return JSONResponse(
            status_code=exc.status,
            content=_problem_body(
                type_uri=exc.type_uri,
                title=exc.title,
                status=exc.status,
                detail=exc.detail,
                code=exc.code,
                errors=exc.errors,
                extensions=exc.extensions,
            ),
            media_type=MediaTypes.PROBLEM_JSON,
            headers=_problem_headers(),
        )

    @app.exception_handler(RequestValidationError)
    async def validation_error_handler(
        _request: Request, exc: RequestValidationError
    ) -> JSONResponse:
        errors = [
            {
                "field": ".".join(str(part) for part in error.get("loc", [])),
                "message": error.get("msg", "invalid"),
            }
            for error in exc.errors()
        ]
        return JSONResponse(
            status_code=400,
            content=_problem_body(
                type_uri="https://acos.local/problems/ai-validation-failed",
                title="Validation Failed",
                status=400,
                detail="Request validation failed",
                code="AI_VALIDATION_FAILED",
                errors=errors,
            ),
            media_type=MediaTypes.PROBLEM_JSON,
            headers=_problem_headers(),
        )

    @app.exception_handler(StarletteHTTPException)
    async def http_error_handler(_request: Request, exc: StarletteHTTPException) -> JSONResponse:
        return JSONResponse(
            status_code=exc.status_code,
            content=_problem_body(
                type_uri="about:blank",
                title="HTTP Error",
                status=exc.status_code,
                detail=str(exc.detail),
                code="AI_HTTP_ERROR",
            ),
            media_type=MediaTypes.PROBLEM_JSON,
            headers=_problem_headers(),
        )

    @app.exception_handler(Exception)
    async def unhandled_error_handler(_request: Request, _exc: Exception) -> JSONResponse:
        return JSONResponse(
            status_code=500,
            content=_problem_body(
                type_uri="https://acos.local/problems/ai-internal-error",
                title="Internal Server Error",
                status=500,
                detail="An unexpected error occurred",
                code="AI_INTERNAL_ERROR",
            ),
            media_type=MediaTypes.PROBLEM_JSON,
            headers=_problem_headers(),
        )
