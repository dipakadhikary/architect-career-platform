"""Cost tracking ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any

from app.intelligence.enterprise.models import CostRecord


class CostTrackerPort(ABC):
    @abstractmethod
    async def record(self, record: CostRecord) -> None:
        raise NotImplementedError

    @abstractmethod
    async def usage_summary(self, *, tenant_id: str | None = None) -> dict[str, Any]:
        raise NotImplementedError
