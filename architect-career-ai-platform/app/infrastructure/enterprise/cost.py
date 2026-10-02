"""Cost tracking and usage aggregation."""

from __future__ import annotations

from typing import Any

from app.intelligence.enterprise.cost.ports import CostTrackerPort
from app.intelligence.enterprise.models import CostRecord
from app.shared.observability.metrics import PlatformMetrics


class InMemoryCostTracker(CostTrackerPort):
    def __init__(self, metrics: PlatformMetrics) -> None:
        self._metrics = metrics
        self._records: list[CostRecord] = []

    async def record(self, record: CostRecord) -> None:
        self._records.append(record)
        self._metrics.estimated_cost.labels(record.provider, record.model).inc(
            record.estimated_cost_usd
        )
        self._metrics.token_usage.labels(record.provider, record.model, "prompt").inc(
            record.prompt_tokens
        )
        self._metrics.token_usage.labels(record.provider, record.model, "completion").inc(
            record.completion_tokens
        )
        if record.embedding_tokens:
            self._metrics.token_usage.labels(record.provider, record.model, "embedding").inc(
                record.embedding_tokens
            )
        self._metrics.enterprise_cost_usd.labels(record.capability, record.provider).inc(
            record.estimated_cost_usd
        )

    async def usage_summary(self, *, tenant_id: str | None = None) -> dict[str, Any]:
        filtered = [
            item
            for item in self._records
            if tenant_id is None or item.metadata.get("tenant_id") == tenant_id
        ]
        return {
            "requests": len(filtered),
            "prompt_tokens": sum(item.prompt_tokens for item in filtered),
            "completion_tokens": sum(item.completion_tokens for item in filtered),
            "embedding_tokens": sum(item.embedding_tokens for item in filtered),
            "estimated_cost_usd": round(sum(item.estimated_cost_usd for item in filtered), 6),
            "by_capability": _group(filtered, "capability"),
            "by_provider": _group(filtered, "provider"),
        }


def _group(records: list[CostRecord], field: str) -> dict[str, float]:
    totals: dict[str, float] = {}
    for item in records:
        key = str(getattr(item, field))
        totals[key] = totals.get(key, 0.0) + item.estimated_cost_usd
    return totals
