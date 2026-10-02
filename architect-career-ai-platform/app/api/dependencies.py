"""API dependency providers."""

from __future__ import annotations

from fastapi import Header, Request

from app.infrastructure.cache.redis_adapter import RedisAdapter
from app.infrastructure.vector.qdrant_adapter import QdrantAdapter
from app.orchestration.enterprise.facades import PipelinedAgenticFacade, PipelinedKnowledgeFacade
from app.orchestration.knowledge.service import KnowledgeService
from app.shared.constants.headers import HeaderNames
from app.shared.di.container import container
from app.shared.security.authentication import AuthenticatedPrincipal, AuthenticationService


def get_redis_adapter() -> RedisAdapter:
    return container.redis_adapter()


def get_qdrant_adapter() -> QdrantAdapter:
    return container.qdrant_adapter()


def get_authentication_service() -> AuthenticationService:
    return container.authentication_service()


def get_knowledge_service() -> KnowledgeService:
    return container.knowledge_service()


def get_pipelined_knowledge_service() -> PipelinedKnowledgeFacade:
    return container.pipelined_knowledge_service()


def get_agentic_service() -> PipelinedAgenticFacade:
    return container.pipelined_agentic_service()


def get_optional_principal(
    request: Request,
    authorization: str | None = Header(default=None, alias=HeaderNames.AUTHORIZATION),
    api_key: str | None = Header(default=None, alias=HeaderNames.API_KEY),
    internal_service: str | None = Header(default=None, alias=HeaderNames.INTERNAL_SERVICE),
) -> AuthenticatedPrincipal | None:
    service = container.authentication_service()
    return service.authenticate(
        authorization=authorization,
        api_key=api_key,
        internal_service_token=internal_service,
    )
