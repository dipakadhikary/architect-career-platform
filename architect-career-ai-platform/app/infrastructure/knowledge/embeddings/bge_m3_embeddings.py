"""BGE-M3 embedding adapter via sentence-transformers when available."""

from __future__ import annotations

from app.intelligence.embeddings.ports import EmbeddingPort, EmbeddingRequest, EmbeddingResponse
from app.shared.config.settings import AppSettings
from app.shared.exceptions import ValidationFailedError


class BgeM3EmbeddingAdapter(EmbeddingPort):
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings
        self._model_name = settings.bge_m3_model

    async def embed(self, request: EmbeddingRequest) -> EmbeddingResponse:
        try:
            from sentence_transformers import SentenceTransformer
        except ImportError as exc:  # pragma: no cover
            raise ValidationFailedError(
                "sentence-transformers is required for BGE-M3 embeddings"
            ) from exc

        model = SentenceTransformer(request.model or self._model_name)
        vectors = model.encode(list(request.texts), normalize_embeddings=True).tolist()
        dimensions = len(vectors[0]) if vectors else self._settings.embedding_dimensions
        return EmbeddingResponse(
            vectors=vectors,
            model=request.model or self._model_name,
            dimensions=dimensions,
        )
