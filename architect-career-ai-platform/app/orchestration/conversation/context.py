"""Bound conversation history and build a retrieval query without replacing the original."""

from __future__ import annotations

from app.orchestration.conversation.store import MessageRow
from app.orchestration.rag.lexical import query_tokens

_FOLLOW_UP = frozenset(
    {"it", "its", "they", "their", "them", "this", "that", "those", "these", "he", "she"}
)
NEW_CONVERSATION_TITLE = "New conversation"


def select_history(
    messages: list[MessageRow],
    *,
    max_messages: int,
    max_characters: int,
) -> list[MessageRow]:
    """Keep the newest completed turns that fit the budget, in chronological order."""
    eligible = [
        message
        for message in messages
        if message.status == "COMPLETED" and message.role in {"USER", "ASSISTANT"}
    ]
    if not eligible:
        return []
    chosen: list[MessageRow] = []
    used = 0
    for message in reversed(eligible):
        if chosen and (len(chosen) >= max_messages or used + len(message.content) > max_characters):
            break
        chosen.append(message)
        used += len(message.content)
    chosen.reverse()
    return chosen


def build_retrieval_query(
    current: str,
    previous_user: str | None,
    *,
    max_characters: int,
) -> tuple[str, str]:
    """Return the original question and the text used for retrieval.

    A short follow-up keeps the previous user question so "its" can resolve.
    The original question is never replaced.
    """
    original = current.strip()
    if not previous_user or not _needs_prior_turn(original):
        return original, original[:max_characters]
    prior = previous_user.strip()
    combined = f"{prior}\n{original}"
    if len(combined) <= max_characters:
        return original, combined
    room = max_characters - len(original) - 1
    if room <= 0:
        return original, original[:max_characters]
    return original, f"{prior[-room:]}\n{original}"


def title_from_message(content: str, *, limit: int) -> str:
    compact = " ".join(content.split())
    if len(compact) <= limit:
        return compact
    return compact[: limit - 1].rstrip() + "…"


def _needs_prior_turn(question: str) -> bool:
    tokens = {token.casefold() for token in query_tokens(question)}
    return bool(tokens & _FOLLOW_UP) or len(tokens) <= 4
