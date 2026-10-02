"""Knowledge component factories for DI wiring."""

from __future__ import annotations

from pathlib import Path

from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.infrastructure.knowledge.caching.redis_cache import RedisRagCache
from app.infrastructure.knowledge.chunking.strategies import (
    ChunkerRegistry,
    MarkdownChunker,
    RecursiveChunker,
    SemanticChunker,
    SentenceChunker,
    TokenChunker,
)
from app.infrastructure.knowledge.context.builder import DefaultContextBuilder
from app.infrastructure.knowledge.embeddings.factory import build_embedding_port
from app.infrastructure.knowledge.evaluation.heuristic import HeuristicRagEvaluator
from app.infrastructure.knowledge.ingestion.docx_loader import DocxDocumentLoader
from app.infrastructure.knowledge.ingestion.html_loader import HtmlDocumentLoader
from app.infrastructure.knowledge.ingestion.json_loader import JsonDocumentLoader
from app.infrastructure.knowledge.ingestion.markdown_loader import MarkdownDocumentLoader
from app.infrastructure.knowledge.ingestion.pdf_loader import PdfDocumentLoader
from app.infrastructure.knowledge.ingestion.registry import DocumentIngestionService
from app.infrastructure.knowledge.ingestion.text_loader import TextDocumentLoader
from app.infrastructure.knowledge.metadata.extractor import (
    DefaultMetadataExtractor,
    InMemoryMetadataRepository,
    RedisMetadataRepository,
)
from app.infrastructure.knowledge.preprocessing.pipeline import DefaultPreprocessor
from app.infrastructure.knowledge.prompt.file_prompt_builder import FilePromptBuilder
from app.infrastructure.knowledge.reranking.providers import build_reranker
from app.infrastructure.knowledge.response.builder import DefaultResponseBuilder
from app.infrastructure.knowledge.retrieval.pipeline import DefaultKnowledgeRetriever
from app.infrastructure.knowledge.vectorstore.factory import build_vector_store
from app.infrastructure.llm.factory import build_llm_port
from app.intelligence.embeddings.ports import EmbeddingPort
from app.intelligence.knowledge.caching.ports import RagCachePort
from app.intelligence.knowledge.chunking.ports import ChunkerRegistryPort
from app.intelligence.knowledge.context.ports import ContextBuilderPort
from app.intelligence.knowledge.evaluation.ports import RagEvaluationPort
from app.intelligence.knowledge.ingestion.ports import DocumentIngestionPort
from app.intelligence.knowledge.metadata.ports import MetadataExtractorPort, MetadataRepositoryPort
from app.intelligence.knowledge.preprocessing.ports import PreprocessorPort
from app.intelligence.knowledge.prompt.ports import PromptBuilderPort
from app.intelligence.knowledge.reranking.ports import KnowledgeRerankerPort
from app.intelligence.knowledge.response.ports import ResponseBuilderPort
from app.intelligence.knowledge.retrieval.ports import KnowledgeRetrieverPort
from app.intelligence.knowledge.vectorstore.ports import VectorStorePort
from app.intelligence.llm.ports import LlmPort
from app.orchestration.knowledge.service import KnowledgeService
from app.shared.config.settings import AppSettings
from app.shared.observability.metrics import PlatformMetrics


def build_document_ingestion() -> DocumentIngestionPort:
    return DocumentIngestionService(
        [
            TextDocumentLoader(),
            MarkdownDocumentLoader(),
            JsonDocumentLoader(),
            HtmlDocumentLoader(),
            PdfDocumentLoader(),
            DocxDocumentLoader(),
        ]
    )


def build_preprocessor() -> PreprocessorPort:
    return DefaultPreprocessor()


def build_metadata_extractor() -> MetadataExtractorPort:
    return DefaultMetadataExtractor()


def build_metadata_repository(
    settings: AppSettings, redis_adapter: RedisAdapter
) -> MetadataRepositoryPort:
    if settings.redis_enabled:
        return RedisMetadataRepository(redis_adapter)
    return InMemoryMetadataRepository()


def build_chunker_registry() -> ChunkerRegistryPort:
    return ChunkerRegistry(
        [
            RecursiveChunker(),
            TokenChunker(),
            SentenceChunker(),
            MarkdownChunker(),
            SemanticChunker(),
        ]
    )


def build_rag_cache(redis_adapter: RedisAdapter) -> RagCachePort:
    return RedisRagCache(redis_adapter)


def build_prompt_builder(settings: AppSettings) -> PromptBuilderPort:
    root = Path(settings.prompts_root)
    if not root.is_absolute():
        root = Path.cwd() / root
    return FilePromptBuilder(root)


def build_context_builder() -> ContextBuilderPort:
    return DefaultContextBuilder()


def build_response_builder() -> ResponseBuilderPort:
    return DefaultResponseBuilder()


def build_rag_evaluator() -> RagEvaluationPort:
    return HeuristicRagEvaluator()


def build_knowledge_retriever(
    embeddings: EmbeddingPort,
    store: VectorStorePort,
    cache: RagCachePort,
    metrics: PlatformMetrics,
    settings: AppSettings,
) -> KnowledgeRetrieverPort:
    return DefaultKnowledgeRetriever(
        embeddings=embeddings,
        store=store,
        cache=cache,
        metrics=metrics,
        embedding_model=settings.embedding_model,
    )


def build_knowledge_service(
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
    langfuse: object | None,
) -> KnowledgeService:
    return KnowledgeService(
        settings=settings,
        ingestion=ingestion,
        preprocessor=preprocessor,
        metadata_extractor=metadata_extractor,
        metadata_repository=metadata_repository,
        chunker_registry=chunker_registry,
        embeddings=embeddings,
        vector_store=vector_store,
        retriever=retriever,
        reranker=reranker,
        context_builder=context_builder,
        prompt_builder=prompt_builder,
        response_builder=response_builder,
        llm=llm,
        evaluator=evaluator,
        metrics=metrics,
        langfuse=langfuse,
    )


__all__ = [
    "build_chunker_registry",
    "build_context_builder",
    "build_document_ingestion",
    "build_embedding_port",
    "build_knowledge_retriever",
    "build_knowledge_service",
    "build_llm_port",
    "build_metadata_extractor",
    "build_metadata_repository",
    "build_preprocessor",
    "build_prompt_builder",
    "build_rag_cache",
    "build_rag_evaluator",
    "build_reranker",
    "build_response_builder",
    "build_vector_store",
]
