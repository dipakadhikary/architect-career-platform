"""Phase 1 assistant HTTP API. Controllers do not call provider SDKs."""

from __future__ import annotations

from fastapi import APIRouter, Depends, Header
from pydantic import BaseModel, Field

from app.api.assistant_auth import resolve_caller
from app.infrastructure.llm.chat_factory import build_chat_provider
from app.intelligence.assistant.models import CallerContext, ChatRequest, ChatResponse
from app.orchestration.assistant.service import AssistantService
from app.orchestration.rag.context import ContextBuilder
from app.orchestration.rag.engine import RagEngine
from app.orchestration.rag.prompt import RagPromptBuilder
from app.orchestration.rag.retriever import VectorRetriever
from app.shared.config.settings import AppSettings, get_settings
from app.shared.constants.headers import HeaderNames
from app.shared.context.request_context import get_request_context
from app.shared.di.container import container
from app.shared.observability.metrics import get_metrics

router = APIRouter(tags=["Assistant"])


class HealthResponse(BaseModel):
    status: str = Field(examples=["UP"])


class ReadinessResponse(BaseModel):
    status: str = Field(examples=["READY"])
    checks: dict[str, str]


def get_assistant_service(settings: AppSettings = Depends(get_settings)) -> AssistantService:
    provider = build_chat_provider(settings)
    rag = None
    if settings.rag_enabled:
        metrics = get_metrics()
        rag = RagEngine(
            settings=settings,
            retriever=VectorRetriever(
                settings=settings,
                embeddings=container.embedding_port(),
                vector_store=container.vector_store(),
                metrics=metrics,
            ),
            context_builder=ContextBuilder(settings),
            prompt_builder=RagPromptBuilder(),
            metrics=metrics,
        )
    return AssistantService(settings=settings, provider=provider, rag=rag)


def get_caller(
    settings: AppSettings = Depends(get_settings),
    authorization: str | None = Header(default=None, alias=HeaderNames.AUTHORIZATION),
    api_key: str | None = Header(default=None, alias=HeaderNames.API_KEY),
    internal_service: str | None = Header(default=None, alias=HeaderNames.INTERNAL_SERVICE),
    user_id: str | None = Header(default=None, alias=HeaderNames.USER_ID),
) -> CallerContext:
    context = get_request_context()
    correlation_id = context.correlation_id if context is not None else ""
    return resolve_caller(
        settings,
        authorization=authorization,
        api_key=api_key,
        internal_service_token=internal_service,
        user_id_header=user_id,
        correlation_id=correlation_id,
    )


@router.get("/health", response_model=HealthResponse, summary="Process liveness")
async def health() -> HealthResponse:
    """Fast process check. Does not call a model or a dependency."""
    return HealthResponse(status="UP")


@router.get("/ready", response_model=ReadinessResponse, summary="Configuration readiness")
async def ready(settings: AppSettings = Depends(get_settings)) -> ReadinessResponse:
    """Report whether chat can be served. Does not call a language model."""
    checks = {"process": "UP", "configuration": _configuration_check(settings)}
    status = "READY" if all(value == "UP" for value in checks.values()) else "NOT_READY"
    return ReadinessResponse(status=status, checks=checks)


@router.post(
    "/api/v1/ai/chat",
    response_model=ChatResponse,
    summary="Create an assistant reply",
    responses={
        400: {"description": "Validation failed"},
        401: {"description": "Authentication required"},
        403: {"description": "Caller does not match user_id"},
        502: {"description": "Provider rejected the call"},
        503: {"description": "Assistant disabled or provider unavailable"},
        504: {"description": "Provider timed out"},
    },
)
async def chat(
    request: ChatRequest,
    caller: CallerContext = Depends(get_caller),
    service: AssistantService = Depends(get_assistant_service),
) -> ChatResponse:
    """Answer a message list. Retrieval runs only when RAG is enabled."""
    return await service.chat(request, caller)


def _configuration_check(settings: AppSettings) -> str:
    if not settings.ai_enabled:
        return "UP"
    if settings.llm_provider == "openai":
        secret = settings.openai_api_key
        if secret is not None and secret.get_secret_value().strip():
            return "UP"
        return "DOWN"
    if settings.llm_provider == "ollama":
        return "UP" if settings.ollama_base_url.strip() else "DOWN"
    return "DOWN"
