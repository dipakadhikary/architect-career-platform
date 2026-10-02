"""Reranker providers and no-op fallback."""

from __future__ import annotations

import time

from app.intelligence.knowledge.models import RetrievalHit
from app.intelligence.knowledge.reranking.ports import KnowledgeRerankerPort
from app.shared.config.settings import AppSettings
from app.shared.exceptions import ValidationFailedError
from app.shared.observability.metrics import PlatformMetrics


class NoOpReranker(KnowledgeRerankerPort):
    async def rerank(self, query: str, documents: list[RetrievalHit]) -> list[RetrievalHit]:
        return documents


class IdentityScoreReranker(KnowledgeRerankerPort):
    """Lightweight lexical reranker used when external providers are unavailable."""

    async def rerank(self, query: str, documents: list[RetrievalHit]) -> list[RetrievalHit]:
        terms = {term.lower() for term in query.split() if term}
        ranked = sorted(
            documents,
            key=lambda doc: (sum(1 for term in terms if term in doc.text.lower()) + doc.score,),
            reverse=True,
        )
        return ranked


class CrossEncoderReranker(KnowledgeRerankerPort):
    def __init__(self, settings: AppSettings) -> None:
        self._model_name = settings.cross_encoder_model

    async def rerank(self, query: str, documents: list[RetrievalHit]) -> list[RetrievalHit]:
        try:
            from sentence_transformers import CrossEncoder
        except ImportError as exc:  # pragma: no cover
            raise ValidationFailedError("sentence-transformers required for cross-encoder") from exc
        model = CrossEncoder(self._model_name)
        pairs = [(query, doc.text) for doc in documents]
        scores = model.predict(pairs)
        ranked = [
            RetrievalHit(
                id=doc.id,
                document_id=doc.document_id,
                text=doc.text,
                score=float(score),
                metadata=doc.metadata,
            )
            for doc, score in sorted(
                zip(documents, scores, strict=True),
                key=lambda pair: pair[1],
                reverse=True,
            )
        ]
        return ranked


class BgeReranker(KnowledgeRerankerPort):
    def __init__(self, settings: AppSettings) -> None:
        self._delegate = CrossEncoderReranker(settings)

    async def rerank(self, query: str, documents: list[RetrievalHit]) -> list[RetrievalHit]:
        return await self._delegate.rerank(query, documents)


class CohereReranker(KnowledgeRerankerPort):
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings

    async def rerank(self, query: str, documents: list[RetrievalHit]) -> list[RetrievalHit]:
        if not self._settings.cohere_api_key:
            raise ValidationFailedError("Cohere API key is not configured")
        import httpx

        async with httpx.AsyncClient(timeout=30.0) as client:
            response = await client.post(
                "https://api.cohere.com/v1/rerank",
                headers={
                    "Authorization": (f"Bearer {self._settings.cohere_api_key.get_secret_value()}")
                },
                json={
                    "model": self._settings.cohere_rerank_model,
                    "query": query,
                    "documents": [doc.text for doc in documents],
                    "top_n": len(documents),
                },
            )
            response.raise_for_status()
            payload = response.json()
        order = payload.get("results", [])
        ranked: list[RetrievalHit] = []
        for item in order:
            index = int(item["index"])
            doc = documents[index]
            ranked.append(
                RetrievalHit(
                    id=doc.id,
                    document_id=doc.document_id,
                    text=doc.text,
                    score=float(item.get("relevance_score", doc.score)),
                    metadata=doc.metadata,
                )
            )
        return ranked


def build_reranker(settings: AppSettings, metrics: PlatformMetrics) -> KnowledgeRerankerPort:
    provider = settings.reranker_provider
    if provider == "none":
        return NoOpReranker()
    if provider == "identity":
        return _TimedReranker(IdentityScoreReranker(), metrics)
    if provider == "cross_encoder":
        return _TimedReranker(CrossEncoderReranker(settings), metrics)
    if provider == "bge":
        return _TimedReranker(BgeReranker(settings), metrics)
    if provider == "cohere":
        return _TimedReranker(CohereReranker(settings), metrics)
    return NoOpReranker()


class _TimedReranker(KnowledgeRerankerPort):
    def __init__(self, delegate: KnowledgeRerankerPort, metrics: PlatformMetrics) -> None:
        self._delegate = delegate
        self._metrics = metrics

    async def rerank(self, query: str, documents: list[RetrievalHit]) -> list[RetrievalHit]:
        started = time.perf_counter()
        try:
            return await self._delegate.rerank(query, documents)
        finally:
            self._metrics.knowledge_rerank_latency.observe(time.perf_counter() - started)
