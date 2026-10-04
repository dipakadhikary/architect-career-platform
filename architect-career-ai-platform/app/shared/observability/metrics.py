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
        self.rag_requests = Counter(
            "acos_ai_rag_requests_total",
            "RAG answers by outcome",
            ["outcome"],
            registry=self.registry,
        )
        self.rag_retrieval_latency = Histogram(
            "acos_ai_rag_retrieval_latency_seconds",
            "Query embedding plus vector search latency",
            registry=self.registry,
        )
        self.rag_embedding_latency = Histogram(
            "acos_ai_rag_embedding_latency_seconds",
            "Query embedding latency",
            registry=self.registry,
        )
        self.rag_search_latency = Histogram(
            "acos_ai_rag_search_latency_seconds",
            "Vector search latency",
            registry=self.registry,
        )
        self.rag_llm_latency = Histogram(
            "acos_ai_rag_llm_latency_seconds",
            "Grounded language-model latency",
            registry=self.registry,
        )
        self.rag_retrieved_chunks = Histogram(
            "acos_ai_rag_retrieved_chunks",
            "Chunks returned after authorization filtering",
            registry=self.registry,
        )
        self.rag_context_characters = Histogram(
            "acos_ai_rag_context_characters",
            "Characters placed in the RAG context",
            registry=self.registry,
        )
        self.rag_sources = Histogram(
            "acos_ai_rag_sources",
            "Deduplicated sources returned with an answer",
            registry=self.registry,
        )
        self.rag_hybrid_requests = Counter(
            "acos_ai_hybrid_retrieval_total",
            "Hybrid retrieval outcomes",
            ["outcome"],
            registry=self.registry,
        )
        self.rag_lexical_requests = Counter(
            "acos_ai_lexical_retrieval_total",
            "Lexical retrieval leg outcomes",
            ["outcome"],
            registry=self.registry,
        )
        self.rag_vector_leg_requests = Counter(
            "acos_ai_vector_retrieval_total",
            "Vector retrieval leg outcomes",
            ["outcome"],
            registry=self.registry,
        )
        self.rag_no_results = Counter(
            "acos_ai_retrieval_no_results_total",
            "Hybrid retrieval calls with no authorized hits",
            registry=self.registry,
        )
        self.rag_reranker_requests = Counter(
            "acos_ai_reranker_requests_total",
            "Reranker calls by outcome",
            ["outcome"],
            registry=self.registry,
        )
        self.rag_reranker_failures = Counter(
            "acos_ai_reranker_failures_total",
            "Reranker failures that fell back to fused order",
            registry=self.registry,
        )
        self.rag_lexical_latency = Histogram(
            "acos_ai_lexical_retrieval_latency_seconds",
            "Lexical retrieval latency",
            registry=self.registry,
        )
        self.rag_vector_leg_latency = Histogram(
            "acos_ai_vector_retrieval_latency_seconds",
            "Vector retrieval leg latency",
            registry=self.registry,
        )
        self.rag_fusion_latency = Histogram(
            "acos_ai_fusion_latency_seconds",
            "Result fusion latency",
            registry=self.registry,
        )
        self.rag_reranker_latency = Histogram(
            "acos_ai_reranker_latency_seconds",
            "Reranker latency",
            registry=self.registry,
        )
        self.rag_hybrid_latency = Histogram(
            "acos_ai_total_retrieval_latency_seconds",
            "Hybrid retrieval latency",
            registry=self.registry,
        )
        self.rag_candidates_before_auth = Histogram(
            "acos_ai_candidates_before_auth",
            "Candidates returned before the owner filter",
            registry=self.registry,
        )
        self.rag_candidates_after_auth = Histogram(
            "acos_ai_candidates_after_auth",
            "Unique candidates after the owner filter",
            registry=self.registry,
        )
        self.conversation_created = Counter(
            "acos_ai_conversations_created_total",
            "Conversations created",
            registry=self.registry,
        )
        self.conversation_deleted = Counter(
            "acos_ai_conversations_deleted_total",
            "Conversations deleted",
            registry=self.registry,
        )
        self.conversation_messages = Counter(
            "acos_ai_messages_total",
            "Conversation messages by role and status",
            ["role", "status"],
            registry=self.registry,
        )
        self.conversation_failures = Counter(
            "acos_ai_conversation_failures_total",
            "Assistant generations that failed after the user message was stored",
            registry=self.registry,
        )
        self.conversation_context_messages = Histogram(
            "acos_ai_conversation_context_messages",
            "Completed messages included in the model context",
            registry=self.registry,
        )
        self.conversation_context_characters = Histogram(
            "acos_ai_conversation_context_characters",
            "Characters of conversation history sent to the model",
            registry=self.registry,
        )
        self.conversation_latency = Histogram(
            "acos_ai_conversation_request_latency_seconds",
            "Conversation message request latency",
            registry=self.registry,
        )
        self.authoring_requests = Counter(
            "acos_ai_authoring_requests_total",
            "Authoring requests by operation",
            ["operation"],
            registry=self.registry,
        )
        self.authoring_success = Counter(
            "acos_ai_authoring_success_total",
            "Authoring proposals produced",
            ["operation"],
            registry=self.registry,
        )
        self.authoring_failure = Counter(
            "acos_ai_authoring_failure_total",
            "Authoring generations that failed",
            ["operation"],
            registry=self.registry,
        )
        self.authoring_regeneration = Counter(
            "acos_ai_authoring_regeneration_total",
            "Authoring regenerations",
            registry=self.registry,
        )
        self.authoring_validation_failure = Counter(
            "acos_ai_authoring_validation_failure_total",
            "Authoring outputs rejected by validation",
            registry=self.registry,
        )
        self.authoring_approval = Counter(
            "acos_ai_authoring_approval_total",
            "Authoring proposals accepted for later ACOS save",
            registry=self.registry,
        )
        self.authoring_rejection = Counter(
            "acos_ai_authoring_rejection_total",
            "Authoring proposals rejected",
            registry=self.registry,
        )
        self.authoring_latency = Histogram(
            "acos_ai_authoring_latency_seconds",
            "Authoring generation latency",
            registry=self.registry,
        )
        self.authoring_tokens_input = Counter(
            "acos_ai_authoring_tokens_input_total",
            "Authoring prompt tokens reported by the provider",
            registry=self.registry,
        )
        self.authoring_tokens_output = Counter(
            "acos_ai_authoring_tokens_output_total",
            "Authoring completion tokens reported by the provider",
            registry=self.registry,
        )
        self.rag_final_chunks = Histogram(
            "acos_ai_final_context_chunks",
            "Chunks sent to context construction",
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
