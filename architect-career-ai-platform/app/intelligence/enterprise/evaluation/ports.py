"""Enterprise evaluation persistence ports."""

from __future__ import annotations

from abc import ABC, abstractmethod

from app.intelligence.enterprise.models import EnterpriseEvaluationRecord


class EnterpriseEvaluationPort(ABC):
    @abstractmethod
    async def evaluate_and_persist(
        self,
        *,
        workflow_id: str,
        workflow: str,
        query: str,
        answer: str,
        context: str,
        latency_ms: float,
        prompt_tokens: int,
        completion_tokens: int,
        estimated_cost_usd: float,
        prompt_version: str,
        model: str,
        provider: str,
    ) -> EnterpriseEvaluationRecord:
        raise NotImplementedError

    @abstractmethod
    async def get(self, workflow_id: str) -> EnterpriseEvaluationRecord | None:
        raise NotImplementedError
