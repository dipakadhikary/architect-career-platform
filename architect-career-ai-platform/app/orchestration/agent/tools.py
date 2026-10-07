"""Read-only tools. Each one calls an existing ACOS capability."""

from __future__ import annotations

from typing import Any

from pydantic import BaseModel, ConfigDict, Field

from app.intelligence.assistant.errors import RagUnavailableError
from app.orchestration.agent.models import RiskLevel, ToolPermission
from app.orchestration.agent.registry import ToolContext, ToolRegistry, ToolSpec
from app.orchestration.conversation.store import ConversationStore
from app.orchestration.rag.models import RetrievedChunk
from app.shared.exceptions import ValidationFailedError

_UNSAFE = ("\x00", "../", "..\\", "<script", "javascript:")


class _SearchInput(BaseModel):
    model_config = ConfigDict(extra="forbid")

    query: str = Field(min_length=1, max_length=500)
    top_k: int = Field(default=5, ge=1, le=5)


class _RetrieveInput(BaseModel):
    model_config = ConfigDict(extra="forbid")

    content_id: str = Field(min_length=1, max_length=80, pattern=r"^[A-Za-z0-9._:-]+$")


class _ConversationInput(BaseModel):
    model_config = ConfigDict(extra="forbid")

    query: str = Field(min_length=1, max_length=200)


class _ContentInput(BaseModel):
    model_config = ConfigDict(extra="forbid")

    query: str = Field(min_length=1, max_length=500)
    content_type: str = Field(default="knowledge", pattern=r"^(knowledge|note|tutorial)$")
    top_k: int = Field(default=5, ge=1, le=5)


def _safe_text(value: str) -> str:
    lowered = value.lower()
    if any(token in lowered for token in _UNSAFE):
        raise ValidationFailedError("Tool input contains an unsafe sequence")
    return value.strip()


def _public_hit(chunk: RetrievedChunk, *, limit: int) -> dict[str, Any]:
    return {
        "contentId": chunk.content_id,
        "title": chunk.title,
        "contentType": chunk.content_type,
        "section": chunk.section,
        "path": chunk.path,
        "url": chunk.source_url,
        "chunkId": chunk.chunk_id,
        "score": chunk.score,
        "snippet": chunk.content[:limit],
    }


class KnowledgeSearchTool:
    spec = ToolSpec(
        name="search_knowledge",
        version="v1",
        description="Search the caller's ACOS knowledge with hybrid retrieval.",
        risk=RiskLevel.LOW,
        permission=ToolPermission.READ_KNOWLEDGE,
        input_model=_SearchInput,
    )

    def __init__(self, retriever: Any, *, snippet_characters: int) -> None:
        self._retriever = retriever
        self._snippet_characters = snippet_characters

    async def execute(self, context: ToolContext, arguments: dict[str, Any]) -> dict[str, Any]:
        query = _safe_text(str(arguments["query"]))
        top_k = int(arguments["top_k"])
        chunks = await _retrieve(self._retriever, query, context.owner_id, top_k)
        return {"results": [_public_hit(chunk, limit=self._snippet_characters) for chunk in chunks]}


class KnowledgeRetrieveTool:
    spec = ToolSpec(
        name="retrieve_knowledge",
        version="v1",
        description="Retrieve one owned knowledge item by its content id.",
        risk=RiskLevel.LOW,
        permission=ToolPermission.READ_KNOWLEDGE,
        input_model=_RetrieveInput,
    )

    def __init__(self, retriever: Any, *, snippet_characters: int) -> None:
        self._retriever = retriever
        self._snippet_characters = snippet_characters

    async def execute(self, context: ToolContext, arguments: dict[str, Any]) -> dict[str, Any]:
        content_id = _safe_text(str(arguments["content_id"]))
        chunks = await _retrieve(self._retriever, content_id, context.owner_id, 5)
        matched = [chunk for chunk in chunks if chunk.content_id == content_id]
        return {
            "results": [_public_hit(chunk, limit=self._snippet_characters) for chunk in matched]
        }


class ConversationSearchTool:
    spec = ToolSpec(
        name="search_conversations",
        version="v1",
        description="Search the caller's own AI conversations.",
        risk=RiskLevel.LOW,
        permission=ToolPermission.READ_CONVERSATION,
        input_model=_ConversationInput,
    )

    def __init__(self, store: ConversationStore) -> None:
        self._store = store

    async def execute(self, context: ToolContext, arguments: dict[str, Any]) -> dict[str, Any]:
        query = _safe_text(str(arguments["query"]))
        rows = self._store.search_messages(context.owner_id, query, limit=5)
        return {
            "results": [
                {"conversationId": conversation_id, "title": title, "snippet": snippet}
                for conversation_id, title, snippet in rows
            ]
        }


class ContentSearchTool:
    spec = ToolSpec(
        name="search_content",
        version="v1",
        description="Search owned notes and tutorials through the same retrieval stack.",
        risk=RiskLevel.LOW,
        permission=ToolPermission.READ_CONTENT,
        input_model=_ContentInput,
    )

    def __init__(self, retriever: Any, *, snippet_characters: int) -> None:
        self._retriever = retriever
        self._snippet_characters = snippet_characters

    async def execute(self, context: ToolContext, arguments: dict[str, Any]) -> dict[str, Any]:
        query = _safe_text(str(arguments["query"]))
        content_type = str(arguments["content_type"])
        top_k = int(arguments["top_k"])
        chunks = await _retrieve(self._retriever, query, context.owner_id, top_k)
        if content_type != "knowledge":
            chunks = [chunk for chunk in chunks if chunk.content_type == content_type]
        return {"results": [_public_hit(chunk, limit=self._snippet_characters) for chunk in chunks]}


def read_only_registry(
    settings: Any,
    retriever: Any,
    conversations: ConversationStore,
) -> ToolRegistry:
    """The only tools the production agent may propose."""
    registry = ToolRegistry()
    snippet = int(settings.agent_max_result_characters)
    registry.register(KnowledgeSearchTool(retriever, snippet_characters=snippet))
    registry.register(KnowledgeRetrieveTool(retriever, snippet_characters=snippet))
    registry.register(ConversationSearchTool(conversations))
    registry.register(ContentSearchTool(retriever, snippet_characters=snippet))
    return registry


async def _retrieve(
    retriever: Any,
    query: str,
    owner_id: str,
    top_k: int,
) -> list[RetrievedChunk]:
    if retriever is None:
        return []
    try:
        chunks = await retriever.retrieve(query, owner_id=owner_id, top_k=top_k)
    except RagUnavailableError:
        return []
    return [chunk for chunk in chunks if chunk.owner_id == owner_id][:top_k]
