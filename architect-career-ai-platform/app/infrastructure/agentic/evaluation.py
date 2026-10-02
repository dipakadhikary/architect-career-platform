"""Agentic evaluation framework with LangFuse hooks."""

from __future__ import annotations

from typing import Any

from app.intelligence.agentic.evaluation.ports import (
    AgenticEvaluationPort,
    AgenticEvaluationRequest,
)
from app.intelligence.agentic.models import (
    AgenticEvaluation,
    CapabilityDescriptor,
    CapabilityKind,
)


class HeuristicAgenticEvaluator(AgenticEvaluationPort):
    def __init__(self, langfuse: Any | None = None) -> None:
        self._langfuse = langfuse

    @property
    def descriptor(self) -> CapabilityDescriptor:
        return CapabilityDescriptor(
            name="evaluator",
            kind=CapabilityKind.EVALUATOR,
            description="Faithfulness, relevance, groundedness, latency, cost evaluation",
        )

    async def evaluate(self, request: AgenticEvaluationRequest) -> AgenticEvaluation:
        query_terms = {term.lower() for term in request.query.split() if len(term) > 2}
        answer_terms = {term.lower() for term in request.answer.split() if len(term) > 2}
        context_text = " ".join(hit.text for hit in request.context_hits).lower()
        context_terms = {term for term in context_text.split() if len(term) > 2}
        overlap_qa = len(query_terms & answer_terms) / len(query_terms) if query_terms else 0.0
        overlap_ac = len(answer_terms & context_terms) / len(answer_terms) if answer_terms else 0.0
        overlap_qc = len(query_terms & context_terms) / len(query_terms) if query_terms else 0.0
        retriever_quality = (
            sum(hit.score for hit in request.context_hits) / len(request.context_hits)
            if request.context_hits
            else 0.0
        )
        faithfulness = min(max(0.5 * overlap_ac + 0.5 * retriever_quality, 0.0), 1.0)
        answer_relevance = min(max(overlap_qa, 0.0), 1.0)
        context_relevance = min(max(overlap_qc, 0.0), 1.0)
        groundedness = min(max(overlap_ac, 0.0), 1.0)
        llm_quality = min(max((faithfulness + answer_relevance) / 2.0, 0.0), 1.0)
        result = AgenticEvaluation(
            faithfulness=faithfulness,
            answer_relevance=answer_relevance,
            context_relevance=context_relevance,
            groundedness=groundedness,
            latency_ms=request.latency_ms,
            prompt_tokens=request.prompt_tokens,
            completion_tokens=request.completion_tokens,
            total_tokens=request.prompt_tokens + request.completion_tokens,
            estimated_cost_usd=request.estimated_cost_usd,
            prompt_version=request.prompt_version,
            retriever_quality=retriever_quality,
            llm_quality=llm_quality,
            details={"langfuse_enabled": bool(getattr(self._langfuse, "enabled", False))},
        )
        trace = getattr(self._langfuse, "trace", None)
        if callable(trace):
            try:
                trace(
                    name="agentic.evaluation",
                    metadata={
                        "faithfulness": result.faithfulness,
                        "answer_relevance": result.answer_relevance,
                        "prompt_version": result.prompt_version,
                    },
                )
            except Exception:  # noqa: S110
                pass
        return result

    async def invoke(self, payload: dict[str, Any]) -> dict[str, Any]:
        evaluation = await self.evaluate(
            AgenticEvaluationRequest(
                query=str(payload.get("query") or ""),
                answer=str(payload.get("answer") or ""),
                latency_ms=float(payload.get("latency_ms") or 0.0),
                prompt_tokens=int(payload.get("prompt_tokens") or 0),
                completion_tokens=int(payload.get("completion_tokens") or 0),
                estimated_cost_usd=float(payload.get("estimated_cost_usd") or 0.0),
                prompt_version=str(payload.get("prompt_version") or "v1"),
            )
        )
        return {
            "faithfulness": evaluation.faithfulness,
            "answer_relevance": evaluation.answer_relevance,
            "context_relevance": evaluation.context_relevance,
            "groundedness": evaluation.groundedness,
            "latency_ms": evaluation.latency_ms,
            "prompt_tokens": evaluation.prompt_tokens,
            "completion_tokens": evaluation.completion_tokens,
            "total_tokens": evaluation.total_tokens,
            "estimated_cost_usd": evaluation.estimated_cost_usd,
            "prompt_version": evaluation.prompt_version,
            "retriever_quality": evaluation.retriever_quality,
            "llm_quality": evaluation.llm_quality,
        }
