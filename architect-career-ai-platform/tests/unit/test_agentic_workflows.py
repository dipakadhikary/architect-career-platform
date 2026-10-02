"""Unit tests for reusable agentic workflows."""

from __future__ import annotations

import pytest
from app.infrastructure.agentic.formatter import DefaultResponseFormatter
from app.infrastructure.agentic.memory import RedisAgenticMemory
from app.infrastructure.agentic.planner import HeuristicPlanner
from app.infrastructure.agentic.prompts import FilePromptRegistry
from app.infrastructure.agentic.reasoner import LlmReasoner
from app.infrastructure.agentic.retriever import MultiSourceCapabilityRetriever
from app.infrastructure.agentic.router import PolicyModelRouter
from app.infrastructure.agentic.tools.builtins import ResumeGenerationTool
from app.infrastructure.agentic.tools.registry import DefaultToolRegistry
from app.infrastructure.agentic.workflows.definitions import (
    QuestionAnsweringWorkflow,
    ResumeWorkflow,
    RetrievalWorkflow,
)
from app.infrastructure.agentic.workflows.engine import DefaultWorkflowEngine
from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.infrastructure.llm.factory import ExtractiveSummarizer
from app.intelligence.agentic.evaluation.ports import (
    AgenticEvaluationPort,
    AgenticEvaluationRequest,
)
from app.intelligence.agentic.models import AgenticEvaluation
from app.intelligence.knowledge.models import RetrievalHit
from app.intelligence.knowledge.retrieval.ports import (
    KnowledgeRetrievalQuery,
    KnowledgeRetrieverPort,
)
from app.orchestration.workflows import WorkflowRequest
from app.shared.config.settings import AppSettings
from app.shared.observability.metrics import PlatformMetrics


class _FakeKnowledgeRetriever(KnowledgeRetrieverPort):
    async def retrieve(self, query: KnowledgeRetrievalQuery) -> list[RetrievalHit]:
        return [
            RetrievalHit(
                id="1",
                document_id="d1",
                text="Clean Architecture separates business rules from frameworks.",
                score=0.9,
                metadata={"user_id": query.filters.get("user_id")},
            )
        ]


class _FakeEvaluator(AgenticEvaluationPort):
    async def evaluate(self, request: AgenticEvaluationRequest) -> AgenticEvaluation:
        return AgenticEvaluation(
            faithfulness=0.8,
            answer_relevance=0.7,
            context_relevance=0.6,
            groundedness=0.75,
            latency_ms=1.0,
            prompt_tokens=10,
            completion_tokens=20,
            total_tokens=30,
            estimated_cost_usd=0.0,
            prompt_version=request.prompt_version,
            retriever_quality=0.9,
            llm_quality=0.8,
        )


@pytest.mark.asyncio
async def test_question_answering_and_resume_workflows(settings: AppSettings) -> None:
    metrics = PlatformMetrics()
    memory = RedisAgenticMemory(RedisAdapter(settings))
    retriever = MultiSourceCapabilityRetriever(_FakeKnowledgeRetriever())
    reasoner = LlmReasoner(ExtractiveSummarizer(), PolicyModelRouter(settings))
    formatter = DefaultResponseFormatter()
    prompts = FilePromptRegistry("prompts/agentic")
    tools = DefaultToolRegistry()
    tools.register(ResumeGenerationTool())

    engine = DefaultWorkflowEngine(metrics)
    engine.register(
        QuestionAnsweringWorkflow(
            planner=HeuristicPlanner(),
            retriever=retriever,
            prompts=prompts,
            reasoner=reasoner,
            formatter=formatter,
            evaluator=_FakeEvaluator(),
            memory=memory,
        )
    )
    engine.register(RetrievalWorkflow(retriever))
    engine.register(ResumeWorkflow(tools))

    qa = await engine.execute(
        WorkflowRequest(
            name="question_answering",
            payload={"user_id": "u1", "message": "What is clean architecture?"},
        )
    )
    assert qa.status == "SUCCEEDED"
    assert qa.output["message"]

    retrieval = await engine.execute(
        WorkflowRequest(name="retrieval", payload={"user_id": "u1", "query": "architecture"})
    )
    assert retrieval.output["hits"]

    resume = await engine.execute(
        WorkflowRequest(
            name="resume",
            payload={
                "target_role": "Architect",
                "experience_highlights": ["Led platform work"],
                "skills": ["Python"],
            },
        )
    )
    assert "Architect" in resume.output["content"]
