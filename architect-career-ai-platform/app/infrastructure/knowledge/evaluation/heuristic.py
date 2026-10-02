"""Heuristic RAG evaluation adapter."""

from __future__ import annotations

from app.intelligence.knowledge.evaluation.ports import (
    RagEvaluationPort,
    RagEvaluationRequest,
    RagEvaluationResult,
)


class HeuristicRagEvaluator(RagEvaluationPort):
    """Offline-safe evaluator based on lexical overlap and retrieval coverage."""

    async def evaluate(self, request: RagEvaluationRequest) -> RagEvaluationResult:
        query_terms = {term.lower() for term in request.query.split() if len(term) > 2}
        answer_terms = {term.lower() for term in request.answer.split() if len(term) > 2}
        overlap = 0.0
        if query_terms:
            overlap = len(query_terms & answer_terms) / len(query_terms)
        coverage = min(len(request.hits) / 3.0, 1.0) if request.hits else 0.0
        avg_score = (
            sum(hit.score for hit in request.hits) / len(request.hits) if request.hits else 0.0
        )
        score = max(0.0, min(1.0, (0.4 * overlap) + (0.3 * coverage) + (0.3 * avg_score)))
        return RagEvaluationResult(
            score=score,
            details={
                "overlap": overlap,
                "coverage": coverage,
                "avg_retrieval_score": avg_score,
                "hit_count": len(request.hits),
            },
        )
