"""Agent-to-Agent extension-point adapters (no network)."""

from __future__ import annotations

from typing import Any

from app.intelligence.enterprise.a2a.ports import AgentDelegationPort, AgentRegistryPort
from app.intelligence.enterprise.models import AgentDescriptor
from app.shared.exceptions import NotFoundError


class InMemoryAgentRegistry(AgentRegistryPort):
    def __init__(self) -> None:
        self._agents: dict[str, AgentDescriptor] = {}

    def register(self, agent: AgentDescriptor) -> None:
        self._agents[agent.agent_id] = agent

    def discover(self, *, capability: str | None = None) -> list[AgentDescriptor]:
        agents = list(self._agents.values())
        if capability is None:
            return agents
        return [agent for agent in agents if capability in agent.capabilities]

    def get(self, agent_id: str) -> AgentDescriptor | None:
        return self._agents.get(agent_id)


class LocalAgentDelegation(AgentDelegationPort):
    def __init__(self, registry: AgentRegistryPort) -> None:
        self._registry = registry

    async def delegate(
        self,
        *,
        from_agent: str,
        to_agent: str,
        task: str,
        shared_context: dict[str, Any],
    ) -> dict[str, Any]:
        target = self._registry.get(to_agent)
        if target is None:
            raise NotFoundError(f"Agent not registered: {to_agent}")
        return {
            "from_agent": from_agent,
            "to_agent": to_agent,
            "task": task,
            "accepted": True,
            "shared_context": shared_context,
            "capabilities": target.capabilities,
        }
