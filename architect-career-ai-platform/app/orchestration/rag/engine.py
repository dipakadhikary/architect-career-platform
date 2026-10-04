"""Grounded answering. This engine does not merge keyword search or rerank."""

from __future__ import annotations

import time

from app.infrastructure.observability.otel import get_tracer
from app.intelligence.assistant.models import CallerContext, ChatRequest, ChatResponse, ChatRole
from app.intelligence.assistant.provider import LlmProvider
from app.orchestration.rag.context import ContextBuilder
from app.orchestration.rag.prompt import NO_CONTEXT_ANSWER, RagPromptBuilder
from app.orchestration.rag.query import prepare_query
from app.orchestration.rag.retriever import VectorRetriever
from app.orchestration.rag.sources import sources_from_chunks
from app.shared.config.settings import AppSettings
from app.shared.logging.setup import get_logger
from app.shared.observability.metrics import PlatformMetrics

logger = get_logger(__name__)
_tracer = get_tracer("acos.ai.rag")


class RagEngine:
    def __init__(
        self,
        *,
        settings: AppSettings,
        retriever: VectorRetriever,
        context_builder: ContextBuilder,
        prompt_builder: RagPromptBuilder,
        metrics: PlatformMetrics,
    ) -> None:
        self._settings = settings
        self._retriever = retriever
        self._context_builder = context_builder
        self._prompt_builder = prompt_builder
        self._metrics = metrics

    async def answer(
        self,
        request: ChatRequest,
        caller: CallerContext,
        provider: LlmProvider,
    ) -> ChatResponse:
        question = _latest_user_text(request)
        prepared = prepare_query(
            question, max_characters=self._settings.ai_max_message_characters
        )
        with _tracer.start_as_current_span("ai.request") as span:
            span.set_attribute("rag.prompt_version", self._settings.rag_prompt_version)
            span.set_attribute("owner_id", caller.owner_id)
            chunks = await self._retriever.retrieve(prepared, owner_id=caller.owner_id)
            with _tracer.start_as_current_span("context.building"):
                context, selected = self._context_builder.build(chunks)
            self._metrics.rag_context_characters.observe(len(context))
            if not selected:
                self._metrics.rag_requests.labels("no_context").inc()
                self._metrics.rag_sources.observe(0)
                logger.info(
                    "rag.no_context",
                    owner_id=caller.owner_id,
                    prompt_version=self._settings.rag_prompt_version,
                )
                return ChatResponse(
                    answer=NO_CONTEXT_ANSWER,
                    model=self._settings.resolve_chat_model(),
                    provider=provider.provider_name,
                    correlation_id=caller.correlation_id,
                    grounded=False,
                    sources=[],
                )
            messages = self._prompt_builder.build(
                history=request.messages,
                question=prepared,
                context=context,
            )
            started = time.perf_counter()
            with _tracer.start_as_current_span("llm.call"):
                completion = await provider.chat(
                    messages,
                    model=self._settings.resolve_chat_model(),
                    temperature=self._settings.ai_temperature,
                    max_tokens=self._settings.ai_max_tokens,
                )
            self._metrics.rag_llm_latency.observe(time.perf_counter() - started)
            citations = sources_from_chunks(
                selected, limit=self._settings.rag_max_sources
            )
            self._metrics.rag_requests.labels("grounded").inc()
            self._metrics.rag_sources.observe(len(citations))
            logger.info(
                "rag.completed",
                owner_id=caller.owner_id,
                prompt_version=self._settings.rag_prompt_version,
                sources=[item.content_id for item in citations],
                context_characters=len(context),
            )
            return ChatResponse(
                answer=completion.answer,
                model=completion.model,
                provider=completion.provider,
                correlation_id=caller.correlation_id,
                grounded=True,
                sources=citations,
            )


def _latest_user_text(request: ChatRequest) -> str:
    for message in reversed(request.messages):
        if message.role == ChatRole.USER:
            return message.content
    return ""
