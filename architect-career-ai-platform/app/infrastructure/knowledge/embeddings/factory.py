"""Embedding provider factory."""

from __future__ import annotations

from app.intelligence.embeddings.ports import EmbeddingPort
from app.shared.config.settings import AppSettings
from app.shared.exceptions import ValidationFailedError


def build_embedding_port(settings: AppSettings) -> EmbeddingPort:
    provider = settings.embedding_provider
    if provider == "openai":
        from app.infrastructure.knowledge.embeddings.openai_embeddings import (
            OpenAIEmbeddingAdapter,
        )

        return OpenAIEmbeddingAdapter(settings)
    if provider == "azure_openai":
        from app.infrastructure.knowledge.embeddings.azure_embeddings import (
            AzureOpenAIEmbeddingAdapter,
        )

        return AzureOpenAIEmbeddingAdapter(settings)
    if provider == "ollama":
        from app.infrastructure.knowledge.embeddings.ollama_embeddings import (
            OllamaEmbeddingAdapter,
        )

        return OllamaEmbeddingAdapter(settings)
    if provider == "bge_m3":
        from app.infrastructure.knowledge.embeddings.bge_m3_embeddings import (
            BgeM3EmbeddingAdapter,
        )

        return BgeM3EmbeddingAdapter(settings)
    if provider == "sentence_transformers":
        from app.infrastructure.knowledge.embeddings.sentence_transformers_embeddings import (
            SentenceTransformersEmbeddingAdapter,
        )

        return SentenceTransformersEmbeddingAdapter(settings)
    if provider == "hashing":
        from app.infrastructure.knowledge.embeddings.hashing_embeddings import (
            HashingEmbeddingAdapter,
        )

        return HashingEmbeddingAdapter(settings)
    raise ValidationFailedError(f"Unsupported embedding provider: {provider}")
