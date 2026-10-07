"""Hybrid retrieval tests. Providers are fakes. No external model is called."""

from __future__ import annotations

import asyncio
import json
from pathlib import Path

import pytest
from app.infrastructure.knowledge.vectorstore.memory_store import InMemoryVectorStore
from app.intelligence.assistant.errors import RagUnavailableError
from app.intelligence.assistant.models import (
    CallerContext,
    ChatMessage,
    ChatRequest,
    ChatRole,
    NormalizedCompletion,
)
from app.intelligence.assistant.provider import LlmProvider
from app.intelligence.knowledge.models import VectorRecord
from app.orchestration.rag.context import ContextBuilder
from app.orchestration.rag.engine import RagEngine
from app.orchestration.rag.evaluation import (
    HybridEvalCase,
    StrategyObservation,
    score_observation,
)
from app.orchestration.rag.fusion import reciprocal_rank_fusion, weighted_fusion
from app.orchestration.rag.hybrid import HybridRetriever
from app.orchestration.rag.lexical import IndexedLexicalRetriever, lexical_rank_score, query_tokens
from app.orchestration.rag.models import RetrievedChunk
from app.orchestration.rag.prompt import RagPromptBuilder
from app.orchestration.rag.rerank import (
    IdentityOverlapReranker,
    LocalCrossEncoderReranker,
    PassthroughReranker,
    build_rag_reranker,
)
from app.shared.config.settings import AppSettings
from app.shared.observability.metrics import PlatformMetrics
from prometheus_client import CollectorRegistry
from pydantic import SecretStr

FIXTURE = Path(__file__).resolve().parents[1] / "fixtures" / "hybrid_eval_cases.json"


def _settings(**overrides: object) -> AppSettings:
    values: dict[str, object] = dict(
        app_env="test",
        redis_enabled=False,
        qdrant_enabled=False,
        langfuse_enabled=False,
        otel_enabled=False,
        auth_jwt_secret=SecretStr("change-me"),
        ai_enabled=True,
        rag_enabled=True,
        rag_hybrid_enabled=True,
        rag_top_k=5,
        rag_lexical_top_k=5,
        rag_vector_top_k=5,
        rag_fusion_top_k=10,
        rag_rrf_k=60,
        rag_reranking_enabled=False,
        rag_rerank_top_k=5,
        rag_rerank_candidate_k=10,
        rag_lexical_timeout_seconds=1,
        rag_rerank_timeout_seconds=1,
        embedding_model="hashing-local",
        embedding_dimensions=4,
        embedding_provider="hashing",
    )
    values.update(overrides)
    return AppSettings(**values)  # type: ignore[arg-type]


def _metrics() -> PlatformMetrics:
    return PlatformMetrics(CollectorRegistry())


def _chunk(
    content_id: str,
    *,
    owner: str = "owner-1",
    content: str = "body",
    score: float = 0.5,
    chunk_id: str | None = None,
    **extra: object,
) -> RetrievedChunk:
    return RetrievedChunk(
        chunk_id=chunk_id or content_id,
        content_id=content_id,
        content=content,
        score=score,
        title=content_id,
        section="Introduction",
        content_type="CONCEPT",
        source_url=f"/tutorials/{content_id}/concept",
        path=content_id,
        topic_id=None,
        owner_id=owner,
        **extra,  # type: ignore[arg-type]
    )


def _record(content_id: str, text: str, owner: str = "owner-1") -> VectorRecord:
    return VectorRecord(
        id=f"chunk-{content_id}",
        document_id=content_id,
        text=text,
        embedding=[0.2, 0.2, 0.2, 0.2],
        metadata={
            "content_id": content_id,
            "owner_id": owner,
            "title": content_id,
            "section": "Introduction",
            "content_type": "CONCEPT",
            "source_url": f"/tutorials/{content_id}/concept",
            "path": content_id,
        },
    )


class _Lexical:
    def __init__(self, chunks: list[RetrievedChunk] | None = None, error: Exception | None = None):
        self.chunks = chunks or []
        self.error = error

    async def retrieve(self, query: str, *, owner_id: str) -> list[RetrievedChunk]:
        del query, owner_id
        if self.error:
            raise self.error
        return list(self.chunks)


class _Vector:
    def __init__(self, chunks: list[RetrievedChunk] | None = None, error: Exception | None = None):
        self.chunks = chunks or []
        self.error = error
        self.top_k: int | None = None
        self.owner_id = ""

    async def retrieve(
        self, query: str, *, owner_id: str, top_k: int | None = None
    ) -> list[RetrievedChunk]:
        del query
        self.top_k = top_k
        self.owner_id = owner_id
        if self.error:
            raise self.error
        return list(self.chunks)


class _RecordingReranker:
    def __init__(self, error: Exception | None = None, delay: float = 0) -> None:
        self.seen: list[RetrievedChunk] = []
        self.error = error
        self.delay = delay
        self.calls = 0

    async def rerank(
        self, query: str, candidates: list[RetrievedChunk], *, top_k: int
    ) -> list[RetrievedChunk]:
        del query
        self.calls += 1
        if self.delay:
            await asyncio.sleep(self.delay)
        if self.error:
            raise self.error
        self.seen = list(candidates)
        return list(candidates[:top_k])


class _FakeModel(LlmProvider):
    provider_name = "fake"

    def __init__(self) -> None:
        self.messages: list[ChatMessage] = []

    async def chat(self, messages, *, model, temperature, max_tokens):
        self.messages = list(messages)
        return NormalizedCompletion(
            answer="Grounded.\nSource: https://malicious.example",
            model=model,
            provider=self.provider_name,
        )


def _hybrid(
    lexical: _Lexical,
    vector: _Vector,
    reranker: object | None = None,
    **overrides: object,
) -> HybridRetriever:
    settings = _settings(**overrides)
    return HybridRetriever(
        settings=settings,
        lexical=lexical,
        vector=vector,
        reranker=reranker or PassthroughReranker(),  # type: ignore[arg-type]
        metrics=_metrics(),
    )


def test_query_tokens_keep_technical_identifiers() -> None:
    tokens = query_tokens(
        "UnsupportedClassVersionError class file version 65 "
        "spring.kafka.bootstrap-servers KafkaConsumer"
    )
    assert "UnsupportedClassVersionError" in tokens
    assert "spring.kafka.bootstrap-servers" in tokens
    assert "KafkaConsumer" in tokens
    exact = lexical_rank_score("KafkaConsumer", "Use KafkaConsumer for the group.")
    loose = lexical_rank_score("KafkaConsumer", "The kafka consumer group rebalances.")
    assert exact > loose
    phrase = lexical_rank_score("Spring Cloud Gateway", "Spring Cloud Gateway routes traffic")
    scattered = lexical_rank_score(
        "Spring Cloud Gateway", "Spring projects use a Cloud and a Gateway"
    )
    assert phrase > scattered


@pytest.mark.asyncio
async def test_lexical_retrieval_ranks_phrases_identifiers_and_owners() -> None:
    store = InMemoryVectorStore()
    await store.upsert(
        [
            _record("gateway", "Spring Cloud Gateway routes traffic"),
            _record("scattered", "Spring projects use a Cloud and a Gateway"),
            _record("kafka", "Use KafkaConsumer for the group."),
            _record("secret", "KafkaConsumer private notes", owner="owner-2"),
            _record("property", "Set spring.kafka.bootstrap-servers to the broker."),
        ]
    )
    retriever = IndexedLexicalRetriever(_settings(rag_lexical_top_k=3), store, _metrics())
    phrase_hits = await retriever.retrieve("Spring Cloud Gateway", owner_id="owner-1")
    assert phrase_hits[0].content_id == "gateway"
    assert phrase_hits[0].lexical_rank == 1
    assert all(hit.owner_id == "owner-1" for hit in phrase_hits)

    owned = await retriever.retrieve("KafkaConsumer", owner_id="owner-1")
    assert [hit.content_id for hit in owned] == ["kafka"]

    identifier_hits = await retriever.retrieve("spring.kafka.bootstrap-servers", owner_id="owner-1")
    assert [hit.content_id for hit in identifier_hits] == ["property"]

    assert await retriever.retrieve("zzzz-not-in-the-index", owner_id="owner-1") == []

    limited = IndexedLexicalRetriever(_settings(rag_lexical_top_k=1), store, _metrics())
    one = await limited.retrieve("Spring", owner_id="owner-1")
    assert len(one) == 1


def test_rrf_uses_one_based_ranks_and_keeps_provenance() -> None:
    fused = reciprocal_rank_fusion(
        {
            "lexical": [
                _chunk("A", retrieval_sources=("lexical",), lexical_rank=1),
                _chunk("B", retrieval_sources=("lexical",), lexical_rank=2),
                _chunk("C", retrieval_sources=("lexical",), lexical_rank=3),
            ],
            "vector": [
                _chunk("C", retrieval_sources=("vector",), vector_rank=1, score=0.4),
                _chunk("A", retrieval_sources=("vector",), vector_rank=2, score=0.3),
                _chunk("D", retrieval_sources=("vector",), vector_rank=3, score=0.2),
            ],
        },
        k=60,
        limit=10,
    )
    assert [item.content_id for item in fused] == ["A", "C", "B", "D"]
    expected = {
        "A": (1 / 61) + (1 / 62),
        "C": (1 / 61) + (1 / 63),
        "B": 1 / 62,
        "D": 1 / 63,
    }
    for item in fused:
        assert item.fusion_score == pytest.approx(expected[item.content_id])
    by_id = {item.content_id: item for item in fused}
    assert by_id["A"].retrieval_sources == ("lexical", "vector")
    assert by_id["A"].chunk_id == "A"
    assert by_id["B"].retrieval_sources == ("lexical",)
    assert by_id["D"].retrieval_sources == ("vector",)


def test_weighted_fusion_normalizes_each_leg() -> None:
    lexical = [
        _chunk("A", lexical_score=10, score=10, retrieval_sources=("lexical",)),
        _chunk("B", lexical_score=0, score=0, retrieval_sources=("lexical",)),
    ]
    vector = [
        _chunk("B", vector_score=0.9, score=0.9, retrieval_sources=("vector",)),
        _chunk("A", vector_score=0.1, score=0.1, retrieval_sources=("vector",)),
    ]
    even = weighted_fusion(
        {"lexical": lexical, "vector": vector},
        lexical_weight=0.5,
        vector_weight=0.5,
        limit=10,
    )
    assert {item.content_id: item.fusion_score for item in even} == {"A": 0.5, "B": 0.5}
    lexical_only = weighted_fusion(
        {"lexical": lexical, "vector": []},
        lexical_weight=1,
        vector_weight=0,
        limit=10,
    )
    assert [item.content_id for item in lexical_only] == ["A", "B"]
    tied = weighted_fusion(
        {
            "lexical": [
                _chunk("A", lexical_score=3, retrieval_sources=("lexical",)),
                _chunk("B", lexical_score=3, retrieval_sources=("lexical",)),
            ],
            "vector": [],
        },
        lexical_weight=1,
        vector_weight=0,
        limit=10,
    )
    assert {item.fusion_score for item in tied} == {1.0}


@pytest.mark.asyncio
async def test_unauthorized_chunks_never_reach_reranker_context_or_model() -> None:
    secret = _chunk("secret", owner="owner-2", content="payroll-secret-do-not-leak")
    allowed = _chunk("allowed", content="KafkaConsumer uses a consumer group.")
    reranker = _RecordingReranker()
    hybrid = _hybrid(
        _Lexical([secret, allowed]),
        _Vector([secret, allowed]),
        reranker,
        rag_reranking_enabled=True,
        rag_top_k=5,
    )
    hits = await hybrid.retrieve("KafkaConsumer", owner_id="owner-1")
    assert [hit.content_id for hit in hits] == ["allowed"]
    assert all(chunk.owner_id == "owner-1" for chunk in reranker.seen)
    assert all("payroll-secret" not in chunk.content for chunk in reranker.seen)

    context, selected = ContextBuilder(_settings()).build(hits)
    assert "payroll-secret" not in context
    assert [chunk.content_id for chunk in selected] == ["allowed"]

    model = _FakeModel()
    engine = RagEngine(
        settings=_settings(),
        retriever=hybrid,
        context_builder=ContextBuilder(_settings()),
        prompt_builder=RagPromptBuilder(),
        metrics=_metrics(),
    )
    response = await engine.answer(
        ChatRequest(messages=[ChatMessage(role=ChatRole.USER, content="KafkaConsumer")]),
        CallerContext(owner_id="owner-1", auth_method="jwt"),
        model,
    )
    rendered = "\n".join(message.content for message in model.messages)
    assert "payroll-secret" not in rendered
    assert response.sources[0].url == "/tutorials/allowed/concept"
    assert "malicious.example" not in response.sources[0].url
    assert response.grounded is True


@pytest.mark.asyncio
async def test_reranker_can_reorder_and_failure_keeps_fused_order() -> None:
    first = _chunk("general", content="general overview")
    second = _chunk("kafka", content="Configure KafkaConsumer carefully.")
    lexical = _Lexical([first, second])
    vector = _Vector([])
    disabled = _hybrid(lexical, vector, _RecordingReranker(error=RuntimeError("should not run")))
    baseline = await disabled.retrieve("KafkaConsumer", owner_id="owner-1")
    assert [hit.content_id for hit in baseline] == ["general", "kafka"]

    enabled = _hybrid(
        _Lexical([first, second]),
        _Vector([]),
        IdentityOverlapReranker(),
        rag_reranking_enabled=True,
    )
    reordered = await enabled.retrieve("KafkaConsumer", owner_id="owner-1")
    assert [hit.content_id for hit in reordered] == ["kafka", "general"]
    assert reordered[0].final_rank == 1
    assert reordered[0].rerank_score is not None

    fallback = _hybrid(
        _Lexical([first, second]),
        _Vector([]),
        _RecordingReranker(error=RuntimeError("reranker down")),
        rag_reranking_enabled=True,
    )
    kept = await fallback.retrieve("KafkaConsumer", owner_id="owner-1")
    assert [hit.content_id for hit in kept] == ["general", "kafka"]

    timed = _hybrid(
        _Lexical([first, second]),
        _Vector([]),
        _RecordingReranker(delay=0.2),
        rag_reranking_enabled=True,
        rag_rerank_timeout_seconds=0.01,
    )
    timed_hits = await timed.retrieve("KafkaConsumer", owner_id="owner-1")
    assert [hit.content_id for hit in timed_hits] == ["general", "kafka"]


@pytest.mark.asyncio
async def test_hybrid_legs_degrade_independently_and_both_can_fail() -> None:
    allowed = _chunk("allowed", content="OpenFeign reuses connections.")
    vector_only = _hybrid(_Lexical(error=ConnectionError("lexical down")), _Vector([allowed]))
    vector_hits = await vector_only.retrieve("OpenFeign", owner_id="owner-1")
    assert [hit.content_id for hit in vector_hits] == ["allowed"]

    lexical_only = _hybrid(_Lexical([allowed]), _Vector(error=TimeoutError("vector down")))
    assert [
        hit.content_id for hit in await lexical_only.retrieve("OpenFeign", owner_id="owner-1")
    ] == ["allowed"]

    both = _hybrid(_Lexical([allowed]), _Vector([allowed]))
    merged = await both.retrieve("OpenFeign", owner_id="owner-1")
    assert merged[0].retrieval_sources == ("lexical", "vector")
    assert merged[0].content_id == "allowed"

    empty = _hybrid(_Lexical([]), _Vector([]))
    assert await empty.retrieve("nothing", owner_id="owner-1") == []

    down = _hybrid(
        _Lexical(error=ConnectionError("lexical down")),
        _Vector(error=ConnectionError("vector down")),
    )
    with pytest.raises(RagUnavailableError):
        await down.retrieve("OpenFeign", owner_id="owner-1")

    vector = _Vector([allowed])
    watched = _hybrid(_Lexical([]), vector, rag_vector_top_k=7)
    await watched.retrieve("OpenFeign", owner_id="owner-1")
    assert vector.top_k == 7
    assert vector.owner_id == "owner-1"


@pytest.mark.asyncio
async def test_lexical_timeout_still_returns_vector_hits() -> None:
    class _SlowStore(InMemoryVectorStore):
        async def keyword_search(self, text: str, *, top_k: int = 10, filters=None):
            del text, top_k, filters
            await asyncio.sleep(0.2)
            return []

    hybrid = HybridRetriever(
        settings=_settings(rag_lexical_timeout_seconds=0.01),
        lexical=IndexedLexicalRetriever(
            _settings(rag_lexical_timeout_seconds=0.01),
            _SlowStore(),
            _metrics(),
        ),
        vector=_Vector([_chunk("allowed", content="vector hit")]),
        reranker=PassthroughReranker(),
        metrics=_metrics(),
    )
    hits = await hybrid.retrieve("KafkaConsumer", owner_id="owner-1")
    assert [hit.content_id for hit in hits] == ["allowed"]


def test_reranker_factory_stays_local() -> None:
    assert isinstance(build_rag_reranker(_settings()), PassthroughReranker)
    identity = build_rag_reranker(
        _settings(rag_reranking_enabled=True, reranker_provider="identity")
    )
    assert isinstance(identity, IdentityOverlapReranker)
    cohere = build_rag_reranker(_settings(rag_reranking_enabled=True, reranker_provider="cohere"))
    assert isinstance(cohere, IdentityOverlapReranker)
    local = build_rag_reranker(
        _settings(rag_reranking_enabled=True, reranker_provider="cross_encoder")
    )
    assert isinstance(local, LocalCrossEncoderReranker)


def test_hybrid_eval_fixture_scores_strategies_without_claiming_production_quality() -> None:
    raw = json.loads(FIXTURE.read_text(encoding="utf-8"))
    cases = [HybridEvalCase(**item) for item in raw]
    assert {case.kind for case in cases} == {
        "keyword",
        "identifier",
        "semantic",
        "qa",
        "concept",
        "mixed",
        "ambiguous",
        "none",
    }
    keyword = next(case for case in cases if case.case_id == "keyword")
    vector = score_observation(
        keyword,
        StrategyObservation([], [], False),
        5,
    )
    lexical = score_observation(
        keyword,
        StrategyObservation(["outbox-concept"], ["/tutorials/outbox/concept"], True),
        5,
    )
    assert vector["recall"] == 0
    assert lexical["recall"] == 1
    assert lexical["citation_precision"] == 1
    assert lexical["grounded_match"] is True
    none = next(case for case in cases if case.case_id == "none")
    missed = score_observation(none, StrategyObservation([], [], False), 5)
    assert missed["grounded_match"] is True
    assert missed["mrr"] == 0
