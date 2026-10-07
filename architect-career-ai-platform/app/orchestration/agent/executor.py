"""Runs one tool behind a timeout. Failures stay inside a ToolResult."""

from __future__ import annotations

import asyncio
from typing import Any

from app.orchestration.agent.models import ToolResult
from app.orchestration.agent.registry import AgentTool, ToolContext


class ToolExecutor:
    def __init__(self, timeout_seconds: float) -> None:
        self._timeout_seconds = timeout_seconds

    async def execute(
        self,
        tool: AgentTool,
        context: ToolContext,
        arguments: dict[str, Any],
    ) -> ToolResult:
        if not tool.spec.idempotent or not tool.spec.read_only:
            return ToolResult(
                tool=tool.spec.name,
                success=False,
                result={},
                error="NOT_IDEMPOTENT",
            )
        first = await self._once(tool, context, arguments)
        if first.success or first.error == "TOOL_TIMEOUT":
            return first
        return await self._once(tool, context, arguments)

    async def _once(
        self,
        tool: AgentTool,
        context: ToolContext,
        arguments: dict[str, Any],
    ) -> ToolResult:
        try:
            payload = await asyncio.wait_for(
                tool.execute(context, arguments),
                timeout=self._timeout_seconds,
            )
        except TimeoutError:
            return ToolResult(tool.spec.name, False, {}, "TOOL_TIMEOUT")
        except Exception:
            return ToolResult(tool.spec.name, False, {}, "TOOL_FAILED")
        if not isinstance(payload, dict):
            return ToolResult(tool.spec.name, False, {}, "TOOL_FAILED")
        return ToolResult(tool.spec.name, True, payload, "")
