"""Normalize Markdown for the AI index. The ACOS source document is not modified."""

from __future__ import annotations

import re

_HTML_TAG = re.compile(r"<[^>\n]+>")


def normalize_markdown(source: str) -> str:
    """Return index text with light cleanup outside fenced code blocks."""
    if not source or not source.strip():
        return ""
    pieces: list[str] = []
    for is_code, block in _split_fences(source):
        if is_code:
            pieces.append(block.strip("\n"))
        else:
            cleaned = _normalize_prose(block)
            if cleaned:
                pieces.append(cleaned)
    return "\n\n".join(pieces).strip()


def _split_fences(source: str) -> list[tuple[bool, str]]:
    parts: list[tuple[bool, str]] = []
    buffer: list[str] = []
    in_fence = False
    for line in source.splitlines():
        opening = line.lstrip().startswith("```")
        if opening and not in_fence:
            if buffer:
                parts.append((False, "\n".join(buffer)))
                buffer = []
            buffer.append(line)
            in_fence = True
            continue
        if opening and in_fence:
            buffer.append(line)
            parts.append((True, "\n".join(buffer)))
            buffer = []
            in_fence = False
            continue
        buffer.append(line)
    if buffer:
        parts.append((in_fence, "\n".join(buffer)))
    return parts


def _normalize_prose(text: str) -> str:
    without_html = _HTML_TAG.sub("", text)
    lines = [line.rstrip() for line in without_html.splitlines()]
    collapsed = re.sub(r"\n{3,}", "\n\n", "\n".join(lines))
    return collapsed.strip()
