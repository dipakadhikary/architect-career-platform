"""Capability registry abstraction."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any, Protocol

from app.intelligence.agentic.models import CapabilityDescriptor, CapabilityKind


class Capability(Protocol):
    @property
    def descriptor(self) -> CapabilityDescriptor: ...

    async def invoke(self, payload: dict[str, Any]) -> dict[str, Any]: ...


class CapabilityRegistryPort(ABC):
    @abstractmethod
    def register(self, capability: Capability) -> None:
        raise NotImplementedError

    @abstractmethod
    def get(self, name: str) -> Capability:
        raise NotImplementedError

    @abstractmethod
    def list(self, kind: CapabilityKind | None = None) -> list[CapabilityDescriptor]:
        raise NotImplementedError

    @abstractmethod
    async def invoke(self, name: str, payload: dict[str, Any]) -> dict[str, Any]:
        raise NotImplementedError
