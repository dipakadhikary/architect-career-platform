"""Audit logging ports."""

from __future__ import annotations

from abc import ABC, abstractmethod

from app.intelligence.enterprise.models import AuditEvent


class AuditLogPort(ABC):
    @abstractmethod
    async def write(self, event: AuditEvent) -> None:
        raise NotImplementedError
