"""Capability retriever wrapping Enterprise RAG with extension modes."""

from __future__ import annotations

from typing import Any

from app.intelligence.agentic.models import (
    CapabilityDescriptor,
    CapabilityKind,
    RetrievalCapabilityHit,
    RetrievalCapabilityQuery,
)
from app.intelligence.agentic.retriever.ports import CapabilityRetrieverPort
from app.intelligence.knowledge.models import RetrievalMode
from app.intelligence.knowledge.retrieval.ports import (
    KnowledgeRetrievalQuery,
    KnowledgeRetrieverPort,
)
from app.shared.exceptions import ValidationFailedError


class MultiSourceCapabilityRetriever(CapabilityRetrieverPort):
    def __init__(
        self,
        knowledge_retriever: KnowledgeRetrieverPort,
        conversation_memory: Any | None = None,
    ) -> None:
        self._knowledge = knowledge_retriever
        self._conversation_memory = conversation_memory

    @property
    def descriptor(self) -> CapabilityDescriptor:
        return CapabilityDescriptor(
            name="retriever",
            kind=CapabilityKind.RETRIEVER,
            description="Knowledge, conversation, and future web/sql/graph retrieval",
            metadata={
                "modes": ["knowledge", "conversation", "web", "sql", "graph"],
            },
        )

    async def retrieve(self, query: RetrievalCapabilityQuery) -> list[RetrievalCapabilityHit]:
        mode = query.mode.lower()
        if mode == "knowledge":
            return await self._knowledge_mode(query)
        if mode == "conversation":
            return await self._conversation_mode(query)
        if mode in {"web", "sql", "graph"}:
            # Extension points — intentionally empty until adapters are registered.
            return []
        raise ValidationFailedError(f"Unsupported retrieval mode: {query.mode}")

    async def invoke(self, payload: dict[str, Any]) -> dict[str, Any]:
        hits = await self.retrieve(
            RetrievalCapabilityQuery(
                text=str(payload.get("text") or payload.get("goal") or ""),
                user_id=payload.get("user_id"),
                mode=str(payload.get("mode") or "knowledge"),
                top_k=int(payload.get("top_k") or 5),
                filters=dict(payload.get("filters") or {}),
            )
        )
        return {
            "hits": [
                {
                    "id": hit.id,
                    "text": hit.text,
                    "score": hit.score,
                    "source": hit.source,
                    "metadata": hit.metadata,
                }
                for hit in hits
            ]
        }

    async def _knowledge_mode(
        self, query: RetrievalCapabilityQuery
    ) -> list[RetrievalCapabilityHit]:
        filters = dict(query.filters)
        if query.user_id:
            filters.setdefault("user_id", query.user_id)
        records = await self._knowledge.retrieve(
            KnowledgeRetrievalQuery(
                text=query.text,
                top_k=query.top_k,
                mode=RetrievalMode.DENSE,
                filters=filters,
            )
        )
        return [
            RetrievalCapabilityHit(
                id=item.id,
                text=item.text,
                score=item.score,
                source="knowledge",
                metadata=item.metadata,
            )
            for item in records
        ]

    async def _conversation_mode(
        self, query: RetrievalCapabilityQuery
    ) -> list[RetrievalCapabilityHit]:
        if self._conversation_memory is None:
            return []
        conversation_id = str(query.filters.get("conversation_id") or "")
        if not conversation_id:
            return []
        state = await self._conversation_memory.load(conversation_id)
        if state is None:
            return []
        needle = query.text.lower()
        hits: list[RetrievalCapabilityHit] = []
        for index, turn in enumerate(state.turns):
            if needle and needle not in turn.content.lower():
                continue
            hits.append(
                RetrievalCapabilityHit(
                    id=f"{conversation_id}:{index}",
                    text=turn.content,
                    score=1.0,
                    source="conversation",
                    metadata={"role": turn.role},
                )
            )
        return hits[: query.top_k]
