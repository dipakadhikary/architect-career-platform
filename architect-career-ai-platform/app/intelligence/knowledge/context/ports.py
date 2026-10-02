"""Context builder ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from dataclasses import dataclass

from app.intelligence.knowledge.models import BuiltContext, RetrievalHit


@dataclass(slots=True, frozen=True)
class ContextOptions:
    max_tokens: int = 3000
    inject_metadata: bool = True
    deduplicate: bool = True
    include_citations: bool = True


class ContextBuilderPort(ABC):
    @abstractmethod
    async def build(
        self,
        hits: list[RetrievalHit],
        options: ContextOptions | None = None,
    ) -> BuiltContext:
        raise NotImplementedError
