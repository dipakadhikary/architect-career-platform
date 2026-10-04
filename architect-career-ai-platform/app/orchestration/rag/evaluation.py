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


@dataclass(slots=True, frozen=True)
class HybridEvalCase:
    """One labeled question. These cases are fixtures, not a production quality claim."""

    case_id: str
    question: str
    kind: str
    expected_content_ids: list[str]
    expected_urls: list[str]
    expected_grounded: bool


@dataclass(slots=True, frozen=True)
class StrategyObservation:
    retrieved_content_ids: list[str]
    source_urls: list[str]
    grounded: bool


def citation_precision(expected: list[str], actual: list[str]) -> float:
    if not actual:
        return 1.0 if not expected else 0.0
    return len(set(expected).intersection(actual)) / len(set(actual))


def score_observation(
    case: HybridEvalCase, observation: StrategyObservation, k: int
) -> dict[str, float | bool]:
    retrieved = observation.retrieved_content_ids
    return {
        "recall": recall_at_k(case.expected_content_ids, retrieved, k),
        "precision": precision_at_k(case.expected_content_ids, retrieved, k),
        "mrr": mean_reciprocal_rank(case.expected_content_ids, retrieved),
        "citation_precision": citation_precision(case.expected_urls, observation.source_urls),
        "grounded_match": observation.grounded == case.expected_grounded,
    }
