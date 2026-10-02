"""File-based prompt templates with versioning."""

from __future__ import annotations

import json
from pathlib import Path
from string import Template

from app.intelligence.knowledge.models import PromptBundle
from app.intelligence.knowledge.prompt.ports import PromptBuilderPort, PromptRenderRequest
from app.shared.exceptions import NotFoundError


class FilePromptBuilder(PromptBuilderPort):
    def __init__(self, root: str | Path) -> None:
        self._root = Path(root)

    async def render(self, request: PromptRenderRequest) -> PromptBundle:
        version = request.version or _latest_version(self._root / request.name)
        folder = self._root / request.name / version
        if not folder.exists():
            raise NotFoundError(f"Prompt template not found: {request.name}@{version}")

        system = (folder / "system.txt").read_text(encoding="utf-8")
        user = (folder / "user.txt").read_text(encoding="utf-8")
        metadata: dict[str, object] = {}
        meta_file = folder / "metadata.json"
        if meta_file.exists():
            metadata = json.loads(meta_file.read_text(encoding="utf-8"))

        few_shot = ""
        if request.few_shot_examples:
            blocks = [
                f"Example\nInput: {item.get('input', '')}\nOutput: {item.get('output', '')}"
                for item in request.few_shot_examples
            ]
            few_shot = "\n\n".join(blocks)

        variables = {"few_shot": few_shot, **request.variables}
        return PromptBundle(
            name=request.name,
            version=version,
            system=Template(system).safe_substitute(variables),
            user=Template(user).safe_substitute(variables),
            metadata=metadata,
        )


def _latest_version(path: Path) -> str:
    if not path.exists():
        raise NotFoundError(f"Prompt family not found: {path.name}")
    versions = sorted(item.name for item in path.iterdir() if item.is_dir())
    if not versions:
        raise NotFoundError(f"No prompt versions for: {path.name}")
    return versions[-1]
