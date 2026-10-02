"""HTTPX client factory."""

from __future__ import annotations

import httpx

from app.shared.config.settings import AppSettings


class HttpxClientFactory:
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings

    def create(self, *, timeout: float = 30.0) -> httpx.AsyncClient:
        return httpx.AsyncClient(
            timeout=timeout,
            headers={"User-Agent": f"{self._settings.app_name}/{self._settings.app_version}"},
        )
