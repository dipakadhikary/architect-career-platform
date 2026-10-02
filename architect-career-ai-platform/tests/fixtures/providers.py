"""Mock providers for Knowledge pipeline tests."""

from __future__ import annotations

from collections.abc import AsyncIterator
from typing import Any

from app.intelligence.embeddings.ports import EmbeddingPort, EmbeddingRequest, EmbeddingResponse
from app.intelligence.knowledge.models import RetrievalHit, VectorRecord
from app.intelligence.knowledge.reranking.ports import KnowledgeRerankerPort
from app.intelligence.knowledge.vectorstore.ports import VectorSearchQuery, VectorStorePort
from app.intelligence.llm.ports import LlmCompletionRequest, LlmCompletionResponse, LlmPort


class MockLlmProvider(LlmPort):
    @property
    def provider_name(self) -> str:
        return "mock"

    async def complete(self, request: LlmCompletionRequest) -> LlmCompletionResponse:
        return LlmCompletionResponse(
            content="mock-response\n- point one\n- point two",
            model=request.model or "mock-model",
            provider=self.provider_name,
        )

    async def stream(self, request: LlmCompletionRequest) -> AsyncIterator[str]:
        yield "mock-response"

    async def health_check(self) -> bool:
        return True


class MockEmbeddingProvider(EmbeddingPort):
    def __init__(self, dimensions: int = 8) -> None:
        self._dimensions = dimensions

    async def embed(self, request: EmbeddingRequest) -> EmbeddingResponse:
        vectors = [[0.1] * self._dimensions for _ in request.texts]
        return EmbeddingResponse(
            vectors=vectors,
            model=request.model or "mock-embed",
            dimensions=self._dimensions,
        )


class MockVectorStore(VectorStorePort):
    def __init__(self) -> None:
        self.records: list[VectorRecord] = []

    async def ensure_collection(self, dimensions: int) -> None:
        return None

    async def upsert(self, records: list[VectorRecord]) -> None:
        self.records.extend(records)

    async def search(self, query: VectorSearchQuery) -> list[VectorRecord]:
        return [
            VectorRecord(
                id=record.id,
                document_id=record.document_id,
                text=record.text,
                embedding=record.embedding,
                metadata=record.metadata,
                score=0.9,
            )
            for record in self.records[: query.top_k]
        ]

    async def delete_by_document(self, document_id: str) -> None:
        self.records = [item for item in self.records if item.document_id != document_id]

    async def keyword_search(
        self,
        text: str,
        *,
        top_k: int = 10,
        filters: dict[str, Any] | None = None,
    ) -> list[VectorRecord]:
        return await self.search(
            VectorSearchQuery(embedding=[], top_k=top_k, filters=filters or {})
        )


class MockReranker(KnowledgeRerankerPort):
    async def rerank(self, query: str, documents: list[RetrievalHit]) -> list[RetrievalHit]:
        return documents
