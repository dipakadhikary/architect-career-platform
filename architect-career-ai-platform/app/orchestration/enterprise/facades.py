"""Enterprise facades wrapping existing services through the AI pipeline."""

from __future__ import annotations

from typing import Any

from app.intelligence.enterprise.models import PipelineRequest
from app.intelligence.enterprise.pipeline.ports import AiExecutionPipelinePort
from app.orchestration.agentic.service import AgenticOrchestrationService
from app.orchestration.knowledge.service import KnowledgeService
from app.shared.security.authentication import AuthenticatedPrincipal

_INTERNAL_PAYLOAD_KEYS = frozenset({"_sanitized_input", "_prompt"})


def _public_payload(payload: dict[str, Any]) -> dict[str, Any]:
    return {key: value for key, value in payload.items() if key not in _INTERNAL_PAYLOAD_KEYS}


class PipelinedAgenticFacade:
    def __init__(
        self,
        inner: AgenticOrchestrationService,
        pipeline: AiExecutionPipelinePort,
    ) -> None:
        self._inner = inner
        self._pipeline = pipeline

    async def chat_completion(
        self,
        *,
        user_id: str,
        message: str,
        conversation_id: str | None = None,
        history: list[dict[str, str]] | None = None,
        principal: AuthenticatedPrincipal | None = None,
    ) -> dict[str, Any]:
        async def _handler(request: PipelineRequest) -> dict[str, Any]:
            payload = request.payload
            return await self._inner.chat_completion(
                user_id=user_id,
                message=str(payload.get("_sanitized_input") or payload.get("message") or message),
                conversation_id=conversation_id,
                history=list(payload.get("history") or history or []),
            )

        result = await self._pipeline.run(
            PipelineRequest(
                workflow="chat_completion",
                capability="conversation_manager",
                payload={
                    "user_id": user_id,
                    "message": message,
                    "conversation_id": conversation_id,
                    "history": history or [],
                    "prompt_variables": {
                        "question": message,
                        "context": "",
                        "history": "",
                    },
                },
                user_id=user_id,
                tenant_id=user_id,
                principal_subject=principal.subject if principal else None,
                prompt_name="chat",
                input_text=message,
            ),
            handler=_handler,
        )
        output = dict(result.output)
        output.setdefault("model", result.model or output.get("model"))
        return output

    async def run_workflow(
        self,
        name: str,
        payload: dict[str, Any],
        *,
        principal: AuthenticatedPrincipal | None = None,
        input_text: str = "",
        prompt_name: str | None = None,
    ) -> dict[str, Any]:
        async def _handler(request: PipelineRequest) -> dict[str, Any]:
            return await self._inner.run_workflow(name, _public_payload(request.payload))

        result = await self._pipeline.run(
            PipelineRequest(
                workflow=name,
                capability=name,
                payload=dict(payload),
                user_id=str(payload.get("user_id") or "") or None,
                tenant_id=str(payload.get("user_id") or "") or None,
                principal_subject=principal.subject if principal else None,
                prompt_name=prompt_name,
                input_text=input_text
                or str(
                    payload.get("message")
                    or payload.get("content")
                    or payload.get("transcript")
                    or payload.get("topic")
                    or payload.get("target_role")
                    or name
                ),
            ),
            handler=_handler,
        )
        return result.output


class PipelinedKnowledgeFacade:
    def __init__(
        self,
        inner: KnowledgeService,
        pipeline: AiExecutionPipelinePort,
    ) -> None:
        self._inner = inner
        self._pipeline = pipeline

    async def index_document(
        self,
        *,
        principal: AuthenticatedPrincipal | None = None,
        **kwargs: Any,
    ) -> dict[str, Any]:
        content = str(kwargs.get("content") or "")

        async def _handler(request: PipelineRequest) -> dict[str, Any]:
            return await self._inner.index_document(**_public_payload(request.payload))

        result = await self._pipeline.run(
            PipelineRequest(
                workflow="knowledge_index",
                capability="retriever",
                payload=dict(kwargs),
                user_id=kwargs.get("user_id"),
                tenant_id=kwargs.get("user_id"),
                principal_subject=principal.subject if principal else None,
                input_text=content,
            ),
            handler=_handler,
        )
        return result.output

    async def search(
        self,
        *,
        principal: AuthenticatedPrincipal | None = None,
        **kwargs: Any,
    ) -> list[dict[str, Any]]:
        query = str(kwargs.get("query") or "")

        async def _handler(request: PipelineRequest) -> dict[str, Any]:
            hits = await self._inner.search(**_public_payload(request.payload))
            return {"hits": hits}

        result = await self._pipeline.run(
            PipelineRequest(
                workflow="knowledge_search",
                capability="retriever",
                payload=dict(kwargs),
                user_id=kwargs.get("user_id"),
                tenant_id=kwargs.get("user_id"),
                principal_subject=principal.subject if principal else None,
                input_text=query,
            ),
            handler=_handler,
        )
        hits = result.output.get("hits")
        return list(hits) if isinstance(hits, list) else []

    async def summarize(
        self,
        *,
        principal: AuthenticatedPrincipal | None = None,
        **kwargs: Any,
    ) -> dict[str, Any]:
        content = str(kwargs.get("content") or "")

        async def _handler(request: PipelineRequest) -> dict[str, Any]:
            return await self._inner.summarize(**_public_payload(request.payload))

        result = await self._pipeline.run(
            PipelineRequest(
                workflow="knowledge_summarize",
                capability="summarization",
                payload=dict(kwargs),
                user_id=kwargs.get("user_id"),
                tenant_id=kwargs.get("user_id"),
                principal_subject=principal.subject if principal else None,
                input_text=content,
            ),
            handler=_handler,
        )
        return result.output
