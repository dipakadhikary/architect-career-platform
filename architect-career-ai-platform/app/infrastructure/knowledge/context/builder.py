"""Context construction with token budget and citations."""

from __future__ import annotations

from app.intelligence.knowledge.context.ports import ContextBuilderPort, ContextOptions
from app.intelligence.knowledge.models import BuiltContext, RetrievalHit


class DefaultContextBuilder(ContextBuilderPort):
    async def build(
        self,
        hits: list[RetrievalHit],
        options: ContextOptions | None = None,
    ) -> BuiltContext:
        opts = options or ContextOptions()
        selected: list[RetrievalHit] = []
        seen_text: set[str] = set()
        used_tokens = 0
        parts: list[str] = []
        citations: list[dict[str, object]] = []

        for hit in hits:
            normalized = " ".join(hit.text.split())
            if opts.deduplicate and normalized in seen_text:
                continue
            estimate = max(len(normalized.split()), 1)
            if used_tokens + estimate > opts.max_tokens and selected:
                break
            seen_text.add(normalized)
            selected.append(hit)
            used_tokens += estimate
            meta = ""
            if opts.inject_metadata and hit.metadata:
                title = hit.metadata.get("title")
                if title:
                    meta = f"Title: {title}\n"
            parts.append(f"[{len(selected)}] {meta}{hit.text}")
            if opts.include_citations:
                citations.append(
                    {
                        "id": hit.id,
                        "documentId": hit.document_id,
                        "score": hit.score,
                        "title": hit.metadata.get("title"),
                    }
                )

        return BuiltContext(
            text="\n\n".join(parts),
            citations=citations,
            token_estimate=used_tokens,
            documents=selected,
        )
