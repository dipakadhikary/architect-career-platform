"""Prompt governance: approval, deprecation, rollback, audit history."""

from __future__ import annotations

from typing import Any

from app.intelligence.agentic.models import PromptTemplateSpec
from app.intelligence.agentic.prompts.ports import PromptRegistryPort
from app.intelligence.enterprise.governance.ports import (
    PromptAuditEntry,
    PromptGovernancePort,
    PromptGovernanceRecord,
)
from app.intelligence.enterprise.models import PromptLifecycleStatus
from app.shared.exceptions import NotFoundError, ValidationFailedError


class PromptGovernanceService(PromptGovernancePort):
    def __init__(self, registry: PromptRegistryPort) -> None:
        self._registry = registry
        self._status: dict[tuple[str, str], PromptGovernanceRecord] = {}
        self._audit: list[PromptAuditEntry] = []
        self._active: dict[str, str] = {}

    async def resolve(
        self,
        name: str,
        variables: dict[str, Any],
        *,
        version: str | None = None,
        require_approved: bool = True,
    ) -> PromptTemplateSpec:
        resolved_version = version or self._active.get(name)
        if resolved_version is None:
            versions = self._registry.list_versions(name)
            if not versions:
                raise NotFoundError(f"Prompt not found: {name}")
            resolved_version = versions[-1]
            self._ensure_default(name, resolved_version)

        record = self.status(name, resolved_version)
        if require_approved and record.status != PromptLifecycleStatus.APPROVED:
            raise ValidationFailedError(
                f"Prompt {name}@{resolved_version} is {record.status.value}"
            )

        rendered = await self._registry.render(name, variables, version=resolved_version)
        self._audit.append(
            PromptAuditEntry(
                name=name,
                version=resolved_version,
                action="resolve",
                actor="system",
                detail={"variables": sorted(variables.keys())},
            )
        )
        return rendered

    def approve(self, name: str, version: str, *, actor: str) -> PromptGovernanceRecord:
        record = PromptGovernanceRecord(
            name=name,
            version=version,
            status=PromptLifecycleStatus.APPROVED,
            approved_by=actor,
        )
        self._status[(name, version)] = record
        self._active[name] = version
        self._audit.append(
            PromptAuditEntry(name=name, version=version, action="approve", actor=actor)
        )
        return record

    def deprecate(self, name: str, version: str, *, actor: str) -> PromptGovernanceRecord:
        record = PromptGovernanceRecord(
            name=name,
            version=version,
            status=PromptLifecycleStatus.DEPRECATED,
            approved_by=actor,
        )
        self._status[(name, version)] = record
        self._audit.append(
            PromptAuditEntry(name=name, version=version, action="deprecate", actor=actor)
        )
        return record

    def rollback(self, name: str, to_version: str, *, actor: str) -> PromptGovernanceRecord:
        if to_version not in self._registry.list_versions(name):
            raise NotFoundError(f"Prompt version not found: {name}@{to_version}")
        current = self._active.get(name)
        if current:
            self._status[(name, current)] = PromptGovernanceRecord(
                name=name,
                version=current,
                status=PromptLifecycleStatus.ROLLED_BACK,
                approved_by=actor,
            )
        record = self.approve(name, to_version, actor=actor)
        self._audit.append(
            PromptAuditEntry(
                name=name,
                version=to_version,
                action="rollback",
                actor=actor,
                detail={"from": current},
            )
        )
        return record

    def audit_history(self, name: str) -> list[PromptAuditEntry]:
        return [entry for entry in self._audit if entry.name == name]

    def status(self, name: str, version: str) -> PromptGovernanceRecord:
        existing = self._status.get((name, version))
        if existing is not None:
            return existing
        record = PromptGovernanceRecord(
            name=name,
            version=version,
            status=PromptLifecycleStatus.DRAFT,
        )
        self._status[(name, version)] = record
        return record

    def _ensure_default(self, name: str, version: str) -> None:
        self.status(name, version)
        self._active.setdefault(name, version)
