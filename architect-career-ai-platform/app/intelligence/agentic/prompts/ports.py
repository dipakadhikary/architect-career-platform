"""Prompt registry capability port."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any

from app.intelligence.agentic.models import PromptTemplateSpec


class PromptRegistryPort(ABC):
    @abstractmethod
    def register(self, spec: PromptTemplateSpec) -> None:
        raise NotImplementedError

    @abstractmethod
    def get(self, name: str, version: str | None = None) -> PromptTemplateSpec:
        raise NotImplementedError

    @abstractmethod
    def list_versions(self, name: str) -> list[str]:
        raise NotImplementedError

    @abstractmethod
    async def render(
        self,
        name: str,
        variables: dict[str, Any],
        *,
        version: str | None = None,
        few_shot: list[dict[str, str]] | None = None,
    ) -> PromptTemplateSpec:
        raise NotImplementedError

    @abstractmethod
    def validate(self, spec: PromptTemplateSpec) -> list[str]:
        raise NotImplementedError
