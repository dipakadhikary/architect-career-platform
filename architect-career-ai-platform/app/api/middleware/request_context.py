"""Correlation / request / trace middleware."""

from __future__ import annotations

import time
from collections.abc import Callable

from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request
from starlette.responses import Response

from app.shared.constants.headers import HeaderNames
from app.shared.context.request_context import RequestContext, bind_request_context
from app.shared.observability.metrics import get_metrics


class RequestContextMiddleware(BaseHTTPMiddleware):
    async def dispatch(
        self, request: Request, call_next: Callable[[Request], Response]
    ) -> Response:
        context = RequestContext.create(
            correlation_id=request.headers.get(HeaderNames.CORRELATION_ID),
            request_id=request.headers.get(HeaderNames.REQUEST_ID),
            trace_id=request.headers.get(HeaderNames.TRACE_ID),
            user_id=request.headers.get(HeaderNames.USER_ID),
        )
        bind_request_context(context)
        metrics = get_metrics()
        started = time.perf_counter()
        response: Response | None = None
        try:
            response = await call_next(request)
            return response
        finally:
            elapsed = time.perf_counter() - started
            path = request.url.path
            status = str(response.status_code if response is not None else 500)
            metrics.http_requests.labels(request.method, path, status).inc()
            metrics.http_latency.labels(request.method, path).observe(elapsed)
            if response is not None:
                response.headers[HeaderNames.CORRELATION_ID] = context.correlation_id
                response.headers[HeaderNames.REQUEST_ID] = context.request_id
                response.headers[HeaderNames.TRACE_ID] = context.trace_id
