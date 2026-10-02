"""MCP (Model Context Protocol) extension-point ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any

from app.intelligence.enterprise.models import McpResourceDescriptor, McpToolDescriptor


class McpTransportPort(ABC):
    @abstractmethod
    async def connect(self) -> None:
        raise NotImplementedError

    @abstractmethod
    async def close(self) -> None:
        raise NotImplementedError

    @abstractmethod
    async def call(self, method: str, params: dict[str, Any]) -> dict[str, Any]:
        raise NotImplementedError


class McpClientPort(ABC):
    @abstractmethod
    async def list_tools(self) -> list[McpToolDescriptor]:
        raise NotImplementedError

    @abstractmethod
    async def list_resources(self) -> list[McpResourceDescriptor]:
        raise NotImplementedError

    @abstractmethod
    async def call_tool(self, name: str, arguments: dict[str, Any]) -> dict[str, Any]:
        raise NotImplementedError


class McpToolRegistryPort(ABC):
    @abstractmethod
    def register(self, tool: McpToolDescriptor) -> None:
        raise NotImplementedError

    @abstractmethod
    def list(self) -> list[McpToolDescriptor]:
        raise NotImplementedError


class McpResourceRegistryPort(ABC):
    @abstractmethod
    def register(self, resource: McpResourceDescriptor) -> None:
        raise NotImplementedError

    @abstractmethod
    def list(self) -> list[McpResourceDescriptor]:
        raise NotImplementedError
