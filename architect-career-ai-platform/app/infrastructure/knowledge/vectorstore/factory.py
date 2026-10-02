"""Vector store factory."""

from __future__ import annotations

from app.infrastructure.knowledge.vectorstore.memory_store import InMemoryVectorStore
from app.infrastructure.knowledge.vectorstore.qdrant_store import QdrantVectorStore
from app.infrastructure.vector.qdrant_adapter import QdrantAdapter
from app.intelligence.knowledge.vectorstore.ports import VectorStorePort
from app.shared.config.settings import AppSettings


def build_vector_store(settings: AppSettings, qdrant: QdrantAdapter) -> VectorStorePort:
    if settings.vector_store_provider == "qdrant" and qdrant.enabled:
        return QdrantVectorStore(settings, qdrant)
    return InMemoryVectorStore()
