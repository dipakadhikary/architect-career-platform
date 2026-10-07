"""Reject empty, unsafe, or malformed authoring output before it becomes a proposal."""

from __future__ import annotations

import json
import re

from pydantic import BaseModel, Field, ValidationError

from app.orchestration.authoring.operations import FENCED, AuthoringOperation
from app.shared.exceptions import ValidationFailedError

_UNSAFE = re.compile(r"<\s*script|javascript\s*:", re.IGNORECASE)
_FENCE = re.compile(r"```[a-zA-Z0-9_+-]*\n.*?```", re.DOTALL)
_DIFFICULTIES = frozenset({"BEGINNER", "INTERMEDIATE", "ADVANCED"})


class QuestionDraft(BaseModel):
    question: str = Field(min_length=1)
    answer: str = Field(min_length=1)
    difficulty: str
    explanation: str = ""


class QuestionBatch(BaseModel):
    questions: list[QuestionDraft] = Field(min_length=1)


def validate_draft_text(text: str, *, max_characters: int) -> str:
    """Check an edited draft. Structured generation rules apply only to model output."""
    cleaned = text.strip()
    if not cleaned:
        raise ValidationFailedError("The draft was empty")
    if len(cleaned) > max_characters:
        raise ValidationFailedError("The draft exceeds the configured size limit")
    if _UNSAFE.search(cleaned):
        raise ValidationFailedError("The draft contains unsupported HTML")
    return cleaned


def validate_output(
    operation: AuthoringOperation,
    text: str,
    *,
    max_characters: int,
    max_questions: int,
) -> tuple[str, list[QuestionDraft]]:
    """Return Markdown plus structured questions when the operation requires them."""
    cleaned = text.strip()
    if operation == AuthoringOperation.GENERATE_QA and cleaned.startswith("```"):
        cleaned = _unwrap_fence(cleaned)
    if not cleaned:
        raise ValidationFailedError("The draft was empty")
    if len(cleaned) > max_characters:
        raise ValidationFailedError("The draft exceeds the configured size limit")
    if _UNSAFE.search(cleaned):
        raise ValidationFailedError("The draft contains unsupported HTML")
    if operation == AuthoringOperation.GENERATE_QA:
        questions = _questions(cleaned, max_questions=max_questions)
        return _questions_as_markdown(questions), questions
    if operation in FENCED and _FENCE.search(cleaned) is None:
        raise ValidationFailedError("The draft is missing a fenced code block")
    return cleaned, []


def _questions(text: str, *, max_questions: int) -> list[QuestionDraft]:
    try:
        payload = QuestionBatch.model_validate(json.loads(text))
    except (json.JSONDecodeError, ValidationError) as exc:
        raise ValidationFailedError("The question draft was not valid JSON") from exc
    if len(payload.questions) > max_questions:
        raise ValidationFailedError("The question draft exceeds the configured limit")
    for item in payload.questions:
        if item.difficulty not in _DIFFICULTIES:
            raise ValidationFailedError("Question difficulty is not supported")
    return payload.questions


def _questions_as_markdown(questions: list[QuestionDraft]) -> str:
    blocks = ["# Questions", ""]
    for index, item in enumerate(questions, start=1):
        blocks.append(f"## {index}. {item.question}")
        blocks.append("")
        blocks.append(item.answer)
        blocks.append("")
        blocks.append(f"Difficulty: {item.difficulty}")
        if item.explanation:
            blocks.append("")
            blocks.append(item.explanation)
        blocks.append("")
    return "\n".join(blocks).strip()


def _unwrap_fence(text: str) -> str:
    lines = text.splitlines()
    if len(lines) >= 2 and lines[-1].strip() == "```":
        return "\n".join(lines[1:-1]).strip()
    return text
