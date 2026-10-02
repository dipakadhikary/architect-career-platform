"""HTTP header names used across the platform."""

from __future__ import annotations


class HeaderNames:
    CORRELATION_ID = "X-Correlation-Id"
    REQUEST_ID = "X-Request-Id"
    TRACE_ID = "X-Trace-Id"
    USER_ID = "X-User-Id"
    API_KEY = "X-API-Key"
    INTERNAL_SERVICE = "X-Internal-Service"
    AUTHORIZATION = "Authorization"
