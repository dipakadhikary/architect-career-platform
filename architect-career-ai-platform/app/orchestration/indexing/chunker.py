"""Heading-aware chunking for technical Markdown.

Character size is a limit applied after section boundaries. Code fences stay intact.
"""

from __future__ import annotations

import re
import uuid

from app.intelligence.indexing.models import IndexChunk

INDEX_NAMESPACE = uuid.UUID("8f3c2a1e-6b47-4d5a-9c1e-0a7b6d5e4f31")
_HEADING = re.compile(r"^(#{1,6})\s+(.*?)\s*$")


def deterministic_chunk_id(
    content_id: str, version: int, index: int, qa_type: str | None
) -> str:
    """Stable id so a repeated index replaces the same vector."""
    name = f"{content_id}:{version}:{index}:{qa_type or 'BODY'}"
    return str(uuid.uuid5(INDEX_NAMESPACE, name))


def chunk_document(
    *,
    content_id: str,
    content_version: int,
    title: str,
    markdown: str,
    qa_type: str | None,
    chunk_size: int,
    chunk_overlap: int,
    start_index: int = 0,
) -> list[IndexChunk]:
    """Split one Markdown body into contextual chunks."""
    size = max(chunk_size, 1)
    overlap = min(max(chunk_overlap, 0), size - 1) if size > 1 else 0
    sections = _sections(markdown, title)
    chunks: list[IndexChunk] = []
    index = start_index
    for section, blocks in sections:
        for text in _pack(title, section, blocks, size, overlap):
            chunks.append(
                IndexChunk(
                    chunk_id=deterministic_chunk_id(content_id, content_version, index, qa_type),
                    content_id=content_id,
                    text=text,
                    index=index,
                    section=section or title,
                    qa_type=qa_type,
                )
            )
            index += 1
    return chunks


def _sections(markdown: str, title: str) -> list[tuple[str, list[str]]]:
    stack: list[tuple[int, str]] = []
    section = title
    blocks: list[str] = []
    sections: list[tuple[str, list[str]]] = []
    paragraph: list[str] = []
    fence: list[str] | None = None

    def flush_paragraph() -> None:
        if paragraph:
            blocks.append("\n".join(paragraph).strip())
            paragraph.clear()

    def flush_section() -> None:
        flush_paragraph()
        kept = [block for block in blocks if block.strip()]
        if kept:
            sections.append((section, kept))
        blocks.clear()

    for line in markdown.splitlines():
        if fence is not None:
            fence.append(line)
            if line.lstrip().startswith("```"):
                blocks.append("\n".join(fence))
                fence = None
            continue
        if line.lstrip().startswith("```"):
            flush_paragraph()
            fence = [line]
            continue
        heading = _HEADING.match(line)
        if heading:
            flush_section()
            level = len(heading.group(1))
            text = heading.group(2).strip()
            while stack and stack[-1][0] >= level:
                stack.pop()
            stack.append((level, text))
            section = " > ".join(item[1] for item in stack)
            continue
        if not line.strip():
            flush_paragraph()
            continue
        paragraph.append(line)
    if fence is not None:
        blocks.append("\n".join(fence))
    flush_section()
    return sections


def _pack(title: str, section: str, blocks: list[str], size: int, overlap: int) -> list[str]:
    context = _context(title, section)
    budget = max(size - len(context) - 2, 1)
    packed: list[str] = []
    buffer = ""
    for block in blocks:
        piece = block.strip()
        if not piece:
            continue
        if not buffer and len(piece) > budget:
            packed.append(_join(context, piece))
            continue
        candidate = piece if not buffer else f"{buffer}\n\n{piece}"
        if buffer and len(candidate) > budget:
            packed.append(_join(context, buffer))
            tail = _overlap_tail(buffer, overlap)
            buffer = f"{tail}\n\n{piece}".strip() if tail else piece
            continue
        buffer = candidate
    if buffer:
        packed.append(_join(context, buffer))
    return packed


def _context(title: str, section: str) -> str:
    lines = [f"# {title}"] if title else []
    if section and section != title:
        lines.append(f"Section: {section}")
    return "\n".join(lines)


def _join(context: str, body: str) -> str:
    if not context:
        return body
    return f"{context}\n\n{body}"


def _overlap_tail(text: str, overlap: int) -> str:
    if overlap <= 0:
        return ""
    tail = text[-overlap:].strip()
    if "```" in tail:
        return ""
    return tail
