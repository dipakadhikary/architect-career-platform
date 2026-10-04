"""Authentication boundary for the Phase 1 assistant.

The service does not issue credentials. User calls present the ACOS JWT.
Machine calls present the internal service token or API key together with X-User-Id.
"""

from __future__ import annotations

from jose import JWTError, jwt

from app.intelligence.assistant.models import CallerContext
from app.shared.config.settings import AppSettings
from app.shared.exceptions import AuthenticationError
from app.shared.security.jwt_algorithms import hmac_algorithms_for_secret


def resolve_caller(
    settings: AppSettings,
    *,
    authorization: str | None,
    api_key: str | None,
    internal_service_token: str | None,
    user_id_header: str | None,
    correlation_id: str,
) -> CallerContext:
    """Resolve the owner id that later retrieval must filter on."""
    if authorization:
        return _caller_from_jwt(settings, authorization, correlation_id)
    if _internal_token_matches(settings, internal_service_token):
        return _caller_from_header(
            user_id_header, auth_method="internal", correlation_id=correlation_id
        )
    if settings.auth_api_key_enabled and api_key and api_key in settings.api_key_set:
        return _caller_from_header(
            user_id_header, auth_method="api_key", correlation_id=correlation_id
        )
    raise AuthenticationError()


def _caller_from_jwt(
    settings: AppSettings, authorization: str, correlation_id: str
) -> CallerContext:
    if not authorization.lower().startswith("bearer "):
        raise AuthenticationError("Bearer token is missing")
    token = authorization.split(" ", 1)[1].strip()
    if not token:
        raise AuthenticationError("Bearer token is missing")
    options = {"verify_aud": bool(settings.auth_jwt_audience)}
    try:
        secret = settings.auth_jwt_secret.get_secret_value()
        claims = jwt.decode(
            token,
            secret,
            algorithms=hmac_algorithms_for_secret(secret, settings.auth_jwt_algorithm),
            audience=settings.auth_jwt_audience or None,
            issuer=settings.auth_jwt_issuer or None,
            options=options,
        )
    except JWTError as exc:
        raise AuthenticationError("Bearer token is invalid") from exc
    subject = claims.get("sub")
    if not isinstance(subject, str) or not subject.strip():
        raise AuthenticationError("Bearer token is missing a subject")
    return CallerContext(
        owner_id=subject.strip(),
        auth_method="jwt",
        correlation_id=correlation_id,
    )


def _internal_token_matches(settings: AppSettings, token: str | None) -> bool:
    return bool(token) and token in settings.internal_service_token_set


def _caller_from_header(
    user_id_header: str | None, *, auth_method: str, correlation_id: str
) -> CallerContext:
    if user_id_header is None or not user_id_header.strip():
        raise AuthenticationError("X-User-Id is required for service credentials")
    return CallerContext(
        owner_id=user_id_header.strip(),
        auth_method=auth_method,
        correlation_id=correlation_id,
    )
