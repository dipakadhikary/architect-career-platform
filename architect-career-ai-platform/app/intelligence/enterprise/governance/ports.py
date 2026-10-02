"""Prompt governance ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Any

from app.intelligence.agentic.models import PromptTemplateSpec
from app.intelligence.enterprise.models import PromptLifecycleStatus


@dataclass(slots=True, frozen=True)
class PromptGovernanceRecord:
    name: str
    version: str
    status: PromptLifecycleStatus
    approved_by: str | None = None
    metadata: dict[str, Any] = field(default_factory=dict)


@dataclass(slots=True, frozen=True)
class PromptAuditEntry:
    name: str
    version: str
    action: str
    actor: str
    detail: dict[str, Any] = field(default_factory=dict)


class PromptGovernancePort(ABC):
    @abstractmethod
    async def resolve(
        self,
        name: str,
        variables: dict[str, Any],
        *,
        version: str | None = None,
        require_approved: bool = True,
    ) -> PromptTemplateSpec:
        raise NotImplementedError

    @abstractmethod
    def approve(self, name: str, version: str, *, actor: str) -> PromptGovernanceRecord:
        raise NotImplementedError

    @abstractmethod
    def deprecate(self, name: str, version: str, *, actor: str) -> PromptGovernanceRecord:
        raise NotImplementedError

    @abstractmethod
    def rollback(self, name: str, to_version: str, *, actor: str) -> PromptGovernanceRecord:
        raise NotImplementedError

    @abstractmethod
    def audit_history(self, name: str) -> list[PromptAuditEntry]:
        raise NotImplementedError

    @abstractmethod
    def status(self, name: str, version: str) -> PromptGovernanceRecord:
        raise NotImplementedError
