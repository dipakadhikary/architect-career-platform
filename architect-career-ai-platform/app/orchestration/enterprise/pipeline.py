"""AI platform execution middleware pipeline."""

from __future__ import annotations

import time
import uuid
from typing import Any

from app.intelligence.agentic.router.ports import ModelRouterPort, RoutingRequest
from app.intelligence.embeddings.ports import EmbeddingPort, EmbeddingRequest
from app.intelligence.enterprise.audit.ports import AuditLogPort
from app.intelligence.enterprise.cache.ports import SemanticCachePort
from app.intelligence.enterprise.cost.ports import CostTrackerPort
from app.intelligence.enterprise.evaluation.ports import EnterpriseEvaluationPort
from app.intelligence.enterprise.governance.ports import PromptGovernancePort
from app.intelligence.enterprise.guardrails.ports import GuardrailsPort
from app.intelligence.enterprise.models import (
    AuditEvent,
    CostRecord,
    GuardrailVerdict,
    PipelineRequest,
    PipelineResult,
)
from app.intelligence.enterprise.pipeline.ports import AiExecutionPipelinePort, Handler
from app.intelligence.enterprise.policy.ports import PolicyEnginePort
from app.intelligence.enterprise.security.ports import DataMaskerPort, PromptSanitizerPort
from app.shared.config.settings import AppSettings
from app.shared.context.request_context import get_request_context
from app.shared.exceptions import (
    GuardrailViolationError,
    PolicyViolationError,
    UpstreamTimeoutError,
)
from app.shared.logging.setup import get_logger
from app.shared.observability.metrics import PlatformMetrics
from app.shared.resilience.patterns import (
    Bulkhead,
    CircuitBreaker,
    with_circuit_breaker,
    with_fallback,
    with_retry,
    with_timeout,
)

logger = get_logger(__name__)

_TEXT_PAYLOAD_KEYS = ("message", "content", "query", "transcript", "topic")
_NON_CACHEABLE_WORKFLOWS = frozenset(
    {"knowledge_index", "knowledge_search", "knowledge_summarize"}
)
_NO_SOFT_FALLBACK_WORKFLOWS = frozenset(
    {"knowledge_index", "knowledge_search", "knowledge_summarize"}
)


class AiExecutionPipeline(AiExecutionPipelinePort):
    """
    Mandatory pipeline for every AI workflow:

    Authz → Correlation → Guardrails → Prompt → Routing → Policy →
    Execution → Evaluation → Observability → Transform → Audit
    """

    def __init__(
        self,
        *,
        settings: AppSettings,
        guardrails: GuardrailsPort,
        policy: PolicyEnginePort,
        governance: PromptGovernancePort,
        router: ModelRouterPort,
        cache: SemanticCachePort,
        cost_tracker: CostTrackerPort,
        evaluator: EnterpriseEvaluationPort,
        audit: AuditLogPort,
        sanitizer: PromptSanitizerPort,
        masker: DataMaskerPort,
        metrics: PlatformMetrics,
        embeddings: EmbeddingPort,
    ) -> None:
        self._settings = settings
        self._guardrails = guardrails
        self._policy = policy
        self._governance = governance
        self._router = router
        self._cache = cache
        self._cost = cost_tracker
        self._evaluator = evaluator
        self._audit = audit
        self._sanitizer = sanitizer
        self._masker = masker
        self._metrics = metrics
        self._embeddings = embeddings
        self._breaker = CircuitBreaker(
            failure_threshold=settings.resilience_circuit_failure_threshold,
            recovery_timeout_seconds=settings.resilience_circuit_recovery_seconds,
        )
        self._bulkhead = Bulkhead(limit=settings.resilience_bulkhead_limit)

    async def run(self, request: PipelineRequest, handler: Handler) -> PipelineResult:
        workflow_id = str(uuid.uuid4())
        started = time.perf_counter()
        retries = 0
        prompt_version = request.prompt_version
        model: str | None = None
        provider: str | None = None
        evaluation: dict[str, Any] = {}
        cost: dict[str, Any] = {}
        guardrail_meta: dict[str, Any] = {}
        failure_reason: str | None = None
        output: dict[str, Any] = {}

        context = get_request_context()
        correlation_id = context.correlation_id if context else None
        trace_id = context.trace_id if context else None

        try:
            tenant_decision = self._policy.evaluate(
                policy_type="tenant",
                context={
                    "tenant_id": request.tenant_id,
                    "user_id": request.user_id,
                    "principal_subject": request.principal_subject,
                },
            )
            if not tenant_decision.allowed:
                raise PolicyViolationError(tenant_decision.reason)

            capability_decision = self._policy.evaluate(
                policy_type="capability",
                context={"capability": request.capability},
            )
            if not capability_decision.allowed:
                raise PolicyViolationError(capability_decision.reason)

            sanitized = self._sanitizer.sanitize(request.input_text or "")
            inbound = await self._guardrails.validate_input(
                sanitized, capability=request.capability
            )
            if inbound.verdict == GuardrailVerdict.BLOCK:
                raise GuardrailViolationError(
                    inbound.findings[0].message if inbound.findings else "blocked"
                )
            guardrail_meta["input"] = {
                "verdict": inbound.verdict.value,
                "findings": [f.rule for f in inbound.findings],
                "redactions": inbound.redactions,
            }
            _apply_sanitized_input(request, inbound.text)

            if request.prompt_name:
                prompt = await self._governance.resolve(
                    request.prompt_name,
                    dict(request.payload.get("prompt_variables") or {}),
                    version=request.prompt_version,
                    require_approved=self._settings.prompt_require_approved,
                )
                prompt_version = prompt.version
                request.payload["_prompt"] = {
                    "system": prompt.system,
                    "user": prompt.user,
                    "version": prompt.version,
                }

            cache_hit = False
            cache_allowed = (
                self._settings.semantic_cache_enabled
                and request.workflow not in _NON_CACHEABLE_WORKFLOWS
            )
            if cache_allowed:
                embedding = await self._embed(inbound.text)
                namespace = f"{request.workflow}:{request.capability}"
                similar = await self._cache.get_similar(
                    embedding,
                    threshold=self._settings.semantic_similarity_threshold,
                    namespace=namespace,
                )
                if similar:
                    output = dict(similar)
                    cache_hit = True
                    self._metrics.enterprise_cache_hits.labels("semantic").inc()
                else:
                    exact_key = f"{namespace}:{hash(inbound.text)}"
                    cached = await self._cache.get_response(exact_key)
                    if cached:
                        output = dict(cached)
                        cache_hit = True
                        self._metrics.enterprise_cache_hits.labels("response").inc()

            if not cache_hit:
                routed = await self._router.route(
                    RoutingRequest(
                        capability=request.capability,
                        prompt_tokens_estimate=max(len(inbound.text.split()), 1),
                        prefer_low_cost=True,
                        metadata={"routing_policy": self._settings.routing_policy},
                    )
                )
                model = routed.model
                provider = routed.provider
                model_decision = self._policy.evaluate(
                    policy_type="model",
                    context={"provider": provider, "model": model},
                )
                if not model_decision.allowed:
                    fallback = self._settings.routing_fallback_provider
                    if fallback:
                        provider = fallback
                        model = "fallback"
                    else:
                        raise PolicyViolationError(model_decision.reason)

                estimated_prompt_tokens = max(len(inbound.text.split()), 1)
                max_tokens = int(request.payload.get("max_tokens") or estimated_prompt_tokens * 2)
                pre_cost = _estimate_cost(
                    provider or "extractive",
                    estimated_prompt_tokens,
                    max_tokens,
                    self._settings,
                )
                execution_decision = self._policy.evaluate(
                    policy_type="execution",
                    context={
                        "estimated_cost_usd": pre_cost,
                        "total_tokens": estimated_prompt_tokens + max_tokens,
                        "prompt_name": request.prompt_name,
                        "tool": request.payload.get("tool"),
                    },
                )
                if not execution_decision.allowed:
                    raise PolicyViolationError(execution_decision.reason)

                async def _execute() -> dict[str, Any]:
                    return await handler(request)

                async def _protected() -> dict[str, Any]:
                    async def _once() -> dict[str, Any]:
                        return await with_circuit_breaker(
                            self._breaker,
                            lambda: self._bulkhead.run(_execute),
                        )

                    try:
                        return await with_timeout(_once, self._settings.resilience_timeout_seconds)
                    except TimeoutError as exc:
                        raise UpstreamTimeoutError() from exc

                async def _fallback(_exc: BaseException) -> dict[str, Any]:
                    return {
                        "degraded": True,
                        "message": "Graceful degradation response",
                        "failure_reason": str(_exc),
                    }

                allow_soft_fallback = (
                    self._settings.resilience_fallback_enabled
                    and request.workflow not in _NO_SOFT_FALLBACK_WORKFLOWS
                )
                if allow_soft_fallback:
                    result, retries = await with_retry(
                        lambda: with_fallback(_protected, _fallback),
                        attempts=self._settings.resilience_retry_attempts,
                    )
                else:
                    result, retries = await with_retry(
                        _protected,
                        attempts=self._settings.resilience_retry_attempts,
                    )
                output = result
                if cache_allowed:
                    exact_key = f"{request.workflow}:{request.capability}:{hash(inbound.text)}"
                    await self._cache.set_response(
                        exact_key,
                        output,
                        ttl_seconds=self._settings.semantic_cache_ttl_seconds,
                    )
                    await self._cache.put_similar(
                        await self._embed(inbound.text),
                        output,
                        namespace=f"{request.workflow}:{request.capability}",
                        ttl_seconds=self._settings.semantic_cache_ttl_seconds,
                    )

            answer_text = str(
                output.get("message")
                or output.get("summary")
                or output.get("content")
                or output.get("answer")
                or ""
            )
            if answer_text.strip():
                outbound = await self._guardrails.validate_output(
                    answer_text, capability=request.capability
                )
                if outbound.verdict == GuardrailVerdict.BLOCK:
                    raise GuardrailViolationError(
                        outbound.findings[0].message if outbound.findings else "blocked"
                    )
                if outbound.verdict == GuardrailVerdict.REDACT:
                    for key in ("message", "summary", "content", "answer"):
                        if key in output:
                            output[key] = outbound.text
                guardrail_meta["output"] = {
                    "verdict": outbound.verdict.value,
                    "findings": [f.rule for f in outbound.findings],
                    "redactions": outbound.redactions,
                }
            else:
                guardrail_meta["output"] = {
                    "verdict": GuardrailVerdict.ALLOW.value,
                    "findings": [],
                    "redactions": [],
                }

            for key in ("message", "summary", "content", "answer"):
                if key in output and isinstance(output[key], str):
                    output[key] = self._masker.mask(str(output[key]))

            latency_ms = (time.perf_counter() - started) * 1000
            prompt_tokens = int(output.get("prompt_tokens") or len(inbound.text.split()))
            completion_tokens = int(
                output.get("completion_tokens") or max(len(answer_text.split()), 1)
            )
            estimated_cost = _estimate_cost(
                provider or "extractive",
                prompt_tokens,
                completion_tokens,
                self._settings,
            )
            cost_record = CostRecord(
                provider=provider or "extractive",
                model=model or "unknown",
                capability=request.capability,
                prompt_tokens=prompt_tokens,
                completion_tokens=completion_tokens,
                embedding_tokens=int(output.get("embedding_tokens") or 0),
                estimated_cost_usd=estimated_cost,
                metadata={"tenant_id": request.tenant_id, "workflow_id": workflow_id},
            )
            await self._cost.record(cost_record)
            cost = {
                "prompt_tokens": prompt_tokens,
                "completion_tokens": completion_tokens,
                "estimated_cost_usd": estimated_cost,
            }

            eval_record = await self._evaluator.evaluate_and_persist(
                workflow_id=workflow_id,
                workflow=request.workflow,
                query=inbound.text,
                answer=answer_text,
                context=str(request.payload.get("context") or ""),
                latency_ms=latency_ms,
                prompt_tokens=prompt_tokens,
                completion_tokens=completion_tokens,
                estimated_cost_usd=estimated_cost,
                prompt_version=prompt_version or "n/a",
                model=model or "unknown",
                provider=provider or "unknown",
            )
            evaluation = {
                "faithfulness": eval_record.faithfulness,
                "groundedness": eval_record.groundedness,
                "context_precision": eval_record.context_precision,
                "context_recall": eval_record.context_recall,
                "answer_relevance": eval_record.answer_relevance,
                "retriever_quality": eval_record.retriever_quality,
            }

            self._metrics.enterprise_pipeline_runs.labels(request.workflow, "SUCCEEDED").inc()
            self._metrics.enterprise_pipeline_latency.labels(request.workflow).observe(
                latency_ms / 1000.0
            )
            self._metrics.enterprise_retries.labels(request.workflow).inc(retries)

            logger.info(
                "enterprise.pipeline",
                workflow=request.workflow,
                workflow_id=workflow_id,
                capability=request.capability,
                correlation_id=correlation_id,
                trace_id=trace_id,
                model=model,
                provider=provider,
                prompt_version=prompt_version,
                retries=retries,
                latency_ms=latency_ms,
                estimated_cost_usd=estimated_cost,
                evaluation_score=eval_record.answer_relevance,
                cache_hit=cache_hit,
            )

            await self._audit.write(
                AuditEvent(
                    action="ai.execute",
                    workflow=request.workflow,
                    workflow_id=workflow_id,
                    principal=request.principal_subject,
                    tenant_id=request.tenant_id,
                    success=True,
                    detail={
                        "capability": request.capability,
                        "model": model,
                        "provider": provider,
                        "prompt_version": prompt_version,
                        "retries": retries,
                        "cache_hit": cache_hit,
                    },
                )
            )

            return PipelineResult(
                output=output,
                workflow_id=workflow_id,
                model=model,
                provider=provider,
                prompt_version=prompt_version,
                evaluation=evaluation,
                cost=cost,
                guardrail=guardrail_meta,
                retries=retries,
                latency_ms=latency_ms,
            )
        except Exception as exc:
            failure_reason = str(exc)
            self._metrics.enterprise_pipeline_runs.labels(request.workflow, "FAILED").inc()
            await self._audit.write(
                AuditEvent(
                    action="ai.execute",
                    workflow=request.workflow,
                    workflow_id=workflow_id,
                    principal=request.principal_subject,
                    tenant_id=request.tenant_id,
                    success=False,
                    detail={"failure_reason": failure_reason},
                )
            )
            raise
        finally:
            _ = failure_reason

    async def _embed(self, text: str) -> list[float]:
        response = await self._embeddings.embed(EmbeddingRequest(texts=[text]))
        return list(response.vectors[0]) if response.vectors else []


def _apply_sanitized_input(request: PipelineRequest, text: str) -> None:
    request.payload["_sanitized_input"] = text
    for key in _TEXT_PAYLOAD_KEYS:
        if key in request.payload and isinstance(request.payload[key], str):
            request.payload[key] = text
    variables = request.payload.get("prompt_variables")
    if isinstance(variables, dict):
        for key in ("question", "topic", "content"):
            if key in variables and isinstance(variables[key], str) and variables[key]:
                variables[key] = text


def _estimate_cost(
    provider: str, prompt_tokens: int, completion_tokens: int, settings: AppSettings
) -> float:
    rates = {
        "openai": settings.cost_openai_per_1k_tokens,
        "azure_openai": settings.cost_azure_per_1k_tokens,
        "ollama": settings.cost_ollama_per_1k_tokens,
    }
    rate = rates.get(provider, 0.0)
    return round(((prompt_tokens + completion_tokens) / 1000.0) * rate, 6)
