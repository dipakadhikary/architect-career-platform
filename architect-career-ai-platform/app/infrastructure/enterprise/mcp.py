"""MCP extension-point adapters (no external MCP servers)."""

from __future__ import annotations

from typing import Any

from app.intelligence.enterprise.mcp.ports import (
    McpClientPort,
    McpResourceRegistryPort,
    McpToolRegistryPort,
    McpTransportPort,
)
from app.intelligence.enterprise.models import McpResourceDescriptor, McpToolDescriptor
from app.shared.exceptions import NotFoundError, ValidationFailedError


class InMemoryMcpTransport(McpTransportPort):
    def __init__(self) -> None:
        self._connected = False

    async def connect(self) -> None:
        self._connected = True

    async def close(self) -> None:
        self._connected = False

    async def call(self, method: str, params: dict[str, Any]) -> dict[str, Any]:
        if not self._connected:
            raise ValidationFailedError("MCP transport is not connected")
        return {"method": method, "params": params, "result": None}


class InMemoryMcpToolRegistry(McpToolRegistryPort):
    def __init__(self) -> None:
        self._tools: dict[str, McpToolDescriptor] = {}

    def register(self, tool: McpToolDescriptor) -> None:
        self._tools[tool.name] = tool

    def list(self) -> list[McpToolDescriptor]:
        return list(self._tools.values())


class InMemoryMcpResourceRegistry(McpResourceRegistryPort):
    def __init__(self) -> None:
        self._resources: dict[str, McpResourceDescriptor] = {}

    def register(self, resource: McpResourceDescriptor) -> None:
        self._resources[resource.uri] = resource

    def list(self) -> list[McpResourceDescriptor]:
        return list(self._resources.values())


class StubMcpClient(McpClientPort):
    def __init__(
        self,
        transport: McpTransportPort,
        tools: McpToolRegistryPort,
        resources: McpResourceRegistryPort,
    ) -> None:
        self._transport = transport
        self._tools = tools
        self._resources = resources

    async def list_tools(self) -> list[McpToolDescriptor]:
        return self._tools.list()

    async def list_resources(self) -> list[McpResourceDescriptor]:
        return self._resources.list()

    async def call_tool(self, name: str, arguments: dict[str, Any]) -> dict[str, Any]:
        names = {tool.name for tool in self._tools.list()}
        if name not in names:
            raise NotFoundError(f"MCP tool not registered: {name}")
        return await self._transport.call("tools/call", {"name": name, "arguments": arguments})
