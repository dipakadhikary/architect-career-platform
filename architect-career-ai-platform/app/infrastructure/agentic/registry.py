"""In-memory capability registry."""

from __future__ import annotations

from typing import Any

from app.intelligence.agentic.models import CapabilityDescriptor, CapabilityKind
from app.intelligence.agentic.registry.ports import Capability, CapabilityRegistryPort
from app.shared.exceptions import NotFoundError, ValidationFailedError


class InMemoryCapabilityRegistry(CapabilityRegistryPort):
    def __init__(self) -> None:
        self._capabilities: dict[str, Capability] = {}

    def register(self, capability: Capability) -> None:
        name = capability.descriptor.name
        if not name:
            raise ValidationFailedError("Capability name is required")
        self._capabilities[name] = capability

    def get(self, name: str) -> Capability:
        capability = self._capabilities.get(name)
        if capability is None:
            raise NotFoundError(f"Capability not registered: {name}")
        return capability

    def list(self, kind: CapabilityKind | None = None) -> list[CapabilityDescriptor]:
        descriptors = [item.descriptor for item in self._capabilities.values()]
        if kind is None:
            return sorted(descriptors, key=lambda item: item.name)
        return sorted(
            [item for item in descriptors if item.kind == kind],
            key=lambda item: item.name,
        )

    async def invoke(self, name: str, payload: dict[str, Any]) -> dict[str, Any]:
        return await self.get(name).invoke(payload)
