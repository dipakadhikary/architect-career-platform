"""Protected indexing API. Browsers cannot submit arbitrary documents."""

from __future__ import annotations

from fastapi import APIRouter, Depends, Header
from pydantic import BaseModel, ConfigDict, Field

from app.api.assistant_auth import resolve_caller
from app.intelligence.indexing.models import ContentType, IndexDocument, IndexState
from app.orchestration.indexing.service import IndexingService
from app.shared.config.settings import AppSettings, get_settings
from app.shared.constants.headers import HeaderNames
from app.shared.context.request_context import get_request_context
from app.shared.di.container import container
from app.shared.exceptions import AuthenticationError, AuthorizationError, NotFoundError

router = APIRouter(tags=["Indexing"])


class IndexContentRequest(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    content_id: str = Field(alias="contentId", min_length=1)
    owner_id: str = Field(alias="ownerId", min_length=1)
    content_type: ContentType = Field(alias="contentType")
    title: str = Field(min_length=1)
    content: str = ""
    question: str = ""
    answer: str = ""
    content_version: int = Field(default=0, alias="contentVersion")
    topic_id: str | None = Field(default=None, alias="topicId")
    slug: str | None = None
    path: str | None = None
    source_url: str | None = Field(default=None, alias="sourceUrl")


class IndexContentResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    content_id: str = Field(alias="contentId")
    status: str
    chunk_count: int = Field(alias="chunkCount")
    content_version: int = Field(alias="contentVersion")
    embedding_model: str = Field(alias="embeddingModel")
    embedding_provider: str = Field(alias="embeddingProvider")
    index_version: int = Field(alias="indexVersion")
    retryable: bool = False
    error: str | None = None


class RebuildResponse(BaseModel):
    status: str
    index_version: int = Field(alias="indexVersion")
    message: str

    model_config = ConfigDict(populate_by_name=True)


def get_indexing_service() -> IndexingService:
    return container.indexing_service()


def get_index_owner(
    settings: AppSettings = Depends(get_settings),
    authorization: str | None = Header(default=None, alias=HeaderNames.AUTHORIZATION),
    api_key: str | None = Header(default=None, alias=HeaderNames.API_KEY),
    internal_service: str | None = Header(default=None, alias=HeaderNames.INTERNAL_SERVICE),
    user_id: str | None = Header(default=None, alias=HeaderNames.USER_ID),
) -> str | None:
    """JWT callers are limited to their subject. Service credentials trust the ACOS payload."""
    context = get_request_context()
    correlation_id = context.correlation_id if context is not None else ""
    if authorization:
        return resolve_caller(
            settings,
            authorization=authorization,
            api_key=None,
            internal_service_token=None,
            user_id_header=user_id,
            correlation_id=correlation_id,
        ).owner_id
    if internal_service and internal_service in settings.internal_service_token_set:
        return None
    if settings.auth_api_key_enabled and api_key and api_key in settings.api_key_set:
        return None
    raise AuthenticationError()


def _document(body: IndexContentRequest, owner_id: str | None) -> IndexDocument:
    if owner_id is not None and body.owner_id != owner_id:
        raise AuthorizationError("ownerId does not match the authenticated caller")
    return IndexDocument(
        content_id=body.content_id,
        owner_id=body.owner_id,
        content_type=body.content_type,
        title=body.title,
        content=body.content,
        question=body.question,
        answer=body.answer,
        content_version=body.content_version,
        topic_id=body.topic_id,
        slug=body.slug,
        path=body.path,
        source_url=body.source_url,
    )


def _response(state: IndexState) -> IndexContentResponse:
    return IndexContentResponse(
        content_id=state.content_id,
        status=state.status.value,
        chunk_count=state.chunk_count,
        content_version=state.content_version,
        embedding_model=state.embedding_model,
        embedding_provider=state.embedding_provider,
        index_version=state.index_version,
        retryable=state.retryable,
        error=state.error,
    )


@router.post("/api/v1/ai/index/content", response_model=IndexContentResponse)
async def index_content(
    body: IndexContentRequest,
    owner_id: str | None = Depends(get_index_owner),
    service: IndexingService = Depends(get_indexing_service),
) -> IndexContentResponse:
    state = await service.index(_document(body, owner_id))
    return _response(state)


@router.post(
    "/api/v1/ai/index/content/{content_id}/reindex",
    response_model=IndexContentResponse,
)
async def reindex_content(
    content_id: str,
    body: IndexContentRequest,
    owner_id: str | None = Depends(get_index_owner),
    service: IndexingService = Depends(get_indexing_service),
) -> IndexContentResponse:
    if body.content_id != content_id:
        raise AuthorizationError("contentId does not match the path")
    state = await service.index(_document(body, owner_id))
    return _response(state)


@router.delete("/api/v1/ai/index/content/{content_id}", status_code=200)
async def delete_content(
    content_id: str,
    owner_id: str | None = Depends(get_index_owner),
    service: IndexingService = Depends(get_indexing_service),
) -> IndexContentResponse:
    state = await service.delete(content_id, owner_id=owner_id)
    return _response(state)


@router.get("/api/v1/ai/index/content/{content_id}", response_model=IndexContentResponse)
async def content_status(
    content_id: str,
    owner_id: str | None = Depends(get_index_owner),
    service: IndexingService = Depends(get_indexing_service),
) -> IndexContentResponse:
    state = service.status(content_id, owner_id=owner_id)
    if state is None:
        raise NotFoundError("Content has not been indexed")
    return _response(state)


@router.post("/api/v1/ai/index/rebuild", response_model=RebuildResponse)
async def rebuild_index(
    settings: AppSettings = Depends(get_settings),
    owner_id: str | None = Depends(get_index_owner),
) -> RebuildResponse:
    """Accept a protected rebuild request. ACOS replays documents one at a time."""
    del owner_id
    return RebuildResponse(
        status="ACCEPTED",
        index_version=settings.index_version,
        message=(
            "Replay each ACOS note, concept, and question through "
            "POST /api/v1/ai/index/content. The index is not the content source."
        ),
    )
