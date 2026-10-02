"""Factories for agentic DI wiring."""

from __future__ import annotations

from pathlib import Path

from app.infrastructure.agentic.conversation import MemoryConversationManager
from app.infrastructure.agentic.evaluation import HeuristicAgenticEvaluator
from app.infrastructure.agentic.formatter import DefaultResponseFormatter
from app.infrastructure.agentic.graphs.engine import (
    LangGraphEngine,
    build_conditional_graph,
    build_parallel_graph,
    build_sequential_graph,
)
from app.infrastructure.agentic.memory import RedisAgenticMemory
from app.infrastructure.agentic.planner import HeuristicPlanner
from app.infrastructure.agentic.prompts import FilePromptRegistry
from app.infrastructure.agentic.reasoner import LlmReasoner
from app.infrastructure.agentic.registry import InMemoryCapabilityRegistry
from app.infrastructure.agentic.retriever import MultiSourceCapabilityRetriever
from app.infrastructure.agentic.tools.builtins import (
    CalculatorTool,
    DocumentRetrievalTool,
    KnowledgeSearchTool,
    ResumeGenerationTool,
)
from app.infrastructure.agentic.tools.registry import DefaultToolRegistry
from app.infrastructure.agentic.workflows.definitions import (
    CoverLetterWorkflow,
    InterviewWorkflow,
    PortfolioReviewWorkflow,
    ProgressEvaluationWorkflow,
    QuestionAnsweringWorkflow,
    QuizWorkflow,
    ReasoningWorkflow,
    RecommendTopicWorkflow,
    ResumeWorkflow,
    RetrievalWorkflow,
    SkillGapWorkflow,
    SummarizationWorkflow,
)
from app.infrastructure.agentic.workflows.engine import DefaultWorkflowEngine
from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.intelligence.agentic.conversation.ports import ConversationManagerPort
from app.intelligence.agentic.evaluation.ports import AgenticEvaluationPort
from app.intelligence.agentic.formatter.ports import ResponseFormatterPort
from app.intelligence.agentic.graphs.ports import GraphEnginePort
from app.intelligence.agentic.memory.ports import AgenticMemoryPort
from app.intelligence.agentic.planner.ports import PlannerPort
from app.intelligence.agentic.prompts.ports import PromptRegistryPort
from app.intelligence.agentic.reasoner.ports import ReasonerPort
from app.intelligence.agentic.registry.ports import CapabilityRegistryPort
from app.intelligence.agentic.retriever.ports import CapabilityRetrieverPort
from app.intelligence.agentic.router.ports import ModelRouterPort
from app.intelligence.agentic.tools.ports import ToolRegistryPort
from app.intelligence.agentic.workflows.ports import WorkflowEnginePort
from app.intelligence.knowledge.retrieval.ports import KnowledgeRetrieverPort
from app.intelligence.llm.ports import LlmPort
from app.orchestration.knowledge.service import KnowledgeService
from app.shared.config.settings import AppSettings
from app.shared.observability.metrics import PlatformMetrics


def build_agentic_memory(redis_adapter: RedisAdapter) -> AgenticMemoryPort:
    return RedisAgenticMemory(redis_adapter)


def build_model_router(settings: AppSettings) -> ModelRouterPort:
    from app.infrastructure.enterprise.router import ConfigurablePolicyModelRouter

    return ConfigurablePolicyModelRouter(settings)


def build_planner() -> PlannerPort:
    return HeuristicPlanner()


def build_prompt_registry(settings: AppSettings) -> PromptRegistryPort:
    root = Path(settings.agentic_prompts_root)
    if not root.is_absolute():
        root = Path.cwd() / root
    return FilePromptRegistry(root)


def build_reasoner(llm: LlmPort, router: ModelRouterPort) -> ReasonerPort:
    return LlmReasoner(llm, router)


def build_capability_retriever(
    knowledge_retriever: KnowledgeRetrieverPort,
    conversation_manager: ConversationManagerPort,
) -> CapabilityRetrieverPort:
    return MultiSourceCapabilityRetriever(knowledge_retriever, conversation_manager)


def build_conversation_manager(memory: AgenticMemoryPort) -> ConversationManagerPort:
    return MemoryConversationManager(memory)


def build_response_formatter() -> ResponseFormatterPort:
    return DefaultResponseFormatter()


def build_agentic_evaluator(langfuse: object | None) -> AgenticEvaluationPort:
    return HeuristicAgenticEvaluator(langfuse)


def build_tool_registry(
    retriever: CapabilityRetrieverPort,
    knowledge_service: KnowledgeService,
) -> ToolRegistryPort:
    registry = DefaultToolRegistry()
    registry.register(KnowledgeSearchTool(retriever))
    registry.register(DocumentRetrievalTool(knowledge_service))
    registry.register(ResumeGenerationTool())
    registry.register(CalculatorTool())
    return registry


def build_graph_engine() -> GraphEnginePort:
    engine = LangGraphEngine()

    async def plan_node(state: dict) -> dict:
        payload = dict(state.get("payload") or {})
        payload["planned"] = True
        return {"payload": payload}

    async def retrieve_node(state: dict) -> dict:
        payload = dict(state.get("payload") or {})
        payload["retrieved"] = True
        return {"payload": payload}

    async def reason_node(state: dict) -> dict:
        payload = dict(state.get("payload") or {})
        payload["reasoned"] = True
        return {"payload": payload}

    async def format_node(state: dict) -> dict:
        payload = dict(state.get("payload") or {})
        payload["formatted"] = True
        return {"payload": payload}

    async def branch_a(state: dict) -> dict:
        payload = dict(state.get("payload") or {})
        payload["branch"] = "a"
        return {"payload": payload}

    async def branch_b(state: dict) -> dict:
        payload = dict(state.get("payload") or {})
        payload["branch"] = "b"
        return {"payload": payload}

    def router(state: dict) -> str:
        payload = state.get("payload") or {}
        return "branch_a" if payload.get("route") == "a" else "branch_b"

    engine.register(
        build_sequential_graph(
            "capability_sequential",
            {
                "plan": plan_node,
                "retrieve": retrieve_node,
                "reason": reason_node,
                "format": format_node,
            },
            ["plan", "retrieve", "reason", "format"],
        )
    )
    engine.register(
        build_conditional_graph(
            "capability_conditional",
            {"route": plan_node, "branch_a": branch_a, "branch_b": branch_b},
            entry="route",
            edges=[
                ("route", "branch_a"),
                ("route", "branch_b"),
                ("branch_a", "__end__"),
                ("branch_b", "__end__"),
            ],
            conditional_edges={"route": router},
        )
    )
    engine.register(
        build_parallel_graph(
            "capability_parallel",
            {"retrieve": retrieve_node, "reason": reason_node},
        )
    )
    return engine


def build_workflow_engine(
    *,
    metrics: PlatformMetrics,
    planner: PlannerPort,
    retriever: CapabilityRetrieverPort,
    prompts: PromptRegistryPort,
    reasoner: ReasonerPort,
    formatter: ResponseFormatterPort,
    evaluator: AgenticEvaluationPort,
    memory: AgenticMemoryPort,
    tools: ToolRegistryPort,
) -> WorkflowEnginePort:
    engine = DefaultWorkflowEngine(metrics)
    engine.register(
        QuestionAnsweringWorkflow(
            planner=planner,
            retriever=retriever,
            prompts=prompts,
            reasoner=reasoner,
            formatter=formatter,
            evaluator=evaluator,
            memory=memory,
        )
    )
    engine.register(SummarizationWorkflow(reasoner, formatter))
    engine.register(RetrievalWorkflow(retriever))
    engine.register(ReasoningWorkflow(reasoner, formatter))
    engine.register(ResumeWorkflow(tools))
    engine.register(InterviewWorkflow(reasoner, formatter))
    engine.register(QuizWorkflow(reasoner, retriever))
    engine.register(PortfolioReviewWorkflow(reasoner, formatter))
    engine.register(SkillGapWorkflow(reasoner, formatter))
    engine.register(RecommendTopicWorkflow(reasoner))
    engine.register(ProgressEvaluationWorkflow(reasoner))
    engine.register(CoverLetterWorkflow(reasoner, formatter))
    return engine


def build_capability_registry(
    *,
    planner: PlannerPort,
    retriever: CapabilityRetrieverPort,
    reasoner: ReasonerPort,
    memory: AgenticMemoryPort,
    prompts: PromptRegistryPort,
    tools: ToolRegistryPort,
    evaluator: AgenticEvaluationPort,
    formatter: ResponseFormatterPort,
    conversation: ConversationManagerPort,
    workflows: WorkflowEnginePort,
    router: ModelRouterPort,
) -> CapabilityRegistryPort:
    registry = InMemoryCapabilityRegistry()
    for capability in (
        planner,
        retriever,
        reasoner,
        memory,
        prompts,
        tools,
        evaluator,
        formatter,
        conversation,
        workflows,
        router,
    ):
        registry.register(capability)  # type: ignore[arg-type]
    return registry
