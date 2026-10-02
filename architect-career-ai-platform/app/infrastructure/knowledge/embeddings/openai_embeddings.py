"""OpenAI embeddings adapter."""

from __future__ import annotations

from openai import AsyncOpenAI

from app.intelligence.embeddings.ports import EmbeddingPort, EmbeddingRequest, EmbeddingResponse
from app.shared.config.settings import AppSettings


class OpenAIEmbeddingAdapter(EmbeddingPort):
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings
        self._client: AsyncOpenAI | None = None
        if settings.openai_enabled and settings.openai_api_key is not None:
            self._client = AsyncOpenAI(
                api_key=settings.openai_api_key.get_secret_value(),
                base_url=settings.openai_base_url,
            )

    async def embed(self, request: EmbeddingRequest) -> EmbeddingResponse:
        if self._client is None:
            raise RuntimeError("OpenAI embeddings are disabled")
        model = request.model or self._settings.embedding_model
        response = await self._client.embeddings.create(model=model, input=request.texts)
        vectors = [list(item.embedding) for item in response.data]
        dimensions = len(vectors[0]) if vectors else self._settings.embedding_dimensions
        return EmbeddingResponse(vectors=vectors, model=model, dimensions=dimensions)
