"""Dynamic tool registry."""

from __future__ import annotations

from typing import Any

from app.intelligence.agentic.models import CapabilityDescriptor, CapabilityKind, ToolSpec
from app.intelligence.agentic.tools.ports import ExecutableTool, ToolRegistryPort
from app.intelligence.tools.ports import ToolRequest, ToolResponse
from app.shared.exceptions import NotFoundError


class DefaultToolRegistry(ToolRegistryPort):
    def __init__(self) -> None:
        self._tools: dict[str, ExecutableTool] = {}

    @property
    def descriptor(self) -> CapabilityDescriptor:
        return CapabilityDescriptor(
            name="tool_executor",
            kind=CapabilityKind.TOOL_EXECUTOR,
            description="Dynamic tool registry and executor",
        )

    def register(self, tool: ExecutableTool) -> None:
        self._tools[tool.spec.name] = tool

    def get(self, name: str) -> ExecutableTool:
        tool = self._tools.get(name)
        if tool is None:
            raise NotFoundError(f"Tool not registered: {name}")
        return tool

    def list(self) -> list[ToolSpec]:
        return [tool.spec for tool in self._tools.values()]

    async def execute(self, request: ToolRequest) -> ToolResponse:
        response = await self.get(request.name).execute(request)
        return response

    async def invoke(self, payload: dict[str, Any]) -> dict[str, Any]:
        name = str(payload.get("name") or payload.get("action") or "")
        response = await self.execute(
            ToolRequest(name=name, arguments=dict(payload.get("arguments") or payload))
        )
        return {"name": response.name, "output": response.output}
