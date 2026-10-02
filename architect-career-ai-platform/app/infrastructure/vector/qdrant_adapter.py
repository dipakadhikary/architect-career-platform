"""Qdrant adapter. Qdrant client never leaves infrastructure."""

from __future__ import annotations

from qdrant_client import QdrantClient

from app.shared.config.settings import AppSettings


class QdrantAdapter:
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings
        self._client: QdrantClient | None = None
        if settings.qdrant_enabled:
            api_key = (
                settings.qdrant_api_key.get_secret_value() if settings.qdrant_api_key else None
            )
            self._client = QdrantClient(url=settings.qdrant_url, api_key=api_key)

    @property
    def enabled(self) -> bool:
        return self._client is not None

    def health_check(self) -> bool:
        if self._client is None:
            return False
        return bool(self._client.get_collections())

    @property
    def client(self) -> QdrantClient:
        if self._client is None:
            raise RuntimeError("Qdrant is disabled")
        return self._client
