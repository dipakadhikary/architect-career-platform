"""Workflow engine composing capabilities (no direct infrastructure calls)."""

from __future__ import annotations

import time
from typing import Any

from app.intelligence.agentic.models import CapabilityDescriptor, CapabilityKind, WorkflowStatus
from app.intelligence.agentic.workflows.ports import WorkflowDefinition, WorkflowEnginePort
from app.orchestration.workflows import WorkflowRequest, WorkflowResult
from app.shared.exceptions import NotFoundError
from app.shared.observability.metrics import PlatformMetrics


class DefaultWorkflowEngine(WorkflowEnginePort):
    def __init__(self, metrics: PlatformMetrics) -> None:
        self._workflows: dict[str, WorkflowDefinition] = {}
        self._metrics = metrics

    @property
    def descriptor(self) -> CapabilityDescriptor:
        return CapabilityDescriptor(
            name="workflow_engine",
            kind=CapabilityKind.WORKFLOW_ENGINE,
            description="Registers and executes reusable capability workflows",
        )

    def register(self, workflow: WorkflowDefinition) -> None:
        self._workflows[workflow.name] = workflow

    def list_names(self) -> list[str]:
        return sorted(self._workflows)

    async def execute(self, request: WorkflowRequest) -> WorkflowResult:
        workflow = self._workflows.get(request.name)
        if workflow is None:
            raise NotFoundError(f"Workflow not registered: {request.name}")
        started = time.perf_counter()
        status = WorkflowStatus.FAILED.value
        output: dict[str, Any] = {}
        try:
            output = await workflow.run(dict(request.payload))
            status = WorkflowStatus.SUCCEEDED.value
            return WorkflowResult(name=request.name, status=status, output=output)
        except Exception:
            status = WorkflowStatus.FAILED.value
            raise
        finally:
            elapsed = time.perf_counter() - started
            self._metrics.agentic_workflow_latency.labels(request.name).observe(elapsed)
            self._metrics.agentic_workflow_runs.labels(request.name, status).inc()

    async def invoke(self, payload: dict[str, Any]) -> dict[str, Any]:
        result = await self.execute(
            WorkflowRequest(
                name=str(payload.get("name") or ""),
                payload=dict(payload.get("payload") or {}),
            )
        )
        return {"name": result.name, "status": result.status, "output": result.output}
