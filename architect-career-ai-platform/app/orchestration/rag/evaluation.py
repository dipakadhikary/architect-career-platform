"""Small retrieval metrics. These do not claim quality without a labeled set."""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(slots=True, frozen=True)
class RetrievalCase:
    question: str
    expected_content_ids: list[str]


def recall_at_k(expected: list[str], retrieved: list[str], k: int) -> float:
    relevant = set(expected)
    if not relevant:
        return 1.0
    return len(relevant.intersection(retrieved[:k])) / len(relevant)


def precision_at_k(expected: list[str], retrieved: list[str], k: int) -> float:
    if k <= 0:
        return 0.0
    relevant = set(expected)
    return len(relevant.intersection(retrieved[:k])) / k


def mean_reciprocal_rank(expected: list[str], retrieved: list[str]) -> float:
    relevant = set(expected)
    for index, content_id in enumerate(retrieved, start=1):
        if content_id in relevant:
            return 1.0 / index
    return 0.0
