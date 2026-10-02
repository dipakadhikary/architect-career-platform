"""Request-scoped correlation context."""

from __future__ import annotations

from contextvars import ContextVar
from dataclasses import dataclass, field
from uuid import uuid4


@dataclass(slots=True, frozen=True)
class RequestContext:
    correlation_id: str
    request_id: str
    trace_id: str
    user_id: str | None = None
    metadata: dict[str, str] = field(default_factory=dict)

    @classmethod
    def create(
        cls,
        *,
        correlation_id: str | None = None,
        request_id: str | None = None,
        trace_id: str | None = None,
        user_id: str | None = None,
    ) -> RequestContext:
        generated = uuid4().hex
        return cls(
            correlation_id=correlation_id or generated,
            request_id=request_id or uuid4().hex,
            trace_id=trace_id or generated,
            user_id=user_id,
        )


request_context_var: ContextVar[RequestContext | None] = ContextVar("request_context", default=None)


def get_request_context() -> RequestContext | None:
    return request_context_var.get()


def bind_request_context(context: RequestContext) -> None:
    request_context_var.set(context)
