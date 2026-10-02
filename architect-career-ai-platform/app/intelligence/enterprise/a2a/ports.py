"""Agent-to-Agent communication extension-point ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any

from app.intelligence.enterprise.models import AgentDescriptor


class AgentRegistryPort(ABC):
    @abstractmethod
    def register(self, agent: AgentDescriptor) -> None:
        raise NotImplementedError

    @abstractmethod
    def discover(self, *, capability: str | None = None) -> list[AgentDescriptor]:
        raise NotImplementedError

    @abstractmethod
    def get(self, agent_id: str) -> AgentDescriptor | None:
        raise NotImplementedError


class AgentDelegationPort(ABC):
    @abstractmethod
    async def delegate(
        self,
        *,
        from_agent: str,
        to_agent: str,
        task: str,
        shared_context: dict[str, Any],
    ) -> dict[str, Any]:
        raise NotImplementedError
