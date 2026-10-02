"""Knowledge AI orchestration service (index / search / summarize)."""

from __future__ import annotations

import time
import uuid
from dataclasses import replace

from app.intelligence.embeddings.ports import EmbeddingPort, EmbeddingRequest
from app.intelligence.knowledge.chunking.ports import ChunkerRegistryPort, ChunkingOptions
from app.intelligence.knowledge.context.ports import ContextBuilderPort, ContextOptions
from app.intelligence.knowledge.evaluation.ports import RagEvaluationPort, RagEvaluationRequest
from app.intelligence.knowledge.ingestion.ports import DocumentIngestionPort
from app.intelligence.knowledge.metadata.ports import (
    MetadataExtractorPort,
    MetadataHints,
    MetadataRepositoryPort,
)
from app.intelligence.knowledge.models import (
    ChunkingStrategy,
    DocumentFormat,
    GenerationResult,
    RawDocument,
    RetrievalMode,
    VectorRecord,
)
from app.intelligence.knowledge.preprocessing.ports import PreprocessorPort
from app.intelligence.knowledge.prompt.ports import PromptBuilderPort, PromptRenderRequest
from app.intelligence.knowledge.reranking.ports import KnowledgeRerankerPort
from app.intelligence.knowledge.response.ports import ResponseBuilderPort
from app.intelligence.knowledge.retrieval.ports import (
    KnowledgeRetrievalQuery,
    KnowledgeRetrieverPort,
)
from app.intelligence.knowledge.vectorstore.ports import VectorStorePort
from app.intelligence.llm.ports import LlmCompletionRequest, LlmMessage, LlmPort
from app.shared.config.settings import AppSettings
from app.shared.logging.setup import get_logger
from app.shared.observability.metrics import PlatformMetrics
from app.shared.utils.time import utc_now

logger = get_logger(__name__)


class KnowledgeService:
    def __init__(
        self,
        *,
        settings: AppSettings,
        ingestion: DocumentIngestionPort,
        preprocessor: PreprocessorPort,
        metadata_extractor: MetadataExtractorPort,
        metadata_repository: MetadataRepositoryPort,
        chunker_registry: ChunkerRegistryPort,
        embeddings: EmbeddingPort,
        vector_store: VectorStorePort,
        retriever: KnowledgeRetrieverPort,
        reranker: KnowledgeRerankerPort,
        context_builder: ContextBuilderPort,
        prompt_builder: PromptBuilderPort,
        response_builder: ResponseBuilderPort,
        llm: LlmPort,
        evaluator: RagEvaluationPort,
        metrics: PlatformMetrics,
        langfuse: object | None = None,
    ) -> None:
        self._settings = settings
        self._ingestion = ingestion
        self._preprocessor = preprocessor
        self._metadata_extractor = metadata_extractor
        self._metadata_repository = metadata_repository
        self._chunker_registry = chunker_registry
        self._embeddings = embeddings
        self._vector_store = vector_store
        self._retriever = retriever
        self._reranker = reranker
        self._context_builder = context_builder
        self._prompt_builder = prompt_builder
        self._response_builder = response_builder
        self._llm = llm
        self._evaluator = evaluator
        self._metrics = metrics
        self._langfuse = langfuse

    async def index_document(
        self,
        *,
        user_id: str,
        note_id: str,
        title: str,
        content: str,
        tags: list[str] | None = None,
        document_format: DocumentFormat = DocumentFormat.PLAIN,
    ) -> dict[str, object]:
        started = time.perf_counter()
        document_id = str(uuid.uuid4())
        tags = tags or []

        loaded = await self._ingestion.ingest(
            RawDocument(content=content, format=document_format, source=f"note:{note_id}")
        )
        processed = await self._preprocessor.process(loaded.text)
        metadata = await self._metadata_extractor.extract(
            loaded,
            hints=MetadataHints(title=title, tags=tags, source=f"user:{user_id}"),
            language=processed.language,
        )

        chunker = self._chunker_registry.get(ChunkingStrategy(self._settings.chunking_strategy))
        chunks = await chunker.chunk(
            processed.text,
            ChunkingOptions(
                strategy=ChunkingStrategy(self._settings.chunking_strategy),
                chunk_size=self._settings.chunk_size,
                chunk_overlap=self._settings.chunk_overlap,
            ),
        )
        metadata = replace(metadata, chunk_count=len(chunks))
        await self._metadata_repository.save(document_id, metadata)

        embed_started = time.perf_counter()
        embedded = await self._embeddings.embed(
            EmbeddingRequest(
                texts=[chunk.text for chunk in chunks] or [""],
                model=self._settings.embedding_model,
            )
        )
        self._metrics.knowledge_embedding_latency.observe(time.perf_counter() - embed_started)
        self._metrics.token_usage.labels(
            self._settings.embedding_provider, embedded.model, "embedding"
        ).inc(len(chunks))

        await self._vector_store.ensure_collection(embedded.dimensions)
        await self._vector_store.delete_by_document(document_id)

        records = [
            VectorRecord(
                id=chunk.chunk_id,
                document_id=document_id,
                text=chunk.text,
                embedding=vector,
                metadata={
                    "user_id": user_id,
                    "note_id": note_id,
                    "title": title,
                    "tags": tags,
                    "checksum": metadata.checksum,
                    "language": metadata.language,
                    "chunk_index": chunk.index,
                },
            )
            for chunk, vector in zip(chunks, embedded.vectors, strict=False)
        ]
        qdrant_started = time.perf_counter()
        await self._vector_store.upsert(records)
        self._metrics.knowledge_qdrant_latency.observe(time.perf_counter() - qdrant_started)

        elapsed_ms = (time.perf_counter() - started) * 1000
        logger.info(
            "knowledge.indexed",
            document_id=document_id,
            note_id=note_id,
            chunks=len(records),
            latency_ms=elapsed_ms,
        )
        return {
            "document_id": document_id,
            "note_id": note_id,
            "status": "INDEXED",
            "indexed_at": utc_now(),
            "chunk_count": len(records),
        }

    async def search(
        self,
        *,
        user_id: str,
        query: str,
        limit: int = 10,
    ) -> list[dict[str, object]]:
        started = time.perf_counter()
        hits = await self._retriever.retrieve(
            KnowledgeRetrievalQuery(
                text=query,
                top_k=max(limit * 2, limit),
                score_threshold=self._settings.retrieval_score_threshold,
                mode=RetrievalMode(self._settings.retrieval_mode),
                filters={"user_id": user_id},
            )
        )
        ranked = await self._reranker.rerank(query, hits)
        selected = ranked[:limit]
        results: list[dict[str, object]] = []
        for hit in selected:
            snippet = hit.text if len(hit.text) <= 280 else f"{hit.text[:277]}..."
            results.append(
                {
                    "note_id": str(hit.metadata.get("note_id") or hit.document_id),
                    "title": str(hit.metadata.get("title") or "Untitled"),
                    "snippet": snippet,
                    "score": float(min(max(hit.score, 0.0), 1.0)),
                }
            )
        logger.info(
            "knowledge.searched",
            user_id=user_id,
            hits=len(results),
            latency_ms=(time.perf_counter() - started) * 1000,
        )
        return results

    async def summarize(
        self,
        *,
        user_id: str,
        content: str,
        note_id: str | None = None,
        max_length: int | None = None,
    ) -> dict[str, object]:
        started = time.perf_counter()
        hits = await self._retriever.retrieve(
            KnowledgeRetrievalQuery(
                text=content[:500],
                top_k=self._settings.summarize_top_k,
                mode=RetrievalMode(self._settings.retrieval_mode),
                filters={"user_id": user_id},
            )
        )
        ranked = await self._reranker.rerank(content[:500], hits)
        context = await self._context_builder.build(
            ranked,
            ContextOptions(max_tokens=self._settings.context_max_tokens),
        )
        prompt = await self._prompt_builder.render(
            PromptRenderRequest(
                name="summarize",
                version=self._settings.summarize_prompt_version,
                variables={
                    "content": content,
                    "context": context.text,
                    "max_length": str(max_length or self._settings.summarize_max_length),
                },
            )
        )

        gen_started = time.perf_counter()
        completion = await self._llm.complete(
            LlmCompletionRequest(
                messages=[
                    LlmMessage(role="system", content=prompt.system),
                    LlmMessage(role="user", content=prompt.user),
                ],
                model=(
                    self._settings.openai_default_model
                    if self._settings.llm_provider == "openai"
                    else None
                ),
            )
        )
        generation_latency = (time.perf_counter() - gen_started) * 1000
        self._metrics.knowledge_generation_latency.observe(generation_latency / 1000.0)
        self._metrics.token_usage.labels(completion.provider, completion.model, "prompt").inc(
            completion.usage.prompt_tokens
        )
        self._metrics.token_usage.labels(completion.provider, completion.model, "completion").inc(
            completion.usage.completion_tokens
        )
        self._metrics.estimated_cost.labels(completion.provider, completion.model).inc(
            completion.cost.amount
        )

        generation = GenerationResult(
            answer=completion.content,
            model=completion.model,
            provider=completion.provider,
            prompt_version=prompt.version,
            latency_ms=generation_latency,
            prompt_tokens=completion.usage.prompt_tokens,
            completion_tokens=completion.usage.completion_tokens,
            total_tokens=completion.usage.total_tokens,
            estimated_cost_usd=completion.cost.amount,
        )
        answer = await self._response_builder.build(
            generation=generation,
            context=context,
            hits=ranked,
            confidence=_confidence(ranked),
        )
        evaluation = await self._evaluator.evaluate(
            RagEvaluationRequest(query=content[:500], answer=answer.answer, hits=ranked)
        )

        key_points = [
            line.strip(" -*")
            for line in answer.answer.splitlines()
            if line.strip().startswith(("-", "*"))
        ][:5]

        logger.info(
            "knowledge.summarized",
            user_id=user_id,
            note_id=note_id,
            latency_ms=(time.perf_counter() - started) * 1000,
            prompt_version=prompt.version,
            model=completion.model,
            provider=completion.provider,
            evaluation_score=evaluation.score,
        )
        return {
            "summary": answer.answer,
            "note_id": note_id,
            "key_points": key_points or None,
            "model": answer.model,
            "provider": answer.provider,
            "prompt_version": answer.prompt_version,
            "token_usage": answer.token_usage,
            "latency_ms": answer.latency_ms,
            "confidence": answer.confidence,
            "sources": answer.sources,
            "evaluation_score": evaluation.score,
        }


def _confidence(hits: list) -> float:
    if not hits:
        return 0.0
    return float(min(max(sum(hit.score for hit in hits) / len(hits), 0.0), 1.0))
