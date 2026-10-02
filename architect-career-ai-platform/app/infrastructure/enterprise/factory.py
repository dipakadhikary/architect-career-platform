"""Enterprise component factories."""

from __future__ import annotations

from pathlib import Path

from app.infrastructure.agentic.prompts import FilePromptRegistry
from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.infrastructure.enterprise.a2a import InMemoryAgentRegistry, LocalAgentDelegation
from app.infrastructure.enterprise.audit import StructlogAuditLogger
from app.infrastructure.enterprise.cache import RedisSemanticCache
from app.infrastructure.enterprise.cost import InMemoryCostTracker
from app.infrastructure.enterprise.evaluation import PersistentEnterpriseEvaluator
from app.infrastructure.enterprise.governance import PromptGovernanceService
from app.infrastructure.enterprise.guardrails import HeuristicGuardrails
from app.infrastructure.enterprise.mcp import (
    InMemoryMcpResourceRegistry,
    InMemoryMcpToolRegistry,
    InMemoryMcpTransport,
    StubMcpClient,
)
from app.infrastructure.enterprise.policy import ConfigurablePolicyEngine
from app.infrastructure.enterprise.router import ConfigurablePolicyModelRouter
from app.infrastructure.enterprise.security import DefaultPromptSanitizer, RegexDataMasker
from app.intelligence.agentic.router.ports import ModelRouterPort
from app.intelligence.embeddings.ports import EmbeddingPort
from app.intelligence.enterprise.a2a.ports import AgentDelegationPort, AgentRegistryPort
from app.intelligence.enterprise.audit.ports import AuditLogPort
from app.intelligence.enterprise.cache.ports import SemanticCachePort
from app.intelligence.enterprise.cost.ports import CostTrackerPort
from app.intelligence.enterprise.evaluation.ports import EnterpriseEvaluationPort
from app.intelligence.enterprise.governance.ports import PromptGovernancePort
from app.intelligence.enterprise.guardrails.ports import GuardrailsPort
from app.intelligence.enterprise.mcp.ports import (
    McpClientPort,
    McpResourceRegistryPort,
    McpToolRegistryPort,
    McpTransportPort,
)
from app.intelligence.enterprise.pipeline.ports import AiExecutionPipelinePort
from app.intelligence.enterprise.policy.ports import PolicyEnginePort
from app.intelligence.enterprise.security.ports import DataMaskerPort, PromptSanitizerPort
from app.orchestration.agentic.service import AgenticOrchestrationService
from app.orchestration.enterprise.facades import PipelinedAgenticFacade, PipelinedKnowledgeFacade
from app.orchestration.enterprise.pipeline import AiExecutionPipeline
from app.orchestration.knowledge.service import KnowledgeService
from app.shared.config.settings import AppSettings
from app.shared.observability.metrics import PlatformMetrics


def build_enterprise_model_router(settings: AppSettings) -> ModelRouterPort:
    return ConfigurablePolicyModelRouter(settings)


def build_guardrails(settings: AppSettings) -> GuardrailsPort:
    return HeuristicGuardrails(settings)


def build_policy_engine(settings: AppSettings) -> PolicyEnginePort:
    return ConfigurablePolicyEngine(settings)


def build_prompt_governance(settings: AppSettings) -> PromptGovernancePort:
    root = Path(settings.agentic_prompts_root)
    if not root.is_absolute():
        root = Path.cwd() / root
    registry = FilePromptRegistry(root)
    governance = PromptGovernanceService(registry)
    for family in ("chat", "quiz", "resume"):
        versions = registry.list_versions(family)
        if versions:
            governance.approve(family, versions[-1], actor="bootstrap")
    return governance


def build_semantic_cache(redis_adapter: RedisAdapter) -> SemanticCachePort:
    return RedisSemanticCache(redis_adapter)


def build_cost_tracker(metrics: PlatformMetrics) -> CostTrackerPort:
    return InMemoryCostTracker(metrics)


def build_enterprise_evaluator(langfuse: object | None) -> EnterpriseEvaluationPort:
    return PersistentEnterpriseEvaluator(langfuse)


def build_audit_logger() -> AuditLogPort:
    return StructlogAuditLogger()


def build_prompt_sanitizer() -> PromptSanitizerPort:
    return DefaultPromptSanitizer()


def build_data_masker() -> DataMaskerPort:
    return RegexDataMasker()


def build_mcp_transport() -> McpTransportPort:
    return InMemoryMcpTransport()


def build_mcp_tool_registry() -> McpToolRegistryPort:
    return InMemoryMcpToolRegistry()


def build_mcp_resource_registry() -> McpResourceRegistryPort:
    return InMemoryMcpResourceRegistry()


def build_mcp_client(
    transport: McpTransportPort,
    tools: McpToolRegistryPort,
    resources: McpResourceRegistryPort,
) -> McpClientPort:
    return StubMcpClient(transport, tools, resources)


def build_agent_registry() -> AgentRegistryPort:
    return InMemoryAgentRegistry()


def build_agent_delegation(registry: AgentRegistryPort) -> AgentDelegationPort:
    return LocalAgentDelegation(registry)


def build_ai_pipeline(
    *,
    settings: AppSettings,
    guardrails: GuardrailsPort,
    policy: PolicyEnginePort,
    governance: PromptGovernancePort,
    router: ModelRouterPort,
    cache: SemanticCachePort,
    cost_tracker: CostTrackerPort,
    evaluator: EnterpriseEvaluationPort,
    audit: AuditLogPort,
    sanitizer: PromptSanitizerPort,
    masker: DataMaskerPort,
    metrics: PlatformMetrics,
    embeddings: EmbeddingPort,
) -> AiExecutionPipelinePort:
    return AiExecutionPipeline(
        settings=settings,
        guardrails=guardrails,
        policy=policy,
        governance=governance,
        router=router,
        cache=cache,
        cost_tracker=cost_tracker,
        evaluator=evaluator,
        audit=audit,
        sanitizer=sanitizer,
        masker=masker,
        metrics=metrics,
        embeddings=embeddings,
    )


def build_pipelined_agentic(
    inner: AgenticOrchestrationService,
    pipeline: AiExecutionPipelinePort,
) -> PipelinedAgenticFacade:
    return PipelinedAgenticFacade(inner, pipeline)


def build_pipelined_knowledge(
    inner: KnowledgeService,
    pipeline: AiExecutionPipelinePort,
) -> PipelinedKnowledgeFacade:
    return PipelinedKnowledgeFacade(inner, pipeline)
