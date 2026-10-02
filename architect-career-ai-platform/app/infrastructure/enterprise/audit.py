"""Audit logging sink."""

from __future__ import annotations

from app.intelligence.enterprise.audit.ports import AuditLogPort
from app.intelligence.enterprise.models import AuditEvent
from app.shared.logging.setup import get_logger

logger = get_logger(__name__)


class StructlogAuditLogger(AuditLogPort):
    def __init__(self) -> None:
        self.events: list[AuditEvent] = []

    async def write(self, event: AuditEvent) -> None:
        self.events.append(event)
        logger.info(
            "enterprise.audit",
            action=event.action,
            workflow=event.workflow,
            workflow_id=event.workflow_id,
            principal=event.principal,
            tenant_id=event.tenant_id,
            success=event.success,
            detail=event.detail,
        )
