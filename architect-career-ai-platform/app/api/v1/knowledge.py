"""Contract-aligned Knowledge AI endpoints."""

from __future__ import annotations

from acos_ai_contracts.models.knowledge_index_request import KnowledgeIndexRequest
from acos_ai_contracts.models.knowledge_index_response import KnowledgeIndexResponse
from acos_ai_contracts.models.knowledge_search_request import KnowledgeSearchRequest
from acos_ai_contracts.models.knowledge_search_response import KnowledgeSearchResponse
from acos_ai_contracts.models.knowledge_summarize_request import KnowledgeSummarizeRequest
from acos_ai_contracts.models.knowledge_summarize_response import KnowledgeSummarizeResponse
from acos_ai_contracts.models.search_knowledge200_response_hits_inner import (
    SearchKnowledge200ResponseHitsInner,
)
from fastapi import APIRouter, Depends

from app.api.dependencies import get_optional_principal, get_pipelined_knowledge_service
from app.orchestration.enterprise.facades import PipelinedKnowledgeFacade
from app.shared.security.authentication import AuthenticatedPrincipal

router = APIRouter(tags=["Knowledge"])


@router.post(
    "/api/v1/ai/knowledge/index",
    response_model=KnowledgeIndexResponse,
    response_model_by_alias=True,
    summary="Index knowledge content",
)
async def index_knowledge(
    body: KnowledgeIndexRequest,
    service: PipelinedKnowledgeFacade = Depends(get_pipelined_knowledge_service),
    principal: AuthenticatedPrincipal | None = Depends(get_optional_principal),
) -> KnowledgeIndexResponse:
    result = await service.index_document(
        principal=principal,
        user_id=body.user_id,
        note_id=body.note_id,
        title=body.title,
        content=body.content,
        tags=list(body.tags or []),
    )
    return KnowledgeIndexResponse(
        documentId=str(result["document_id"]),
        noteId=str(result["note_id"]),
        status=str(result["status"]),
        indexedAt=result["indexed_at"],
    )


@router.post(
    "/api/v1/ai/knowledge/search",
    response_model=KnowledgeSearchResponse,
    response_model_by_alias=True,
    summary="Search knowledge content",
)
async def search_knowledge(
    body: KnowledgeSearchRequest,
    service: PipelinedKnowledgeFacade = Depends(get_pipelined_knowledge_service),
    principal: AuthenticatedPrincipal | None = Depends(get_optional_principal),
) -> KnowledgeSearchResponse:
    hits = await service.search(
        principal=principal,
        user_id=body.user_id,
        query=body.query,
        limit=body.limit or 10,
    )
    return KnowledgeSearchResponse(
        hits=[
            SearchKnowledge200ResponseHitsInner(
                noteId=str(item["note_id"]),
                title=str(item["title"]),
                snippet=str(item["snippet"]),
                score=float(item["score"]),
            )
            for item in hits
        ]
    )


@router.post(
    "/api/v1/ai/knowledge/summarize",
    response_model=KnowledgeSummarizeResponse,
    response_model_by_alias=True,
    summary="Summarize knowledge content",
)
async def summarize_knowledge(
    body: KnowledgeSummarizeRequest,
    service: PipelinedKnowledgeFacade = Depends(get_pipelined_knowledge_service),
    principal: AuthenticatedPrincipal | None = Depends(get_optional_principal),
) -> KnowledgeSummarizeResponse:
    result = await service.summarize(
        principal=principal,
        user_id=body.user_id,
        content=body.content,
        note_id=body.note_id,
        max_length=body.max_length,
    )
    return KnowledgeSummarizeResponse(
        noteId=result.get("note_id"),
        summary=str(result["summary"]),
        keyPoints=result.get("key_points"),
    )
