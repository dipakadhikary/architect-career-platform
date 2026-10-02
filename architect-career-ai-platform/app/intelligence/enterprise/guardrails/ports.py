"""Guardrails capability ports."""

from __future__ import annotations

from abc import ABC, abstractmethod

from app.intelligence.enterprise.models import GuardrailResult


class GuardrailsPort(ABC):
    @abstractmethod
    async def validate_input(self, text: str, *, capability: str) -> GuardrailResult:
        raise NotImplementedError

    @abstractmethod
    async def validate_output(self, text: str, *, capability: str) -> GuardrailResult:
        raise NotImplementedError
