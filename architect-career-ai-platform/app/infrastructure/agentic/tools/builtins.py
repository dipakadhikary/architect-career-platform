"""Built-in tools: knowledge search, document retrieval, resume generation, calculator."""

from __future__ import annotations

import ast
import operator
from typing import Any, ClassVar

from app.intelligence.agentic.models import RetrievalCapabilityQuery, ToolSpec
from app.intelligence.agentic.retriever.ports import CapabilityRetrieverPort
from app.intelligence.agentic.tools.ports import ExecutableTool
from app.intelligence.tools.ports import ToolRequest, ToolResponse
from app.orchestration.knowledge.service import KnowledgeService
from app.shared.exceptions import ValidationFailedError


class KnowledgeSearchTool(ExecutableTool):
    def __init__(self, retriever: CapabilityRetrieverPort) -> None:
        self._retriever = retriever

    @property
    def spec(self) -> ToolSpec:
        return ToolSpec(
            name="knowledge_search",
            description="Search indexed knowledge for a user query",
            parameters_schema={"query": "string", "user_id": "string", "top_k": "integer"},
        )

    async def execute(self, request: ToolRequest) -> ToolResponse:
        hits = await self._retriever.retrieve(
            RetrievalCapabilityQuery(
                text=str(request.arguments.get("query") or ""),
                user_id=request.arguments.get("user_id"),
                mode="knowledge",
                top_k=int(request.arguments.get("top_k") or 5),
            )
        )
        return ToolResponse(
            name=self.spec.name,
            output={
                "hits": [
                    {"id": hit.id, "text": hit.text, "score": hit.score, "source": hit.source}
                    for hit in hits
                ]
            },
        )


class DocumentRetrievalTool(ExecutableTool):
    def __init__(self, knowledge_service: KnowledgeService) -> None:
        self._knowledge = knowledge_service

    @property
    def spec(self) -> ToolSpec:
        return ToolSpec(
            name="document_retrieval",
            description="Search knowledge documents by user and query",
            parameters_schema={"user_id": "string", "query": "string", "limit": "integer"},
        )

    async def execute(self, request: ToolRequest) -> ToolResponse:
        results = await self._knowledge.search(
            user_id=str(request.arguments.get("user_id") or ""),
            query=str(request.arguments.get("query") or ""),
            limit=int(request.arguments.get("limit") or 5),
        )
        return ToolResponse(name=self.spec.name, output={"results": results})


class ResumeGenerationTool(ExecutableTool):
    @property
    def spec(self) -> ToolSpec:
        return ToolSpec(
            name="resume_generation",
            description="Generate a structured resume draft from highlights and skills",
            parameters_schema={
                "target_role": "string",
                "experience_highlights": "array",
                "skills": "array",
            },
        )

    async def execute(self, request: ToolRequest) -> ToolResponse:
        role = str(request.arguments.get("target_role") or request.arguments.get("goal") or "Role")
        highlights = list(request.arguments.get("experience_highlights") or [])
        skills = list(request.arguments.get("skills") or [])
        lines = [f"# {role}", "", "## Experience"]
        lines.extend(f"- {item}" for item in highlights or ["Impactful delivery across platforms"])
        lines.extend(["", "## Skills", ", ".join(skills) if skills else "Architecture, Leadership"])
        return ToolResponse(
            name=self.spec.name,
            output={"content": "\n".join(lines), "format": "markdown"},
        )


class CalculatorTool(ExecutableTool):
    _OPS: ClassVar[dict[type, Any]] = {
        ast.Add: operator.add,
        ast.Sub: operator.sub,
        ast.Mult: operator.mul,
        ast.Div: operator.truediv,
        ast.Pow: operator.pow,
        ast.USub: operator.neg,
    }

    @property
    def spec(self) -> ToolSpec:
        return ToolSpec(
            name="calculator",
            description="Evaluate a basic arithmetic expression",
            parameters_schema={"expression": "string"},
            metadata={"mcp_compatible": True},
        )

    async def execute(self, request: ToolRequest) -> ToolResponse:
        expression = str(request.arguments.get("expression") or "")
        try:
            value = self._eval(ast.parse(expression, mode="eval").body)
        except Exception as exc:
            raise ValidationFailedError(f"Invalid expression: {expression}") from exc
        return ToolResponse(name=self.spec.name, output={"result": value})

    def _eval(self, node: Any) -> float:
        if isinstance(node, ast.Constant) and isinstance(node.value, (int, float)):
            return float(node.value)
        if isinstance(node, ast.BinOp):
            op = self._OPS[type(node.op)]
            return float(op(self._eval(node.left), self._eval(node.right)))
        if isinstance(node, ast.UnaryOp):
            op = self._OPS[type(node.op)]
            return float(op(self._eval(node.operand)))
        raise ValidationFailedError("Unsupported expression")
