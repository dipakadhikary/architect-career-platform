"""Unit tests for Knowledge preprocessing, chunking, and embeddings."""

from __future__ import annotations

import pytest
from app.infrastructure.knowledge.chunking.strategies import (
    ChunkerRegistry,
    RecursiveChunker,
    SentenceChunker,
    TokenChunker,
)
from app.infrastructure.knowledge.embeddings.hashing_embeddings import HashingEmbeddingAdapter
from app.infrastructure.knowledge.evaluation.heuristic import HeuristicRagEvaluator
from app.infrastructure.knowledge.ingestion.html_loader import HtmlDocumentLoader
from app.infrastructure.knowledge.ingestion.json_loader import JsonDocumentLoader
from app.infrastructure.knowledge.ingestion.markdown_loader import MarkdownDocumentLoader
from app.infrastructure.knowledge.ingestion.text_loader import TextDocumentLoader
from app.infrastructure.knowledge.metadata.extractor import DefaultMetadataExtractor
from app.infrastructure.knowledge.preprocessing.pipeline import DefaultPreprocessor
from app.infrastructure.knowledge.reranking.providers import IdentityScoreReranker
from app.infrastructure.knowledge.vectorstore.memory_store import InMemoryVectorStore
from app.intelligence.embeddings.ports import EmbeddingRequest
from app.intelligence.knowledge.chunking.ports import ChunkingOptions
from app.intelligence.knowledge.evaluation.ports import RagEvaluationRequest
from app.intelligence.knowledge.models import (
    ChunkingStrategy,
    DocumentFormat,
    RawDocument,
    RetrievalHit,
    VectorRecord,
)
from app.intelligence.knowledge.preprocessing.ports import PreprocessOptions
from app.intelligence.knowledge.vectorstore.ports import VectorSearchQuery
from app.shared.config.settings import AppSettings


@pytest.mark.asyncio
async def test_text_and_structured_loaders() -> None:
    text = await TextDocumentLoader().load(
        RawDocument(content="hello world", format=DocumentFormat.PLAIN, source="t")
    )
    assert text.text == "hello world"

    md = await MarkdownDocumentLoader().load(
        RawDocument(content="# Title\n\nBody", format=DocumentFormat.MARKDOWN, source="m")
    )
    assert "Title" in md.text

    js = await JsonDocumentLoader().load(
        RawDocument(content='{"a":1,"b":"x"}', format=DocumentFormat.JSON, source="j")
    )
    assert "a" in js.text

    html = await HtmlDocumentLoader().load(
        RawDocument(
            content="<html><body><p>Hi</p></body></html>",
            format=DocumentFormat.HTML,
            source="h",
        )
    )
    assert "Hi" in html.text


@pytest.mark.asyncio
async def test_preprocessor_normalizes_and_deduplicates() -> None:
    result = await DefaultPreprocessor().process(
        "Page 1\nHello\nHello\n\n\nWorld",
        PreprocessOptions(remove_headers_footers=True, remove_duplicates=True),
    )
    assert "Hello" in result.text
    assert result.text.count("Hello") == 1


@pytest.mark.asyncio
async def test_metadata_extractor() -> None:
    loaded = await TextDocumentLoader().load(
        RawDocument(content="alpha beta gamma", format=DocumentFormat.TXT, source="s")
    )
    metadata = await DefaultMetadataExtractor().extract(loaded)
    assert metadata.word_count == 3
    assert len(metadata.checksum) == 64


@pytest.mark.asyncio
async def test_chunking_strategies() -> None:
    text = "One. Two. Three. Four. Five. Six."
    options = ChunkingOptions(strategy=ChunkingStrategy.SENTENCE, chunk_size=2, chunk_overlap=0)
    chunks = await SentenceChunker().chunk(text, options)
    assert len(chunks) >= 2

    token_chunks = await TokenChunker().chunk(
        "a b c d e f",
        ChunkingOptions(strategy=ChunkingStrategy.TOKEN, chunk_size=3, chunk_overlap=1),
    )
    assert len(token_chunks) >= 2

    recursive = await RecursiveChunker().chunk(
        "x" * 50,
        ChunkingOptions(strategy=ChunkingStrategy.RECURSIVE, chunk_size=20, chunk_overlap=5),
    )
    assert len(recursive) >= 2

    registry = ChunkerRegistry([RecursiveChunker(), TokenChunker(), SentenceChunker()])
    assert registry.get(ChunkingStrategy.RECURSIVE).strategy == ChunkingStrategy.RECURSIVE


@pytest.mark.asyncio
async def test_hashing_embeddings_and_memory_store(settings: AppSettings) -> None:
    embedder = HashingEmbeddingAdapter(settings)
    embedded = await embedder.embed(EmbeddingRequest(texts=["architecture patterns"]))
    assert len(embedded.vectors[0]) == settings.embedding_dimensions

    store = InMemoryVectorStore()
    await store.ensure_collection(embedded.dimensions)
    await store.upsert(
        [
            VectorRecord(
                id="1",
                document_id="doc-1",
                text="architecture patterns",
                embedding=embedded.vectors[0],
                metadata={"user_id": "u1"},
            )
        ]
    )
    hits = await store.search(
        VectorSearchQuery(embedding=embedded.vectors[0], top_k=5, filters={"user_id": "u1"})
    )
    assert hits and hits[0].id == "1"


@pytest.mark.asyncio
async def test_identity_reranker_and_evaluator() -> None:
    docs = [
        RetrievalHit(id="1", document_id="d", text="alpha beta", score=0.1),
        RetrievalHit(id="2", document_id="d", text="gamma", score=0.9),
    ]
    ranked = await IdentityScoreReranker().rerank("alpha", docs)
    assert ranked[0].id == "1"

    evaluation = await HeuristicRagEvaluator().evaluate(
        RagEvaluationRequest(query="alpha beta", answer="alpha summary", hits=ranked)
    )
    assert 0.0 <= evaluation.score <= 1.0
