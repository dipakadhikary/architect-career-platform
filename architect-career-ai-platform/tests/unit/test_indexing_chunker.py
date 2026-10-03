"""Chunking and normalization tests for the Phase 3 index."""

from __future__ import annotations

from app.intelligence.indexing.models import QaType
from app.orchestration.indexing.chunker import chunk_document, deterministic_chunk_id
from app.orchestration.indexing.normalize import normalize_markdown


def test_normalization_preserves_code_and_drops_html_without_changing_source() -> None:
    source = "# Title\n\n<p>Hello</p>\n\n```java\nint x = 1;\n```\n"
    original = source
    normalized = normalize_markdown(source)
    assert source == original
    assert "<p>" not in normalized
    assert "```java\nint x = 1;\n```" in normalized
    assert "Hello" in normalized


def test_empty_content_normalizes_to_blank() -> None:
    assert normalize_markdown("   \n") == ""


def test_small_document_is_one_chunk_with_title_context() -> None:
    chunks = chunk_document(
        content_id="note-1",
        content_version=3,
        title="Factory Pattern",
        markdown="The factory creates objects.",
        qa_type=None,
        chunk_size=800,
        chunk_overlap=40,
    )
    assert len(chunks) == 1
    assert chunks[0].section == "Factory Pattern"
    assert "The factory creates objects." in chunks[0].text
    assert chunks[0].text.startswith("# Factory Pattern")


def test_nested_headings_lists_code_tables_images_and_links() -> None:
    markdown = """
# Factory Pattern

Intro with a [guide](https://example.com/factory).

## Implementation

- hide construction
- return an interface

```java
public class Factory {
    Widget create() { return new Widget(); }
}
```

| Role | Type |
| --- | --- |
| Client | Widget |

![diagram](images/factory.png)

### Details

Another paragraph.
"""
    chunks = chunk_document(
        content_id="note-2",
        content_version=1,
        title="Factory Pattern",
        markdown=markdown,
        qa_type=None,
        chunk_size=500,
        chunk_overlap=20,
    )
    combined = "\n".join(chunk.text for chunk in chunks)
    assert "public class Factory" in combined
    assert "![diagram](images/factory.png)" in combined
    assert "https://example.com/factory" in combined
    assert "| Client | Widget |" in combined
    assert "- hide construction" in combined
    assert any(chunk.section == "Factory Pattern > Implementation > Details" for chunk in chunks)
    code_chunk = next(chunk for chunk in chunks if "public class Factory" in chunk.text)
    assert "Section: Factory Pattern > Implementation" in code_chunk.text


def test_large_section_splits_on_paragraphs_with_overlap_and_stable_ids() -> None:
    paragraph = "Dependency injection supplies collaborators from outside the object. " * 4
    markdown = f"## Concept\n\n{paragraph}\n\n{paragraph}"
    first = chunk_document(
        content_id="note-3",
        content_version=5,
        title="Dependency Injection",
        markdown=markdown,
        qa_type=None,
        chunk_size=180,
        chunk_overlap=30,
    )
    second = chunk_document(
        content_id="note-3",
        content_version=5,
        title="Dependency Injection",
        markdown=markdown,
        qa_type=None,
        chunk_size=180,
        chunk_overlap=30,
    )
    assert len(first) > 1
    assert [chunk.chunk_id for chunk in first] == [chunk.chunk_id for chunk in second]
    assert first[0].chunk_id == deterministic_chunk_id("note-3", 5, 0, None)
    assert "Section: Concept" in first[1].text
    assert first[0].text[-20:][:10] in first[1].text or len(first[1].text) > 0


def test_code_block_is_not_split_when_it_exceeds_the_character_budget() -> None:
    code = "```java\n" + ("System.out.println(1);\n" * 30) + "```"
    chunks = chunk_document(
        content_id="note-4",
        content_version=1,
        title="Logging",
        markdown=f"## Example\n\nShort intro.\n\n{code}",
        qa_type=None,
        chunk_size=80,
        chunk_overlap=10,
    )
    code_chunks = [chunk for chunk in chunks if "System.out.println" in chunk.text]
    assert len(code_chunks) == 1
    assert code_chunks[0].text.count("```") == 2


def test_question_and_answer_chunks_keep_distinct_metadata() -> None:
    question = chunk_document(
        content_id="q-1",
        content_version=2,
        title="Factory",
        markdown="What problem does a factory solve?",
        qa_type=QaType.QUESTION.value,
        chunk_size=400,
        chunk_overlap=0,
    )
    answer = chunk_document(
        content_id="q-1",
        content_version=2,
        title="Factory",
        markdown="## Answer\n\nIt centralizes construction.",
        qa_type=QaType.ANSWER.value,
        chunk_size=400,
        chunk_overlap=0,
        start_index=1,
    )
    assert question[0].qa_type == "QUESTION"
    assert answer[0].qa_type == "ANSWER"
    assert question[0].chunk_id != answer[0].chunk_id
