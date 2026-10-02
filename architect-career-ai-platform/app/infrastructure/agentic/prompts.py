"""File-backed prompt registry with validation and versioning."""

from __future__ import annotations

import json
from pathlib import Path
from string import Template
from typing import Any

from app.intelligence.agentic.models import (
    CapabilityDescriptor,
    CapabilityKind,
    PromptTemplateSpec,
)
from app.intelligence.agentic.prompts.ports import PromptRegistryPort
from app.shared.exceptions import NotFoundError, ValidationFailedError


class FilePromptRegistry(PromptRegistryPort):
    def __init__(self, root: str | Path) -> None:
        self._root = Path(root)
        self._overrides: dict[tuple[str, str], PromptTemplateSpec] = {}

    @property
    def descriptor(self) -> CapabilityDescriptor:
        return CapabilityDescriptor(
            name="prompt_builder",
            kind=CapabilityKind.PROMPT_BUILDER,
            description="Versioned prompt templates with variables and metadata",
        )

    def register(self, spec: PromptTemplateSpec) -> None:
        errors = self.validate(spec)
        if errors:
            raise ValidationFailedError("; ".join(errors))
        self._overrides[(spec.name, spec.version)] = spec

    def get(self, name: str, version: str | None = None) -> PromptTemplateSpec:
        resolved = version or self._latest(name)
        override = self._overrides.get((name, resolved))
        if override is not None:
            return override
        folder = self._root / name / resolved
        if not folder.exists():
            raise NotFoundError(f"Prompt not found: {name}@{resolved}")
        metadata: dict[str, Any] = {}
        meta_file = folder / "metadata.json"
        if meta_file.exists():
            metadata = json.loads(meta_file.read_text(encoding="utf-8"))
        few_shot: list[dict[str, str]] = []
        few_file = folder / "few_shot.json"
        if few_file.exists():
            few_shot = json.loads(few_file.read_text(encoding="utf-8"))
        return PromptTemplateSpec(
            name=name,
            version=resolved,
            system=(folder / "system.txt").read_text(encoding="utf-8"),
            user=(folder / "user.txt").read_text(encoding="utf-8"),
            metadata=metadata,
            few_shot=few_shot,
        )

    def list_versions(self, name: str) -> list[str]:
        folder = self._root / name
        versions = set()
        if folder.exists():
            versions.update(item.name for item in folder.iterdir() if item.is_dir())
        versions.update(version for (n, version) in self._overrides if n == name)
        return sorted(versions)

    async def render(
        self,
        name: str,
        variables: dict[str, Any],
        *,
        version: str | None = None,
        few_shot: list[dict[str, str]] | None = None,
    ) -> PromptTemplateSpec:
        spec = self.get(name, version)
        shots = few_shot if few_shot is not None else spec.few_shot
        few_shot_block = ""
        if shots:
            few_shot_block = "\n\n".join(
                f"Example\nInput: {item.get('input', '')}\nOutput: {item.get('output', '')}"
                for item in shots
            )
        values = {"few_shot": few_shot_block, **{k: str(v) for k, v in variables.items()}}
        return PromptTemplateSpec(
            name=spec.name,
            version=spec.version,
            system=Template(spec.system).safe_substitute(values),
            user=Template(spec.user).safe_substitute(values),
            metadata=dict(spec.metadata),
            few_shot=list(shots),
        )

    def validate(self, spec: PromptTemplateSpec) -> list[str]:
        errors: list[str] = []
        if not spec.name:
            errors.append("name is required")
        if not spec.version:
            errors.append("version is required")
        if not spec.system.strip():
            errors.append("system prompt is required")
        if not spec.user.strip():
            errors.append("user prompt is required")
        return errors

    async def invoke(self, payload: dict[str, Any]) -> dict[str, Any]:
        rendered = await self.render(
            str(payload.get("name") or "chat"),
            dict(payload.get("variables") or {}),
            version=payload.get("version"),
            few_shot=payload.get("few_shot"),
        )
        return {
            "name": rendered.name,
            "version": rendered.version,
            "system": rendered.system,
            "user": rendered.user,
            "metadata": rendered.metadata,
        }

    def _latest(self, name: str) -> str:
        versions = self.list_versions(name)
        if not versions:
            raise NotFoundError(f"No prompt versions for: {name}")
        return versions[-1]
