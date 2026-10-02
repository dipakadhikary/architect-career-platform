"""Settings foundation tests."""

from __future__ import annotations

from app.shared.config.settings import AppSettings


def test_cors_origins_parsing() -> None:
    settings = AppSettings(cors_allow_origins="http://a.local, http://b.local")
    assert settings.cors_origins == ["http://a.local", "http://b.local"]
