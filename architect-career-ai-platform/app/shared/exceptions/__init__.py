"""Platform exception hierarchy."""

from app.shared.exceptions.base import (
    AuthenticationError,
    AuthorizationError,
    ConflictError,
    GuardrailViolationError,
    NotFoundError,
    PlatformError,
    PolicyViolationError,
    RateLimitError,
    UpstreamTimeoutError,
    ValidationFailedError,
)

__all__ = [
    "AuthenticationError",
    "AuthorizationError",
    "ConflictError",
    "GuardrailViolationError",
    "NotFoundError",
    "PlatformError",
    "PolicyViolationError",
    "RateLimitError",
    "UpstreamTimeoutError",
    "ValidationFailedError",
]
