"""Typed platform exceptions mapped to RFC 9457 Problem Details."""

from __future__ import annotations

from typing import Any


class PlatformError(Exception):
    """Base platform error."""

    def __init__(
        self,
        *,
        title: str,
        detail: str,
        status: int,
        code: str,
        type_uri: str = "about:blank",
        errors: list[dict[str, Any]] | None = None,
        extensions: dict[str, Any] | None = None,
    ) -> None:
        super().__init__(detail)
        self.title = title
        self.detail = detail
        self.status = status
        self.code = code
        self.type_uri = type_uri
        self.errors = errors or []
        self.extensions = extensions or {}


class ValidationFailedError(PlatformError):
    def __init__(self, detail: str, errors: list[dict[str, Any]] | None = None) -> None:
        super().__init__(
            title="Validation Failed",
            detail=detail,
            status=400,
            code="AI_VALIDATION_FAILED",
            type_uri="https://acos.local/problems/ai-validation-failed",
            errors=errors,
        )


class AuthenticationError(PlatformError):
    def __init__(self, detail: str = "Authentication credentials are missing or invalid") -> None:
        super().__init__(
            title="Unauthorized",
            detail=detail,
            status=401,
            code="AI_AUTHENTICATION_FAILED",
            type_uri="https://acos.local/problems/ai-authentication-failed",
        )


class AuthorizationError(PlatformError):
    def __init__(self, detail: str = "Caller is not permitted") -> None:
        super().__init__(
            title="Forbidden",
            detail=detail,
            status=403,
            code="AI_FORBIDDEN",
            type_uri="https://acos.local/problems/ai-forbidden",
        )


class NotFoundError(PlatformError):
    def __init__(self, detail: str = "Resource was not found") -> None:
        super().__init__(
            title="Not Found",
            detail=detail,
            status=404,
            code="AI_NOT_FOUND",
            type_uri="https://acos.local/problems/ai-not-found",
        )


class ConflictError(PlatformError):
    def __init__(self, detail: str) -> None:
        super().__init__(
            title="Conflict",
            detail=detail,
            status=409,
            code="AI_CONFLICT",
            type_uri="https://acos.local/problems/ai-conflict",
        )


class RateLimitError(PlatformError):
    def __init__(self, detail: str = "Rate limit exceeded", retry_after: int = 30) -> None:
        super().__init__(
            title="Too Many Requests",
            detail=detail,
            status=429,
            code="AI_RATE_LIMITED",
            type_uri="https://acos.local/problems/ai-rate-limited",
            extensions={"retryAfter": retry_after},
        )


class GuardrailViolationError(PlatformError):
    def __init__(self, detail: str = "Request blocked by guardrails") -> None:
        super().__init__(
            title="Guardrail Violation",
            detail=detail,
            status=400,
            code="AI_GUARDRAIL_VIOLATION",
            type_uri="https://acos.local/problems/ai-guardrail-violation",
        )


class PolicyViolationError(PlatformError):
    def __init__(self, detail: str = "Request blocked by policy") -> None:
        super().__init__(
            title="Policy Violation",
            detail=detail,
            status=403,
            code="AI_POLICY_VIOLATION",
            type_uri="https://acos.local/problems/ai-policy-violation",
        )


class UpstreamTimeoutError(PlatformError):
    def __init__(self, detail: str = "Upstream timed out") -> None:
        super().__init__(
            title="Gateway Timeout",
            detail=detail,
            status=504,
            code="AI_UPSTREAM_TIMEOUT",
            type_uri="https://acos.local/problems/ai-upstream-timeout",
        )
