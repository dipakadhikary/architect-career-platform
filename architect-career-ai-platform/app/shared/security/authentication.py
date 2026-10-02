"""Authentication preparation (JWT, API key, internal service). No authorization logic."""

from __future__ import annotations

from dataclasses import dataclass
from typing import Any

from jose import JWTError, jwt

from app.shared.config.settings import AppSettings
from app.shared.exceptions import AuthenticationError


@dataclass(slots=True, frozen=True)
class AuthenticatedPrincipal:
    subject: str
    auth_method: str
    claims: dict[str, Any]


class AuthenticationService:
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings

    def authenticate(
        self,
        *,
        authorization: str | None,
        api_key: str | None,
        internal_service_token: str | None,
    ) -> AuthenticatedPrincipal | None:
        if not any(
            [
                self._settings.auth_jwt_enabled,
                self._settings.auth_api_key_enabled,
                bool(self._settings.internal_service_token_set),
            ]
        ):
            return None

        if (
            internal_service_token
            and internal_service_token in self._settings.internal_service_token_set
        ):
            return AuthenticatedPrincipal(
                subject="internal-service",
                auth_method="internal",
                claims={},
            )

        if self._settings.auth_api_key_enabled and api_key:
            if api_key not in self._settings.api_key_set:
                raise AuthenticationError("API key is invalid")
            return AuthenticatedPrincipal(subject="api-key", auth_method="api_key", claims={})

        if self._settings.auth_jwt_enabled:
            if not authorization or not authorization.lower().startswith("bearer "):
                raise AuthenticationError("Bearer token is missing")
            token = authorization.split(" ", 1)[1].strip()
            return self._validate_jwt(token)

        raise AuthenticationError()

    def _validate_jwt(self, token: str) -> AuthenticatedPrincipal:
        options = {"verify_aud": bool(self._settings.auth_jwt_audience)}
        try:
            claims = jwt.decode(
                token,
                self._settings.auth_jwt_secret.get_secret_value(),
                algorithms=[self._settings.auth_jwt_algorithm],
                audience=self._settings.auth_jwt_audience or None,
                issuer=self._settings.auth_jwt_issuer or None,
                options=options,
            )
        except JWTError as exc:
            raise AuthenticationError("Bearer token is invalid") from exc

        subject = str(claims.get("sub") or claims.get("client_id") or "unknown")
        return AuthenticatedPrincipal(subject=subject, auth_method="jwt", claims=dict(claims))
