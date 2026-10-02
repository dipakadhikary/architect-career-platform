"""Structured response formatter."""

from __future__ import annotations

from typing import Any

from app.intelligence.agentic.formatter.ports import ResponseFormatterPort
from app.intelligence.agentic.models import (
    CapabilityDescriptor,
    CapabilityKind,
    FormattedResponse,
)


class DefaultResponseFormatter(ResponseFormatterPort):
    @property
    def descriptor(self) -> CapabilityDescriptor:
        return CapabilityDescriptor(
            name="response_formatter",
            kind=CapabilityKind.RESPONSE_FORMATTER,
            description="Formats capability outputs into structured responses",
        )

    async def format(
        self,
        *,
        message: str,
        structured: dict[str, Any] | None = None,
        sources: list[dict[str, Any]] | None = None,
        model: str | None = None,
        prompt_version: str | None = None,
    ) -> FormattedResponse:
        cleaned = message.strip()
        return FormattedResponse(
            message=cleaned,
            structured=dict(structured or {}),
            sources=list(sources or []),
            model=model,
            prompt_version=prompt_version,
        )

    async def invoke(self, payload: dict[str, Any]) -> dict[str, Any]:
        formatted = await self.format(
            message=str(payload.get("message") or payload.get("answer") or ""),
            structured=payload.get("structured"),
            sources=payload.get("sources"),
            model=payload.get("model"),
            prompt_version=payload.get("prompt_version"),
        )
        return {
            "message": formatted.message,
            "structured": formatted.structured,
            "sources": formatted.sources,
            "model": formatted.model,
            "prompt_version": formatted.prompt_version,
        }
