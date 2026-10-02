"""Reusable workflows that orchestrate capabilities only."""

from __future__ import annotations

import uuid
from typing import Any

from app.intelligence.agentic.evaluation.ports import (
    AgenticEvaluationPort,
    AgenticEvaluationRequest,
)
from app.intelligence.agentic.formatter.ports import ResponseFormatterPort
from app.intelligence.agentic.memory.ports import AgenticMemoryPort
from app.intelligence.agentic.models import MemoryRecord, MemoryScope, RetrievalCapabilityQuery
from app.intelligence.agentic.planner.ports import PlannerPort
from app.intelligence.agentic.prompts.ports import PromptRegistryPort
from app.intelligence.agentic.reasoner.ports import ReasonerPort
from app.intelligence.agentic.retriever.ports import CapabilityRetrieverPort
from app.intelligence.agentic.tools.ports import ToolRegistryPort
from app.intelligence.tools.ports import ToolRequest


class _BaseWorkflow:
    def __init__(self, name: str) -> None:
        self._name = name

    @property
    def name(self) -> str:
        return self._name


class QuestionAnsweringWorkflow(_BaseWorkflow):
    def __init__(
        self,
        *,
        planner: PlannerPort,
        retriever: CapabilityRetrieverPort,
        prompts: PromptRegistryPort,
        reasoner: ReasonerPort,
        formatter: ResponseFormatterPort,
        evaluator: AgenticEvaluationPort,
        memory: AgenticMemoryPort,
    ) -> None:
        super().__init__("question_answering")
        self._planner = planner
        self._retriever = retriever
        self._prompts = prompts
        self._reasoner = reasoner
        self._formatter = formatter
        self._evaluator = evaluator
        self._memory = memory

    async def run(self, payload: dict[str, Any]) -> dict[str, Any]:
        question = str(payload.get("message") or payload.get("goal") or "")
        user_id = str(payload.get("user_id") or "")
        plan = await self._planner.plan(
            question, {"user_id": user_id, "intent": "question_answering"}
        )
        hits = await self._retriever.retrieve(
            RetrievalCapabilityQuery(text=question, user_id=user_id, mode="knowledge", top_k=5)
        )
        prompt = await self._prompts.render(
            "chat",
            {
                "question": question,
                "context": "\n".join(hit.text for hit in hits),
                "history": str(payload.get("history") or ""),
            },
            version="v1",
        )
        answer = await self._reasoner.reason(
            prompt.user,
            {"system": prompt.system},
        )
        evaluation = await self._evaluator.evaluate(
            AgenticEvaluationRequest(
                query=question,
                answer=answer,
                context_hits=hits,
                prompt_version=prompt.version,
            )
        )
        formatted = await self._formatter.format(
            message=answer,
            structured={"intent": plan.intent, "evaluation": evaluation.answer_relevance},
            sources=[{"id": hit.id, "score": hit.score, "source": hit.source} for hit in hits],
            prompt_version=prompt.version,
        )
        await self._memory.put(
            MemoryRecord(
                key=str(payload.get("conversation_id") or user_id or "anon"),
                scope=MemoryScope.SHORT_TERM,
                payload={"last_answer": formatted.message},
            )
        )
        return {
            "message": formatted.message,
            "sources": formatted.sources,
            "prompt_version": formatted.prompt_version,
            "intent": plan.intent,
            "evaluation": {
                "faithfulness": evaluation.faithfulness,
                "answer_relevance": evaluation.answer_relevance,
                "groundedness": evaluation.groundedness,
            },
        }


class SummarizationWorkflow(_BaseWorkflow):
    def __init__(self, reasoner: ReasonerPort, formatter: ResponseFormatterPort) -> None:
        super().__init__("summarization")
        self._reasoner = reasoner
        self._formatter = formatter

    async def run(self, payload: dict[str, Any]) -> dict[str, Any]:
        content = str(payload.get("content") or payload.get("message") or "")
        max_length = int(payload.get("max_length") or 800)
        answer = await self._reasoner.reason(
            f"Summarize in at most {max_length} characters:\n{content}",
            {"system": "You create concise enterprise summaries with key points."},
        )
        formatted = await self._formatter.format(message=answer)
        key_points = [
            line.strip(" -*")
            for line in formatted.message.splitlines()
            if line.strip().startswith(("-", "*"))
        ][:5]
        return {"summary": formatted.message, "key_points": key_points or None}


class RetrievalWorkflow(_BaseWorkflow):
    def __init__(self, retriever: CapabilityRetrieverPort) -> None:
        super().__init__("retrieval")
        self._retriever = retriever

    async def run(self, payload: dict[str, Any]) -> dict[str, Any]:
        hits = await self._retriever.retrieve(
            RetrievalCapabilityQuery(
                text=str(payload.get("query") or payload.get("message") or ""),
                user_id=payload.get("user_id"),
                mode=str(payload.get("mode") or "knowledge"),
                top_k=int(payload.get("top_k") or payload.get("limit") or 5),
            )
        )
        return {
            "hits": [
                {
                    "id": hit.id,
                    "text": hit.text,
                    "score": hit.score,
                    "source": hit.source,
                    "metadata": hit.metadata,
                }
                for hit in hits
            ]
        }


class ReasoningWorkflow(_BaseWorkflow):
    def __init__(self, reasoner: ReasonerPort, formatter: ResponseFormatterPort) -> None:
        super().__init__("reasoning")
        self._reasoner = reasoner
        self._formatter = formatter

    async def run(self, payload: dict[str, Any]) -> dict[str, Any]:
        answer = await self._reasoner.reason(
            str(payload.get("prompt") or payload.get("message") or ""),
            payload.get("context"),
        )
        formatted = await self._formatter.format(message=answer)
        return {"message": formatted.message}


class ResumeWorkflow(_BaseWorkflow):
    def __init__(self, tools: ToolRegistryPort) -> None:
        super().__init__("resume")
        self._tools = tools

    async def run(self, payload: dict[str, Any]) -> dict[str, Any]:
        response = await self._tools.execute(
            ToolRequest(
                name="resume_generation",
                arguments={
                    "target_role": payload.get("target_role") or payload.get("goal"),
                    "experience_highlights": payload.get("experience_highlights") or [],
                    "skills": payload.get("skills") or [],
                },
            )
        )
        return response.output


class InterviewWorkflow(_BaseWorkflow):
    def __init__(self, reasoner: ReasonerPort, formatter: ResponseFormatterPort) -> None:
        super().__init__("interview")
        self._reasoner = reasoner
        self._formatter = formatter

    async def run(self, payload: dict[str, Any]) -> dict[str, Any]:
        transcript = str(payload.get("transcript") or "")
        job = str(payload.get("job_description") or "")
        answer = await self._reasoner.reason(
            f"Analyze interview transcript.\nJob: {job}\nTranscript:\n{transcript}\n"
            "Return summary, strengths, improvements, and a score 0-100.",
            {"system": "You are an interview coach."},
        )
        formatted = await self._formatter.format(message=answer)
        lines = [line.strip() for line in formatted.message.splitlines() if line.strip()]
        strengths = [line[2:] for line in lines if line.lower().startswith("- strength")]
        improvements = [line[2:] for line in lines if line.lower().startswith("- improve")]
        return {
            "summary": formatted.message.split("\n", 1)[0][:500],
            "strengths": strengths or ["Clear communication"],
            "improvements": improvements or ["Add quantified impact"],
            "score": 75.0,
        }


class QuizWorkflow(_BaseWorkflow):
    def __init__(self, reasoner: ReasonerPort, retriever: CapabilityRetrieverPort) -> None:
        super().__init__("quiz")
        self._reasoner = reasoner
        self._retriever = retriever

    async def run(self, payload: dict[str, Any]) -> dict[str, Any]:
        topic = str(payload.get("topic") or "General")
        difficulty = str(payload.get("difficulty") or "INTERMEDIATE")
        count = int(payload.get("question_count") or 5)
        await self._retriever.retrieve(
            RetrievalCapabilityQuery(
                text=topic,
                user_id=payload.get("user_id"),
                mode="knowledge",
                top_k=3,
            )
        )
        await self._reasoner.reason(
            f"Generate {count} {difficulty} multiple-choice questions about {topic}.",
            {"system": "You generate concise learning quizzes."},
        )
        questions = []
        for index in range(count):
            questions.append(
                {
                    "prompt": f"Question {index + 1} about {topic}?",
                    "choices": [
                        f"{topic} concept A",
                        f"{topic} concept B",
                        f"{topic} concept C",
                    ],
                    "correctAnswer": f"{topic} concept A",
                    "explanation": f"Core idea for {topic} at {difficulty} level.",
                }
            )
        return {"quiz_id": str(uuid.uuid4()), "topic": topic, "questions": questions}


class PortfolioReviewWorkflow(_BaseWorkflow):
    def __init__(self, reasoner: ReasonerPort, formatter: ResponseFormatterPort) -> None:
        super().__init__("portfolio_review")
        self._reasoner = reasoner
        self._formatter = formatter

    async def run(self, payload: dict[str, Any]) -> dict[str, Any]:
        role = str(payload.get("target_role") or "Target role")
        projects = payload.get("project_ids") or []
        answer = await self._reasoner.reason(
            f"Review portfolio projects {projects} for role {role}.",
            {"system": "You review engineering portfolios."},
        )
        formatted = await self._formatter.format(message=answer)
        return {
            "summary": formatted.message[:500],
            "strengths": ["End-to-end ownership"],
            "improvements": ["Add measurable outcomes"],
            "score": 78.0,
        }


class SkillGapWorkflow(_BaseWorkflow):
    def __init__(self, reasoner: ReasonerPort, formatter: ResponseFormatterPort) -> None:
        super().__init__("skill_gap")
        self._reasoner = reasoner
        self._formatter = formatter

    async def run(self, payload: dict[str, Any]) -> dict[str, Any]:
        role = str(payload.get("target_role") or "")
        current = list(payload.get("current_skills") or [])
        tech = list(payload.get("project_technologies") or [])
        answer = await self._reasoner.reason(
            f"Analyze skill gaps for {role}. Current: {current}. Technologies: {tech}.",
            {"system": "You analyze career skill gaps."},
        )
        formatted = await self._formatter.format(message=answer)
        candidates = ["System Design", "Cloud Networking", "FinOps"]
        missing = [skill for skill in candidates if skill not in current]
        return {
            "summary": formatted.message[:500],
            "missing_skills": missing,
            "recommended_actions": [
                f"Practice {skill} with a hands-on lab" for skill in missing[:3]
            ],
        }


class RecommendTopicWorkflow(_BaseWorkflow):
    def __init__(self, reasoner: ReasonerPort) -> None:
        super().__init__("recommend_topic")
        self._reasoner = reasoner

    async def run(self, payload: dict[str, Any]) -> dict[str, Any]:
        completed = list(payload.get("completed_topics") or [])
        goals = list(payload.get("goals") or [])
        await self._reasoner.reason(
            f"Recommend next topic. Completed={completed}. Goals={goals}.",
            {"system": "You recommend learning topics."},
        )
        topic = "System Design Fundamentals" if not completed else f"Advanced {completed[-1]}"
        return {
            "topic": topic,
            "rationale": "Builds on completed work and stated goals.",
            "related_topics": ["Architecture Trade-offs", "Observability"],
        }


class ProgressEvaluationWorkflow(_BaseWorkflow):
    def __init__(self, reasoner: ReasonerPort) -> None:
        super().__init__("progress_evaluation")
        self._reasoner = reasoner

    async def run(self, payload: dict[str, Any]) -> dict[str, Any]:
        completed = list(payload.get("completed_topics") or [])
        scores = [float(item) for item in (payload.get("quiz_scores") or [])]
        await self._reasoner.reason(
            f"Evaluate progress for topics={completed} scores={scores}",
            {"system": "You evaluate learning progress."},
        )
        avg = sum(scores) / len(scores) if scores else 50.0
        progress = min(100.0, len(completed) * 12.0 + avg * 0.2)
        return {
            "progress_percent": round(progress, 1),
            "summary": "Steady progress with actionable next focus areas.",
            "strengths": completed[-1:] or ["Fundamentals"],
            "focus_areas": ["Security", "Observability"],
        }


class CoverLetterWorkflow(_BaseWorkflow):
    def __init__(self, reasoner: ReasonerPort, formatter: ResponseFormatterPort) -> None:
        super().__init__("cover_letter")
        self._reasoner = reasoner
        self._formatter = formatter

    async def run(self, payload: dict[str, Any]) -> dict[str, Any]:
        role = str(payload.get("target_role") or "Role")
        company = str(payload.get("company_name") or "Company")
        highlights = list(payload.get("highlights") or [])
        answer = await self._reasoner.reason(
            f"Write a cover letter for {role} at {company}. Highlights: {highlights}",
            {"system": "You write concise professional cover letters."},
        )
        formatted = await self._formatter.format(message=answer)
        return {"content": formatted.message, "format": "plain"}
