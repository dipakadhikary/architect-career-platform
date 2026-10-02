"""Enterprise evaluation with persistence and LangFuse integration."""

from __future__ import annotations

from typing import Any

from app.intelligence.enterprise.evaluation.ports import EnterpriseEvaluationPort
from app.intelligence.enterprise.models import EnterpriseEvaluationRecord


class PersistentEnterpriseEvaluator(EnterpriseEvaluationPort):
    def __init__(self, langfuse: Any | None = None) -> None:
        self._langfuse = langfuse
        self._store: dict[str, EnterpriseEvaluationRecord] = {}

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
        q_terms = {t.lower() for t in query.split() if len(t) > 2}
        a_terms = {t.lower() for t in answer.split() if len(t) > 2}
        c_terms = {t.lower() for t in context.split() if len(t) > 2}
        faithfulness = _ratio(a_terms & c_terms, a_terms)
        groundedness = faithfulness
        answer_relevance = _ratio(q_terms & a_terms, q_terms)
        context_precision = _ratio(q_terms & c_terms, c_terms)
        context_recall = _ratio(q_terms & c_terms, q_terms)
        retriever_quality = (context_precision + context_recall) / 2.0
        record = EnterpriseEvaluationRecord(
            workflow_id=workflow_id,
            workflow=workflow,
            faithfulness=faithfulness,
            groundedness=groundedness,
            context_precision=context_precision,
            context_recall=context_recall,
            answer_relevance=answer_relevance,
            retriever_quality=retriever_quality,
            latency_ms=latency_ms,
            prompt_tokens=prompt_tokens,
            completion_tokens=completion_tokens,
            estimated_cost_usd=estimated_cost_usd,
            prompt_version=prompt_version,
            model=model,
            provider=provider,
            details={"persisted": True},
        )
        self._store[workflow_id] = record
        trace = getattr(self._langfuse, "trace", None)
        if callable(trace):
            try:
                trace(
                    name="enterprise.evaluation",
                    metadata={
                        "workflow_id": workflow_id,
                        "faithfulness": faithfulness,
                        "answer_relevance": answer_relevance,
                        "prompt_version": prompt_version,
                        "model": model,
                        "provider": provider,
                    },
                )
            except Exception:  # noqa: S110
                pass
        return record

    async def get(self, workflow_id: str) -> EnterpriseEvaluationRecord | None:
        return self._store.get(workflow_id)


def _ratio(numerator: set[str], denominator: set[str]) -> float:
    if not denominator:
        return 0.0
    return min(max(len(numerator) / len(denominator), 0.0), 1.0)
