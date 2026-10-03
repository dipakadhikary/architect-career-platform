"""Prometheus metrics registry for the platform."""

from __future__ import annotations

from prometheus_client import (
    CONTENT_TYPE_LATEST,
    CollectorRegistry,
    Counter,
    Histogram,
    generate_latest,
)


class PlatformMetrics:
    def __init__(self, registry: CollectorRegistry | None = None) -> None:
        self.registry = registry or CollectorRegistry()
        self.http_requests = Counter(
            "acos_ai_http_requests_total",
            "Total HTTP requests processed by the AI Platform",
            ["method", "path", "status"],
            registry=self.registry,
        )
        self.assistant_requests = Counter(
            "acos_ai_assistant_requests_total",
            "Phase 1 assistant chat requests",
            ["provider", "outcome"],
            registry=self.registry,
        )
        self.assistant_latency = Histogram(
            "acos_ai_assistant_request_duration_seconds",
            "Phase 1 assistant chat latency",
            ["provider"],
            registry=self.registry,
        )
        self.index_documents = Counter(
            "acos_ai_index_documents_total",
            "Indexing operations by outcome",
            ["outcome"],
            registry=self.registry,
        )
        self.index_chunks = Counter(
            "acos_ai_index_chunks_total",
            "Chunks written to the vector index",
            registry=self.registry,
        )
        self.embedding_requests = Counter(
            "acos_ai_embedding_requests_total",
            "Embedding batch requests",
            ["outcome"],
            registry=self.registry,
        )
        self.embedding_failures = Counter(
            "acos_ai_embedding_failures_total",
            "Embedding batches rejected before indexing",
            registry=self.registry,
        )
        self.vector_upserts = Counter(
            "acos_ai_vector_upserts_total",
            "Vectors upserted into the index",
            registry=self.registry,
        )
        self.vector_deletes = Counter(
            "acos_ai_vector_deletes_total",
            "Vector delete operations",
            registry=self.registry,
        )
        self.index_duration = Histogram(
            "acos_ai_index_duration_seconds",
            "Indexing latency",
            registry=self.registry,
        )
        self.index_retries = Counter(
            "acos_ai_index_retries_total",
            "Retry attempts during indexing",
            registry=self.registry,
        )
        self.http_latency = Histogram(
            "acos_ai_http_request_duration_seconds",
            "HTTP request latency in seconds",
            ["method", "path"],
            registry=self.registry,
        )
        self.token_usage = Counter(
            "acos_ai_token_usage_total",
            "Token usage placeholder counter",
            ["provider", "model", "direction"],
            registry=self.registry,
        )
        self.estimated_cost = Counter(
            "acos_ai_estimated_cost_usd_total",
            "Estimated cost placeholder counter in USD",
            ["provider", "model"],
            registry=self.registry,
        )
        self.knowledge_embedding_latency = Histogram(
            "acos_ai_knowledge_embedding_latency_seconds",
            "Knowledge embedding latency",
            registry=self.registry,
        )
        self.knowledge_retrieval_latency = Histogram(
            "acos_ai_knowledge_retrieval_latency_seconds",
            "Knowledge retrieval latency",
            registry=self.registry,
        )
        self.knowledge_rerank_latency = Histogram(
            "acos_ai_knowledge_rerank_latency_seconds",
            "Knowledge rerank latency",
            registry=self.registry,
        )
        self.knowledge_generation_latency = Histogram(
            "acos_ai_knowledge_generation_latency_seconds",
            "Knowledge generation latency",
            registry=self.registry,
        )
        self.knowledge_qdrant_latency = Histogram(
            "acos_ai_knowledge_vectorstore_latency_seconds",
            "Knowledge vector store latency",
            registry=self.registry,
        )
        self.agentic_workflow_runs = Counter(
            "acos_ai_agentic_workflow_runs_total",
            "Agentic workflow executions",
            ["workflow", "status"],
            registry=self.registry,
        )
        self.agentic_workflow_latency = Histogram(
            "acos_ai_agentic_workflow_latency_seconds",
            "Agentic workflow latency",
            ["workflow"],
            registry=self.registry,
        )
        self.agentic_capability_usage = Counter(
            "acos_ai_agentic_capability_usage_total",
            "Agentic capability invocations",
            ["capability"],
            registry=self.registry,
        )
        self.agentic_tool_usage = Counter(
            "acos_ai_agentic_tool_usage_total",
            "Agentic tool executions",
            ["tool"],
            registry=self.registry,
        )
        self.agentic_graph_runs = Counter(
            "acos_ai_agentic_graph_runs_total",
            "LangGraph engine runs",
            ["graph", "status"],
            registry=self.registry,
        )
        self.enterprise_pipeline_runs = Counter(
            "acos_ai_enterprise_pipeline_runs_total",
            "Enterprise AI pipeline executions",
            ["workflow", "status"],
            registry=self.registry,
        )
        self.enterprise_pipeline_latency = Histogram(
            "acos_ai_enterprise_pipeline_latency_seconds",
            "Enterprise AI pipeline latency",
            ["workflow"],
            registry=self.registry,
        )
        self.enterprise_retries = Counter(
            "acos_ai_enterprise_retries_total",
            "Enterprise pipeline retry count",
            ["workflow"],
            registry=self.registry,
        )
        self.enterprise_cache_hits = Counter(
            "acos_ai_enterprise_cache_hits_total",
            "Enterprise semantic cache hits",
            ["cache_type"],
            registry=self.registry,
        )
        self.enterprise_cost_usd = Counter(
            "acos_ai_enterprise_cost_usd_total",
            "Enterprise estimated cost by capability",
            ["capability", "provider"],
            registry=self.registry,
        )

    def render(self) -> tuple[bytes, str]:
        return generate_latest(self.registry), CONTENT_TYPE_LATEST


_METRICS: PlatformMetrics | None = None


def get_metrics() -> PlatformMetrics:
    global _METRICS
    if _METRICS is None:
        _METRICS = PlatformMetrics()
    return _METRICS
