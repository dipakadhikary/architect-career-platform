"""Deterministic local embedding adapter for tests and offline development."""

from __future__ import annotations

import hashlib
import math

from app.intelligence.embeddings.ports import EmbeddingPort, EmbeddingRequest, EmbeddingResponse
from app.shared.config.settings import AppSettings


class HashingEmbeddingAdapter(EmbeddingPort):
    def __init__(self, settings: AppSettings) -> None:
        self._dimensions = settings.embedding_dimensions

    async def embed(self, request: EmbeddingRequest) -> EmbeddingResponse:
        vectors = [self._embed_one(text) for text in request.texts]
        return EmbeddingResponse(
            vectors=vectors,
            model=request.model or "hashing-local",
            dimensions=self._dimensions,
        )

    def _embed_one(self, text: str) -> list[float]:
        digest = hashlib.sha256(text.encode("utf-8")).digest()
        values: list[float] = []
        while len(values) < self._dimensions:
            for byte in digest:
                values.append((byte / 255.0) * 2 - 1)
                if len(values) >= self._dimensions:
                    break
            digest = hashlib.sha256(digest).digest()
        norm = math.sqrt(sum(v * v for v in values)) or 1.0
        return [v / norm for v in values]
