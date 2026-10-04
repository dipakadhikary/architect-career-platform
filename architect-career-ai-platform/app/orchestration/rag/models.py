"""Retrieval models. These are not vector-database responses."""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(slots=True, frozen=True)
class RetrievedChunk:
    chunk_id: str
    content_id: str
    content: str
    score: float
    title: str
    section: str
    content_type: str
    source_url: str
    path: str
    topic_id: str | None
    owner_id: str
