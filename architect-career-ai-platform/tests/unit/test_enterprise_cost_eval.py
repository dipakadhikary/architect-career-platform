"""Unit tests for cost tracking and evaluation hooks."""

from __future__ import annotations

import pytest
from app.infrastructure.enterprise.cost import InMemoryCostTracker
from app.infrastructure.enterprise.evaluation import PersistentEnterpriseEvaluator
from app.intelligence.enterprise.models import CostRecord
from app.shared.observability.metrics import PlatformMetrics


class MockLangFuse:
    def __init__(self) -> None:
        self.traces: list[dict] = []

    def trace(self, **kwargs):
        self.traces.append(kwargs)
        return self

    def update(self, **kwargs):
        self.traces.append(kwargs)
        return self


@pytest.mark.asyncio
async def test_cost_tracker_summary() -> None:
    metrics = PlatformMetrics()
    tracker = InMemoryCostTracker(metrics)
    await tracker.record(
        CostRecord(
            provider="openai",
            model="gpt",
            capability="chat",
            prompt_tokens=100,
            completion_tokens=50,
            embedding_tokens=10,
            estimated_cost_usd=0.02,
            metadata={"tenant_id": "t1"},
        )
    )
    summary = await tracker.usage_summary(tenant_id="t1")
    assert summary["requests"] == 1
    assert summary["prompt_tokens"] == 100
    assert summary["estimated_cost_usd"] == 0.02


@pytest.mark.asyncio
async def test_evaluator_persists_and_hooks_langfuse() -> None:
    mock = MockLangFuse()
    evaluator = PersistentEnterpriseEvaluator(mock)
    record = await evaluator.evaluate_and_persist(
        workflow_id="w1",
        workflow="chat",
        query="what is kafka",
        answer="kafka is a distributed log",
        context="kafka stores messages",
        latency_ms=12.0,
        prompt_tokens=10,
        completion_tokens=8,
        estimated_cost_usd=0.001,
        prompt_version="v1",
        model="gpt",
        provider="openai",
    )
    assert 0.0 <= record.answer_relevance <= 1.0
    assert mock.traces
