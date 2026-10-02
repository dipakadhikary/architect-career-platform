"""Unit tests for MCP and A2A extension points."""

from __future__ import annotations

import pytest
from app.infrastructure.enterprise.a2a import InMemoryAgentRegistry, LocalAgentDelegation
from app.infrastructure.enterprise.mcp import (
    InMemoryMcpResourceRegistry,
    InMemoryMcpToolRegistry,
    InMemoryMcpTransport,
    StubMcpClient,
)
from app.intelligence.enterprise.models import (
    AgentDescriptor,
    McpResourceDescriptor,
    McpToolDescriptor,
)


@pytest.mark.asyncio
async def test_mcp_tool_registry_and_call() -> None:
    tools = InMemoryMcpToolRegistry()
    resources = InMemoryMcpResourceRegistry()
    transport = InMemoryMcpTransport()
    tools.register(
        McpToolDescriptor(name="echo", description="echo", input_schema={"type": "object"})
    )
    resources.register(McpResourceDescriptor(uri="res://demo", name="demo", mime_type="text/plain"))
    client = StubMcpClient(transport, tools, resources)
    await transport.connect()
    assert len(await client.list_tools()) == 1
    assert len(await client.list_resources()) == 1
    result = await client.call_tool("echo", {"text": "hi"})
    assert result["method"] == "tools/call"


@pytest.mark.asyncio
async def test_a2a_delegation() -> None:
    registry = InMemoryAgentRegistry()
    registry.register(
        AgentDescriptor(
            agent_id="agent-a",
            name="Agent A",
            capabilities=["summarize"],
            metadata={"endpoint": "local://agent-a"},
        )
    )
    delegation = LocalAgentDelegation(registry)
    discovered = registry.discover(capability="summarize")
    assert discovered[0].agent_id == "agent-a"
    result = await delegation.delegate(
        from_agent="orchestrator",
        to_agent="agent-a",
        task="summarize",
        shared_context={"tenant": "t1"},
    )
    assert result["to_agent"] == "agent-a"
    assert result["task"] == "summarize"
    assert result["accepted"] is True
