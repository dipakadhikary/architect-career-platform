"""Caller resolution for the Phase 1 assistant."""

from __future__ import annotations

import pytest
from app.api.assistant_auth import resolve_caller
from app.shared.config.settings import AppSettings
from app.shared.exceptions import AuthenticationError
from jose import jwt
from pydantic import SecretStr


def _settings(**overrides: object) -> AppSettings:
    values: dict[str, object] = {
        "app_env": "test",
        "redis_enabled": False,
        "qdrant_enabled": False,
        "openai_enabled": False,
        "ollama_enabled": False,
        "langfuse_enabled": False,
        "otel_enabled": False,
        "auth_jwt_secret": SecretStr("change-me"),
        "auth_jwt_algorithm": "HS256",
        "auth_api_key_enabled": True,
        "auth_api_keys": "service-key",
        "auth_internal_service_tokens": "internal-token",
    }
    values.update(overrides)
    return AppSettings(**values)  # type: ignore[arg-type]


def test_jwt_subject_is_the_owner() -> None:
    token = jwt.encode({"sub": "user-42"}, "change-me", algorithm="HS256")
    caller = resolve_caller(
        _settings(),
        authorization=f"Bearer {token}",
        api_key=None,
        internal_service_token=None,
        user_id_header="ignored",
        correlation_id="c1",
    )
    assert caller.owner_id == "user-42"
    assert caller.auth_method == "jwt"
    assert caller.correlation_id == "c1"


def test_invalid_jwt_is_rejected_even_when_api_key_matches() -> None:
    with pytest.raises(AuthenticationError):
        resolve_caller(
            _settings(),
            authorization="Bearer not-a-token",
            api_key="service-key",
            internal_service_token=None,
            user_id_header="user-1",
            correlation_id="c1",
        )


def test_internal_service_requires_user_header() -> None:
    caller = resolve_caller(
        _settings(),
        authorization=None,
        api_key=None,
        internal_service_token="internal-token",
        user_id_header="user-7",
        correlation_id="c1",
    )
    assert caller.owner_id == "user-7"
    assert caller.auth_method == "internal"


def test_internal_service_without_user_is_rejected() -> None:
    with pytest.raises(AuthenticationError):
        resolve_caller(
            _settings(),
            authorization=None,
            api_key=None,
            internal_service_token="internal-token",
            user_id_header=None,
            correlation_id="c1",
        )


def test_missing_credentials_are_rejected() -> None:
    with pytest.raises(AuthenticationError):
        resolve_caller(
            _settings(),
            authorization=None,
            api_key=None,
            internal_service_token=None,
            user_id_header=None,
            correlation_id="c1",
        )
