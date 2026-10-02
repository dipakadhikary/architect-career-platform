"""In-memory rate limiting middleware foundation."""

from __future__ import annotations

import time
from collections import defaultdict, deque
from collections.abc import Callable

from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request
from starlette.responses import Response

from app.shared.config.settings import AppSettings
from app.shared.exceptions import RateLimitError


class RateLimitMiddleware(BaseHTTPMiddleware):
    def __init__(self, app: Callable[..., object], settings: AppSettings) -> None:
        super().__init__(app)
        self._settings = settings
        self._hits: dict[str, deque[float]] = defaultdict(deque)

    async def dispatch(
        self, request: Request, call_next: Callable[[Request], Response]
    ) -> Response:
        limit = self._settings.rate_limit_requests_per_minute
        if limit <= 0:
            return await call_next(request)
        if request.url.path in self._settings.rate_limit_exempt_path_set:
            return await call_next(request)

        identity = request.headers.get("X-API-Key") or (
            request.client.host if request.client else "anonymous"
        )
        now = time.time()
        window = self._hits[identity]
        while window and now - window[0] > 60:
            window.popleft()
        if len(window) >= limit:
            raise RateLimitError()
        window.append(now)
        return await call_next(request)
