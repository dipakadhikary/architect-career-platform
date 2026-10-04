"""Normalize a question without damaging technical terms."""

from __future__ import annotations

import re

from app.shared.exceptions import ValidationFailedError

_SPACES = re.compile(r"[ \t]+")
_BLANK_LINES = re.compile(r"\n{3,}")


def prepare_query(text: str, *, max_characters: int) -> str:
    """Trim and collapse whitespace. Case, acronyms, and class names stay as written."""
    normalized = _BLANK_LINES.sub("\n\n", _SPACES.sub(" ", text.strip()))
    if not normalized:
        raise ValidationFailedError("Question is empty")
    if len(normalized) > max_characters:
        raise ValidationFailedError("Question exceeds the configured size limit")
    return normalized
