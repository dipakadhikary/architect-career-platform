"""SQLite drafts for AI proposals. This is not the ACOS knowledge database."""

from __future__ import annotations

import json
import sqlite3
import threading
import uuid
from dataclasses import dataclass
from datetime import UTC, datetime

from app.intelligence.assistant.models import AnswerSource


def _now() -> str:
    return datetime.now(UTC).isoformat()


@dataclass(slots=True, frozen=True)
class ProposalRow:
    id: str
    owner_id: str
    operation: str
    status: str
    topic: str
    instructions: str
    source_content: str
    content_id: str | None
    source_version: int | None
    conversation_id: str | None
    use_knowledge: bool
    difficulty: str
    question_count: int
    content: str
    questions: tuple[dict[str, str], ...]
    warnings: tuple[str, ...]
    sources: tuple[AnswerSource, ...]
    model: str
    provider: str
    prompt_version: str
    prompt_tokens: int
    completion_tokens: int
    grounded: bool
    created_at: str
    updated_at: str


class ProposalStore:
    """One connection for AI drafts. Rejected rows stay for audit."""

    def __init__(self, path: str) -> None:
        self._lock = threading.Lock()
        self._conn = sqlite3.connect(path, check_same_thread=False)
        self._conn.row_factory = sqlite3.Row
        self._conn.execute("PRAGMA foreign_keys = ON")
        self._conn.execute("PRAGMA journal_mode = WAL")
        self._migrate()

    def close(self) -> None:
        with self._lock:
            self._conn.close()

    def insert(self, row: ProposalRow) -> ProposalRow:
        with self._lock, self._conn:
            self._conn.execute(
                """
                insert into ai_authoring_proposal (
                    id, owner_id, operation, status, topic, instructions, source_content,
                    content_id, source_version, conversation_id, use_knowledge, difficulty,
                    question_count, content, questions_json, warnings_json, sources_json,
                    model, provider, prompt_version, prompt_tokens, completion_tokens,
                    grounded, created_at, updated_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                _values(row),
            )
        return row

    def get(self, proposal_id: str) -> ProposalRow | None:
        with self._lock:
            found = self._conn.execute(
                "select * from ai_authoring_proposal where id = ?",
                (proposal_id,),
            ).fetchone()
        return _row(found) if found else None

    def update_content(self, proposal_id: str, owner_id: str, content: str, status: str) -> bool:
        with self._lock, self._conn:
            cursor = self._conn.execute(
                """
                update ai_authoring_proposal
                set content = ?, status = ?, updated_at = ?
                where id = ? and owner_id = ?
                """,
                (content, status, _now(), proposal_id, owner_id),
            )
        return cursor.rowcount == 1

    def update_status(self, proposal_id: str, owner_id: str, status: str) -> bool:
        with self._lock, self._conn:
            cursor = self._conn.execute(
                """
                update ai_authoring_proposal
                set status = ?, updated_at = ?
                where id = ? and owner_id = ?
                """,
                (status, _now(), proposal_id, owner_id),
            )
        return cursor.rowcount == 1

    def _migrate(self) -> None:
        self._conn.executescript("""
            create table if not exists ai_authoring_proposal (
                id text primary key,
                owner_id text not null,
                operation text not null,
                status text not null,
                topic text not null,
                instructions text not null,
                source_content text not null,
                content_id text,
                source_version integer,
                conversation_id text,
                use_knowledge integer not null,
                difficulty text not null,
                question_count integer not null,
                content text not null,
                questions_json text not null,
                warnings_json text not null,
                sources_json text not null,
                model text not null,
                provider text not null,
                prompt_version text not null,
                prompt_tokens integer not null,
                completion_tokens integer not null,
                grounded integer not null,
                created_at text not null,
                updated_at text not null
            );
            create index if not exists idx_ai_authoring_owner_updated
                on ai_authoring_proposal (owner_id, updated_at desc);
            """)
        self._conn.commit()


def new_id() -> str:
    return str(uuid.uuid4())


def _values(row: ProposalRow) -> tuple[object, ...]:
    return (
        row.id,
        row.owner_id,
        row.operation,
        row.status,
        row.topic,
        row.instructions,
        row.source_content,
        row.content_id,
        row.source_version,
        row.conversation_id,
        int(row.use_knowledge),
        row.difficulty,
        row.question_count,
        row.content,
        json.dumps(list(row.questions)),
        json.dumps(list(row.warnings)),
        json.dumps([item.model_dump(by_alias=True) for item in row.sources]),
        row.model,
        row.provider,
        row.prompt_version,
        row.prompt_tokens,
        row.completion_tokens,
        int(row.grounded),
        row.created_at,
        row.updated_at,
    )


def _row(record: sqlite3.Row) -> ProposalRow:
    sources = [AnswerSource.model_validate(item) for item in json.loads(record["sources_json"])]
    questions = tuple(json.loads(record["questions_json"]))
    return ProposalRow(
        id=record["id"],
        owner_id=record["owner_id"],
        operation=record["operation"],
        status=record["status"],
        topic=record["topic"],
        instructions=record["instructions"],
        source_content=record["source_content"],
        content_id=record["content_id"],
        source_version=record["source_version"],
        conversation_id=record["conversation_id"],
        use_knowledge=bool(record["use_knowledge"]),
        difficulty=record["difficulty"],
        question_count=int(record["question_count"]),
        content=record["content"],
        questions=tuple(questions),
        warnings=tuple(json.loads(record["warnings_json"])),
        sources=tuple(sources),
        model=record["model"],
        provider=record["provider"],
        prompt_version=record["prompt_version"],
        prompt_tokens=int(record["prompt_tokens"]),
        completion_tokens=int(record["completion_tokens"]),
        grounded=bool(record["grounded"]),
        created_at=record["created_at"],
        updated_at=record["updated_at"],
    )
