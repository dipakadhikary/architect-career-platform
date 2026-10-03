"""Configuration for the Phase 1 assistant."""

from __future__ import annotations

from app.shared.config.settings import AppSettings, get_settings


def test_ai_provider_alias(monkeypatch) -> None:
    monkeypatch.setenv("APP_ENV", "test")
    monkeypatch.setenv("AI_PROVIDER", "ollama")
    monkeypatch.setenv("AI_ENABLED", "false")
    monkeypatch.setenv("AI_MODEL", "llama3.2")
    monkeypatch.setenv("AI_TIMEOUT", "12")
    monkeypatch.setenv("AI_MAX_TOKENS", "256")
    monkeypatch.setenv("AI_TEMPERATURE", "0.4")
    monkeypatch.setenv("REDIS_ENABLED", "false")
    monkeypatch.setenv("QDRANT_ENABLED", "false")
    get_settings.cache_clear()
    settings = get_settings()
    assert settings.llm_provider == "ollama"
    assert settings.ai_enabled is False
    assert settings.resolve_chat_model() == "llama3.2"
    assert settings.ai_timeout_seconds == 12
    assert settings.ai_max_tokens == 256
    assert settings.ai_temperature == 0.4
    get_settings.cache_clear()


def test_retry_limit_is_bounded() -> None:
    try:
        AppSettings(
            app_env="test",
            redis_enabled=False,
            qdrant_enabled=False,
            ai_retry_max_attempts=9,
        )
    except Exception as exc:
        assert "ai_retry_max_attempts" in str(exc)
    else:
        raise AssertionError("expected retry limit validation to fail")
