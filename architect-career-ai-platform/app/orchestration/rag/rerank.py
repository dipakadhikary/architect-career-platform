"""Replaceable rerankers. The chat path does not call an external rerank API."""

from __future__ import annotations

import asyncio
from dataclasses import replace
from typing import Protocol

from app.orchestration.rag.lexical import lexical_rank_score
from app.orchestration.rag.models import RetrievedChunk
from app.shared.config.settings import AppSettings


class RagReranker(Protocol):
    async def rerank(
        self,
        query: str,
        candidates: list[RetrievedChunk],
        *,
        top_k: int,
    ) -> list[RetrievedChunk]:
        """Return candidates in the order the context builder should prefer."""
        ...


class PassthroughReranker:
    async def rerank(
        self,
        query: str,
        candidates: list[RetrievedChunk],
        *,
        top_k: int,
    ) -> list[RetrievedChunk]:
        del query
        return list(candidates[:top_k])


class IdentityOverlapReranker:
    """Local overlap reranker. It does not download or call a model."""

    async def rerank(
        self,
        query: str,
        candidates: list[RetrievedChunk],
        *,
        top_k: int,
    ) -> list[RetrievedChunk]:
        ranked = [
            replace(chunk, rerank_score=lexical_rank_score(query, chunk.content))
            for chunk in candidates
        ]
        ranked.sort(
            key=lambda item: (
                -(item.rerank_score or 0.0),
                item.content_id,
                item.chunk_id,
            )
        )
        return [replace(chunk, final_score=chunk.rerank_score) for chunk in ranked[:top_k]]


class LocalCrossEncoderReranker:
    """Optional local cross-encoder. The model loads only when reranking runs."""

    def __init__(self, model_name: str) -> None:
        self._model_name = model_name
        self._model: object | None = None

    async def rerank(
        self,
        query: str,
        candidates: list[RetrievedChunk],
        *,
        top_k: int,
    ) -> list[RetrievedChunk]:
        if not candidates:
            return []
        model = self._load()
        pairs = [(query, chunk.content) for chunk in candidates]
        scores = await asyncio.to_thread(model.predict, pairs)  # type: ignore[attr-defined]
        paired = list(zip(candidates, scores, strict=True))
        paired.sort(key=lambda item: float(item[1]), reverse=True)
        ranked: list[RetrievedChunk] = []
        for chunk, score in paired[:top_k]:
            value = float(score)
            ranked.append(replace(chunk, rerank_score=value, final_score=value))
        return ranked

    def _load(self) -> object:
        if self._model is None:
            from sentence_transformers import CrossEncoder

            self._model = CrossEncoder(self._model_name)
        return self._model


def build_rag_reranker(settings: AppSettings) -> RagReranker:
    if not settings.rag_reranking_enabled or settings.reranker_provider == "none":
        return PassthroughReranker()
    if settings.reranker_provider in {"cross_encoder", "bge"}:
        model = settings.rag_rerank_model or settings.cross_encoder_model
        return LocalCrossEncoderReranker(model)
    return IdentityOverlapReranker()
