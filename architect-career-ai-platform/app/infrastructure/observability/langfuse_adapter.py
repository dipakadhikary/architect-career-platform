"""Langfuse adapter. SDK remains inside infrastructure."""

from __future__ import annotations

from typing import Any

from app.shared.config.settings import AppSettings


class LangfuseAdapter:
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings
        self._client: Any | None = None
        if (
            settings.langfuse_enabled
            and settings.langfuse_public_key
            and settings.langfuse_secret_key
        ):
            from langfuse import Langfuse

            self._client = Langfuse(
                public_key=settings.langfuse_public_key,
                secret_key=settings.langfuse_secret_key.get_secret_value(),
                host=settings.langfuse_host,
            )

    @property
    def enabled(self) -> bool:
        return self._client is not None

    def trace(self, name: str, metadata: dict[str, Any] | None = None) -> Any | None:
        if self._client is None:
            return None
        return self._client.trace(name=name, metadata=metadata or {})

    def flush(self) -> None:
        if self._client is not None:
            self._client.flush()
