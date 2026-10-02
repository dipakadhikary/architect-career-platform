"""Dependency injection application container."""

from __future__ import annotations

from dependency_injector import containers, providers

from app.infrastructure.agentic.factory import (
    build_agentic_evaluator,
    build_agentic_memory,
    build_capability_registry,
    build_capability_retriever,
    build_conversation_manager,
    build_graph_engine,
    build_model_router,
    build_planner,
    build_prompt_registry,
    build_reasoner,
    build_response_formatter,
    build_tool_registry,
    build_workflow_engine,
)
from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.infrastructure.enterprise.factory import (
    build_agent_delegation,
    build_agent_registry,
    build_ai_pipeline,
    build_audit_logger,
    build_cost_tracker,
    build_data_masker,
    build_enterprise_evaluator,
    build_guardrails,
    build_mcp_client,
    build_mcp_resource_registry,
    build_mcp_tool_registry,
    build_mcp_transport,
    build_pipelined_agentic,
    build_pipelined_knowledge,
    build_policy_engine,
    build_prompt_governance,
    build_prompt_sanitizer,
    build_semantic_cache,
)
from app.infrastructure.http.httpx_client import HttpxClientFactory
from app.infrastructure.knowledge.embeddings.factory import build_embedding_port
from app.infrastructure.knowledge.factory import (
    build_chunker_registry,
    build_context_builder,
    build_document_ingestion,
    build_knowledge_retriever,
    build_knowledge_service,
    build_metadata_extractor,
    build_metadata_repository,
    build_preprocessor,
    build_prompt_builder,
    build_rag_cache,
    build_rag_evaluator,
    build_response_builder,
)
from app.infrastructure.knowledge.reranking.providers import build_reranker
from app.infrastructure.knowledge.vectorstore.factory import build_vector_store
from app.infrastructure.llm.azure_openai_adapter import AzureOpenAIAdapter
from app.infrastructure.llm.factory import build_llm_port
from app.infrastructure.llm.ollama_adapter import OllamaAdapter
from app.infrastructure.llm.openai_adapter import OpenAIAdapter
from app.infrastructure.observability.langfuse_adapter import LangfuseAdapter
from app.infrastructure.observability.otel import configure_otel
from app.infrastructure.storage.filesystem_adapter import FilesystemAdapter
from app.infrastructure.vector.qdrant_adapter import QdrantAdapter
from app.orchestration.agentic.service import AgenticOrchestrationService
from app.shared.config.settings import get_settings
from app.shared.observability.metrics import get_metrics
from app.shared.security.authentication import AuthenticationService


class ApplicationContainer(containers.DeclarativeContainer):
    config = providers.Singleton(get_settings)
    metrics = providers.Singleton(get_metrics)
    authentication_service = providers.Factory(AuthenticationService, settings=config)
    http_client_factory = providers.Singleton(HttpxClientFactory, settings=config)
    redis_adapter = providers.Singleton(RedisAdapter, settings=config)
    qdrant_adapter = providers.Singleton(QdrantAdapter, settings=config)
    filesystem_adapter = providers.Singleton(FilesystemAdapter)
    langfuse_adapter = providers.Singleton(LangfuseAdapter, settings=config)
    openai_adapter = providers.Singleton(OpenAIAdapter, settings=config)
    azure_openai_adapter = providers.Singleton(AzureOpenAIAdapter, settings=config)
    ollama_adapter = providers.Singleton(OllamaAdapter, settings=config)

    # Knowledge / Enterprise RAG
    document_ingestion = providers.Singleton(build_document_ingestion)
    preprocessor = providers.Singleton(build_preprocessor)
    metadata_extractor = providers.Singleton(build_metadata_extractor)
    metadata_repository = providers.Singleton(
        build_metadata_repository, settings=config, redis_adapter=redis_adapter
    )
    chunker_registry = providers.Singleton(build_chunker_registry)
    embedding_port = providers.Singleton(build_embedding_port, settings=config)
    vector_store = providers.Singleton(build_vector_store, settings=config, qdrant=qdrant_adapter)
    rag_cache = providers.Singleton(build_rag_cache, redis_adapter=redis_adapter)
    knowledge_retriever = providers.Singleton(
        build_knowledge_retriever,
        embeddings=embedding_port,
        store=vector_store,
        cache=rag_cache,
        metrics=metrics,
        settings=config,
    )
    knowledge_reranker = providers.Singleton(build_reranker, settings=config, metrics=metrics)
    context_builder = providers.Singleton(build_context_builder)
    prompt_builder = providers.Singleton(build_prompt_builder, settings=config)
    response_builder = providers.Singleton(build_response_builder)
    llm_port = providers.Singleton(build_llm_port, settings=config)
    rag_evaluator = providers.Singleton(build_rag_evaluator)
    knowledge_service = providers.Factory(
        build_knowledge_service,
        settings=config,
        ingestion=document_ingestion,
        preprocessor=preprocessor,
        metadata_extractor=metadata_extractor,
        metadata_repository=metadata_repository,
        chunker_registry=chunker_registry,
        embeddings=embedding_port,
        vector_store=vector_store,
        retriever=knowledge_retriever,
        reranker=knowledge_reranker,
        context_builder=context_builder,
        prompt_builder=prompt_builder,
        response_builder=response_builder,
        llm=llm_port,
        evaluator=rag_evaluator,
        metrics=metrics,
        langfuse=langfuse_adapter,
    )

    # Agentic AI Platform
    agentic_memory = providers.Singleton(build_agentic_memory, redis_adapter=redis_adapter)
    model_router = providers.Singleton(build_model_router, settings=config)
    agentic_planner = providers.Singleton(build_planner)
    agentic_prompt_registry = providers.Singleton(build_prompt_registry, settings=config)
    agentic_reasoner = providers.Singleton(build_reasoner, llm=llm_port, router=model_router)
    conversation_manager = providers.Singleton(build_conversation_manager, memory=agentic_memory)
    capability_retriever = providers.Singleton(
        build_capability_retriever,
        knowledge_retriever=knowledge_retriever,
        conversation_manager=conversation_manager,
    )
    agentic_formatter = providers.Singleton(build_response_formatter)
    agentic_evaluator = providers.Singleton(build_agentic_evaluator, langfuse=langfuse_adapter)
    tool_registry = providers.Singleton(
        build_tool_registry,
        retriever=capability_retriever,
        knowledge_service=knowledge_service,
    )
    workflow_engine = providers.Singleton(
        build_workflow_engine,
        metrics=metrics,
        planner=agentic_planner,
        retriever=capability_retriever,
        prompts=agentic_prompt_registry,
        reasoner=agentic_reasoner,
        formatter=agentic_formatter,
        evaluator=agentic_evaluator,
        memory=agentic_memory,
        tools=tool_registry,
    )
    graph_engine = providers.Singleton(build_graph_engine)
    capability_registry = providers.Singleton(
        build_capability_registry,
        planner=agentic_planner,
        retriever=capability_retriever,
        reasoner=agentic_reasoner,
        memory=agentic_memory,
        prompts=agentic_prompt_registry,
        tools=tool_registry,
        evaluator=agentic_evaluator,
        formatter=agentic_formatter,
        conversation=conversation_manager,
        workflows=workflow_engine,
        router=model_router,
    )
    agentic_service = providers.Factory(
        AgenticOrchestrationService,
        workflows=workflow_engine,
        graphs=graph_engine,
        conversations=conversation_manager,
        registry=capability_registry,
        metrics=metrics,
        langfuse=langfuse_adapter,
    )

    # Enterprise production controls
    guardrails = providers.Singleton(build_guardrails, settings=config)
    policy_engine = providers.Singleton(build_policy_engine, settings=config)
    prompt_governance = providers.Singleton(build_prompt_governance, settings=config)
    semantic_cache = providers.Singleton(build_semantic_cache, redis_adapter=redis_adapter)
    cost_tracker = providers.Singleton(build_cost_tracker, metrics=metrics)
    enterprise_evaluator = providers.Singleton(
        build_enterprise_evaluator, langfuse=langfuse_adapter
    )
    audit_logger = providers.Singleton(build_audit_logger)
    prompt_sanitizer = providers.Singleton(build_prompt_sanitizer)
    data_masker = providers.Singleton(build_data_masker)
    mcp_transport = providers.Singleton(build_mcp_transport)
    mcp_tool_registry = providers.Singleton(build_mcp_tool_registry)
    mcp_resource_registry = providers.Singleton(build_mcp_resource_registry)
    mcp_client = providers.Singleton(
        build_mcp_client,
        transport=mcp_transport,
        tools=mcp_tool_registry,
        resources=mcp_resource_registry,
    )
    agent_registry = providers.Singleton(build_agent_registry)
    agent_delegation = providers.Singleton(build_agent_delegation, registry=agent_registry)
    ai_pipeline = providers.Singleton(
        build_ai_pipeline,
        settings=config,
        guardrails=guardrails,
        policy=policy_engine,
        governance=prompt_governance,
        router=model_router,
        cache=semantic_cache,
        cost_tracker=cost_tracker,
        evaluator=enterprise_evaluator,
        audit=audit_logger,
        sanitizer=prompt_sanitizer,
        masker=data_masker,
        metrics=metrics,
        embeddings=embedding_port,
    )
    pipelined_agentic_service = providers.Factory(
        build_pipelined_agentic,
        inner=agentic_service,
        pipeline=ai_pipeline,
    )
    pipelined_knowledge_service = providers.Factory(
        build_pipelined_knowledge,
        inner=knowledge_service,
        pipeline=ai_pipeline,
    )


def bootstrap_observability() -> None:
    configure_otel(get_settings())


container = ApplicationContainer()
