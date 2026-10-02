"""Ollama embeddings adapter."""

from __future__ import annotations

import httpx

from app.intelligence.embeddings.ports import EmbeddingPort, EmbeddingRequest, EmbeddingResponse
from app.shared.config.settings import AppSettings


class OllamaEmbeddingAdapter(EmbeddingPort):
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings

    async def embed(self, request: EmbeddingRequest) -> EmbeddingResponse:
        if not self._settings.ollama_enabled:
            raise RuntimeError("Ollama embeddings are disabled")
        model = request.model or self._settings.ollama_embedding_model
        vectors: list[list[float]] = []
        async with httpx.AsyncClient(
            base_url=self._settings.ollama_base_url, timeout=60.0
        ) as client:
            for text in request.texts:
                response = await client.post(
                    "/api/embeddings",
                    json={"model": model, "prompt": text},
                )
                response.raise_for_status()
                vectors.append(list(response.json()["embedding"]))
        dimensions = len(vectors[0]) if vectors else self._settings.embedding_dimensions
        return EmbeddingResponse(vectors=vectors, model=model, dimensions=dimensions)
