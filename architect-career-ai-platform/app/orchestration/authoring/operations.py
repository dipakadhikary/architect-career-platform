"""Authoring operations. Each one has its own prompt, not a generic instruction."""

from __future__ import annotations

from enum import StrEnum


class AuthoringOperation(StrEnum):
    GENERATE = "GENERATE"
    IMPROVE = "IMPROVE"
    REWRITE = "REWRITE"
    SUMMARIZE = "SUMMARIZE"
    EXPAND = "EXPAND"
    GENERATE_QA = "GENERATE_QA"
    GENERATE_EXAMPLES = "GENERATE_EXAMPLES"
    GENERATE_EXPLANATION = "GENERATE_EXPLANATION"
    GENERATE_OBJECTIVES = "GENERATE_OBJECTIVES"
    GENERATE_PREREQUISITES = "GENERATE_PREREQUISITES"
    SUGGEST_STRUCTURE = "SUGGEST_STRUCTURE"
    GENERATE_CODE = "GENERATE_CODE"


SOURCE_REQUIRED = frozenset(
    {
        AuthoringOperation.IMPROVE,
        AuthoringOperation.REWRITE,
        AuthoringOperation.SUMMARIZE,
        AuthoringOperation.EXPAND,
        AuthoringOperation.GENERATE_EXPLANATION,
        AuthoringOperation.GENERATE_OBJECTIVES,
        AuthoringOperation.GENERATE_PREREQUISITES,
        AuthoringOperation.SUGGEST_STRUCTURE,
    }
)

TOPIC_OR_SOURCE = frozenset(
    {
        AuthoringOperation.GENERATE,
        AuthoringOperation.GENERATE_QA,
        AuthoringOperation.GENERATE_EXAMPLES,
        AuthoringOperation.GENERATE_CODE,
    }
)

STRUCTURED = frozenset({AuthoringOperation.GENERATE_QA})
FENCED = frozenset({AuthoringOperation.GENERATE_CODE, AuthoringOperation.GENERATE_EXAMPLES})
DIFFABLE = frozenset(
    {
        AuthoringOperation.IMPROVE,
        AuthoringOperation.REWRITE,
        AuthoringOperation.EXPAND,
    }
)

_TASKS: dict[AuthoringOperation, str] = {
    AuthoringOperation.GENERATE: "Write a new Markdown knowledge note.",
    AuthoringOperation.IMPROVE: "Improve the source Markdown. Keep the same facts.",
    AuthoringOperation.REWRITE: "Rewrite the source Markdown using the user instructions.",
    AuthoringOperation.SUMMARIZE: "Summarize the source Markdown in concise Markdown.",
    AuthoringOperation.EXPAND: "Expand the source Markdown with more explanation.",
    AuthoringOperation.GENERATE_QA: "Write questions and answers as JSON.",
    AuthoringOperation.GENERATE_EXAMPLES: "Write Markdown examples with fenced code blocks.",
    AuthoringOperation.GENERATE_EXPLANATION: "Explain the source Markdown for a learner.",
    AuthoringOperation.GENERATE_OBJECTIVES: "List learning objectives as a Markdown list.",
    AuthoringOperation.GENERATE_PREREQUISITES: "List prerequisites as a Markdown list.",
    AuthoringOperation.SUGGEST_STRUCTURE: "Suggest a Markdown heading structure.",
    AuthoringOperation.GENERATE_CODE: "Write a fenced code example in Markdown.",
}


def task_for(operation: AuthoringOperation) -> str:
    return _TASKS[operation]
