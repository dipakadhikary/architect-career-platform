"""AI platform execution pipeline port."""

from __future__ import annotations

from abc import ABC, abstractmethod
from collections.abc import Awaitable, Callable
from typing import Any

from app.intelligence.enterprise.models import PipelineRequest, PipelineResult

Handler = Callable[[PipelineRequest], Awaitable[dict[str, Any]]]


class AiExecutionPipelinePort(ABC):
    """Mandatory middleware pipeline for every AI workflow execution."""

    @abstractmethod
    async def run(self, request: PipelineRequest, handler: Handler) -> PipelineResult:
        raise NotImplementedError
