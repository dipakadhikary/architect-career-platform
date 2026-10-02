"""Azure OpenAI embeddings adapter."""

from __future__ import annotations

from openai import AsyncAzureOpenAI

from app.intelligence.embeddings.ports import EmbeddingPort, EmbeddingRequest, EmbeddingResponse
from app.shared.config.settings import AppSettings


class AzureOpenAIEmbeddingAdapter(EmbeddingPort):
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings
        self._client: AsyncAzureOpenAI | None = None
        if (
            settings.azure_openai_enabled
            and settings.azure_openai_api_key is not None
            and settings.azure_openai_endpoint
        ):
            self._client = AsyncAzureOpenAI(
                api_key=settings.azure_openai_api_key.get_secret_value(),
                azure_endpoint=settings.azure_openai_endpoint,
                api_version=settings.azure_openai_api_version,
            )

    async def embed(self, request: EmbeddingRequest) -> EmbeddingResponse:
        if self._client is None:
            raise RuntimeError("Azure OpenAI embeddings are disabled")
        model = (
            request.model
            or self._settings.azure_embedding_deployment
            or self._settings.embedding_model
        )
        response = await self._client.embeddings.create(model=model, input=request.texts)
        vectors = [list(item.embedding) for item in response.data]
        dimensions = len(vectors[0]) if vectors else self._settings.embedding_dimensions
        return EmbeddingResponse(vectors=vectors, model=model, dimensions=dimensions)
