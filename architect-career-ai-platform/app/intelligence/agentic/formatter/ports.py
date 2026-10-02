"""Response formatter capability port."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any

from app.intelligence.agentic.models import FormattedResponse


class ResponseFormatterPort(ABC):
    @abstractmethod
    async def format(
        self,
        *,
        message: str,
        structured: dict[str, Any] | None = None,
        sources: list[dict[str, Any]] | None = None,
        model: str | None = None,
        prompt_version: str | None = None,
    ) -> FormattedResponse:
        raise NotImplementedError
