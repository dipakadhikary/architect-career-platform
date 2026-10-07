"""SQLite store for AI conversations. This is not the ACOS business database."""

from __future__ import annotations

import sqlite3
import threading
import uuid
from dataclasses import dataclass
from datetime import UTC, datetime, timedelta

from app.shared.exceptions import ConflictError


def _now() -> str:
    return datetime.now(UTC).isoformat()


@dataclass(slots=True, frozen=True)
class SourceRow:
    content_id: str
    chunk_id: str
    title: str
    content_type: str
    section: str
    path: str
    source_url: str
    score: float
    rank: int


@dataclass(slots=True, frozen=True)
class MessageRow:
    id: str
    conversation_id: str
    role: str
    content: str
    sequence_number: int
    status: str
    created_at: str
    model: str
    provider: str
    prompt_tokens: int
    completion_tokens: int
    grounded: bool
    retrieval_query: str
    sources: tuple[SourceRow, ...] = ()


@dataclass(slots=True, frozen=True)
class ConversationRow:
    id: str
    owner_id: str
    title: str
    status: str
    created_at: str
    updated_at: str


class ConversationStore:
    """One connection, serialized writes. Foreign keys remove messages with a conversation."""

    def __init__(self, path: str) -> None:
        self._lock = threading.Lock()
        self._conn = sqlite3.connect(path, check_same_thread=False)
        self._conn.row_factory = sqlite3.Row
        self._conn.execute("PRAGMA foreign_keys = ON")
        self._migrate()

    def close(self) -> None:
        with self._lock:
            self._conn.close()

    def create(self, owner_id: str, title: str) -> ConversationRow:
        now = _now()
        row = ConversationRow(
            id=str(uuid.uuid4()),
            owner_id=owner_id,
            title=title,
            status="ACTIVE",
            created_at=now,
            updated_at=now,
        )
        with self._lock, self._conn:
            self._conn.execute(
                """
                insert into ai_conversation
                    (id, owner_id, title, status, created_at, updated_at)
                values (?, ?, ?, ?, ?, ?)
                """,
                (row.id, row.owner_id, row.title, row.status, row.created_at, row.updated_at),
            )
        return row

    def get(self, conversation_id: str) -> ConversationRow | None:
        with self._lock:
            found = self._conn.execute(
                "select * from ai_conversation where id = ?",
                (conversation_id,),
            ).fetchone()
        return _conversation(found) if found else None

    def list_page(
        self, owner_id: str, *, page: int, size: int
    ) -> tuple[list[ConversationRow], int]:
        with self._lock:
            total = int(
                self._conn.execute(
                    "select count(*) from ai_conversation where owner_id = ?",
                    (owner_id,),
                ).fetchone()[0]
            )
            rows = self._conn.execute(
                """
                select * from ai_conversation
                where owner_id = ?
                order by updated_at desc, id desc
                limit ? offset ?
                """,
                (owner_id, size, page * size),
            ).fetchall()
        return [_conversation(row) for row in rows], total

    def search_messages(
        self, owner_id: str, query: str, *, limit: int
    ) -> list[tuple[str, str, str]]:
        """Return owner-scoped (conversation id, title, snippet) matches. The query is bound."""
        escaped = query.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")
        pattern = f"%{escaped}%"
        with self._lock:
            rows = self._conn.execute(
                """
                select c.id, c.title, m.content
                from ai_message m
                join ai_conversation c on c.id = m.conversation_id
                where c.owner_id = ? and m.role = 'USER' and m.content like ? escape '\\'
                order by m.created_at desc
                limit ?
                """,
                (owner_id, pattern, limit),
            ).fetchall()
        return [(str(row[0]), str(row[1]), str(row[2])[:240]) for row in rows]

    def rename(self, conversation_id: str, owner_id: str, title: str) -> ConversationRow | None:
        now = _now()
        with self._lock, self._conn:
            updated = self._conn.execute(
                """
                update ai_conversation
                set title = ?, updated_at = ?
                where id = ? and owner_id = ?
                """,
                (title, now, conversation_id, owner_id),
            ).rowcount
        if updated == 0:
            return None
        return self.get(conversation_id)

    def delete(self, conversation_id: str, owner_id: str) -> bool:
        with self._lock, self._conn:
            deleted = self._conn.execute(
                "delete from ai_conversation where id = ? and owner_id = ?",
                (conversation_id, owner_id),
            ).rowcount
        return deleted == 1

    def message_count(self, conversation_id: str) -> int:
        with self._lock:
            return int(
                self._conn.execute(
                    "select count(*) from ai_message where conversation_id = ?",
                    (conversation_id,),
                ).fetchone()[0]
            )

    def messages(self, conversation_id: str, *, limit: int) -> list[MessageRow]:
        with self._lock:
            rows = self._conn.execute(
                """
                select * from ai_message
                where conversation_id = ?
                order by sequence_number desc
                limit ?
                """,
                (conversation_id, limit),
            ).fetchall()
            loaded = [self._message(row) for row in rows]
        loaded.reverse()
        return loaded

    def find_idempotent(self, conversation_id: str, key: str) -> MessageRow | None:
        with self._lock:
            row = self._conn.execute(
                """
                select * from ai_message
                where conversation_id = ? and idempotency_key = ?
                """,
                (conversation_id, key),
            ).fetchone()
            return self._message(row) if row else None

    def assistant_after(self, conversation_id: str, sequence_number: int) -> MessageRow | None:
        with self._lock:
            row = self._conn.execute(
                """
                select * from ai_message
                where conversation_id = ? and sequence_number = ?
                """,
                (conversation_id, sequence_number + 1),
            ).fetchone()
            return self._message(row) if row else None

    def expire_stale_processing(self, conversation_id: str, timeout_seconds: float) -> None:
        cutoff = (datetime.now(UTC) - timedelta(seconds=timeout_seconds)).isoformat()
        with self._lock, self._conn:
            self._conn.execute(
                """
                update ai_message
                set status = 'FAILED',
                    content = 'Sorry, I could not generate a response right now. Please try again.'
                where conversation_id = ? and status = 'PROCESSING' and created_at < ?
                """,
                (conversation_id, cutoff),
            )

    def has_processing(self, conversation_id: str) -> bool:
        with self._lock:
            row = self._conn.execute(
                """
                select 1 from ai_message
                where conversation_id = ? and status = 'PROCESSING'
                """,
                (conversation_id,),
            ).fetchone()
        return row is not None

    def begin_turn(
        self,
        *,
        conversation_id: str,
        owner_id: str,
        content: str,
        idempotency_key: str | None,
        retrieval_query: str,
        title: str | None,
    ) -> tuple[MessageRow, MessageRow]:
        """Insert the user turn and a processing assistant row in one transaction."""
        now = _now()
        user_id = str(uuid.uuid4())
        assistant_id = str(uuid.uuid4())
        with self._lock, self._conn:
            processing = self._conn.execute(
                """
                select 1 from ai_message
                where conversation_id = ? and status = 'PROCESSING'
                """,
                (conversation_id,),
            ).fetchone()
            if processing is not None:
                raise ConflictError("This conversation is already generating a response")
            user_sequence = self._next_sequence(conversation_id)
            self._conn.execute(
                """
                insert into ai_message (
                    id, conversation_id, owner_id, role, content, sequence_number,
                    status, created_at, model, provider, prompt_tokens, completion_tokens,
                    grounded, idempotency_key, retrieval_query
                ) values (?, ?, ?, 'USER', ?, ?, 'COMPLETED', ?, '', '', 0, 0, 0, ?, ?)
                """,
                (
                    user_id,
                    conversation_id,
                    owner_id,
                    content,
                    user_sequence,
                    now,
                    idempotency_key,
                    retrieval_query,
                ),
            )
            self._conn.execute(
                """
                insert into ai_message (
                    id, conversation_id, owner_id, role, content, sequence_number,
                    status, created_at, model, provider, prompt_tokens, completion_tokens,
                    grounded, idempotency_key, retrieval_query
                ) values (?, ?, ?, 'ASSISTANT', '', ?, 'PROCESSING', ?, '', '', 0, 0, 0, null, '')
                """,
                (assistant_id, conversation_id, owner_id, user_sequence + 1, now),
            )
            if title:
                self._conn.execute(
                    "update ai_conversation set title = ?, updated_at = ? where id = ?",
                    (title, now, conversation_id),
                )
            else:
                self._conn.execute(
                    "update ai_conversation set updated_at = ? where id = ?",
                    (now, conversation_id),
                )
            user_row = self._conn.execute(
                "select * from ai_message where id = ?",
                (user_id,),
            ).fetchone()
            assistant_row = self._conn.execute(
                "select * from ai_message where id = ?",
                (assistant_id,),
            ).fetchone()
            return self._message(user_row), self._message(assistant_row)

    def add_user_message(
        self,
        *,
        conversation_id: str,
        owner_id: str,
        content: str,
        idempotency_key: str | None,
        retrieval_query: str,
        title: str | None,
    ) -> MessageRow:
        now = _now()
        message_id = str(uuid.uuid4())
        with self._lock, self._conn:
            sequence = self._next_sequence(conversation_id)
            self._conn.execute(
                """
                insert into ai_message (
                    id, conversation_id, owner_id, role, content, sequence_number,
                    status, created_at, model, provider, prompt_tokens, completion_tokens,
                    grounded, idempotency_key, retrieval_query
                ) values (?, ?, ?, 'USER', ?, ?, 'COMPLETED', ?, '', '', 0, 0, 0, ?, ?)
                """,
                (
                    message_id,
                    conversation_id,
                    owner_id,
                    content,
                    sequence,
                    now,
                    idempotency_key,
                    retrieval_query,
                ),
            )
            if title:
                self._conn.execute(
                    "update ai_conversation set title = ?, updated_at = ? where id = ?",
                    (title, now, conversation_id),
                )
            else:
                self._conn.execute(
                    "update ai_conversation set updated_at = ? where id = ?",
                    (now, conversation_id),
                )
            row = self._conn.execute(
                "select * from ai_message where id = ?",
                (message_id,),
            ).fetchone()
            return self._message(row)

    def add_processing(self, *, conversation_id: str, owner_id: str) -> MessageRow:
        now = _now()
        message_id = str(uuid.uuid4())
        with self._lock, self._conn:
            sequence = self._next_sequence(conversation_id)
            self._conn.execute(
                """
                insert into ai_message (
                    id, conversation_id, owner_id, role, content, sequence_number,
                    status, created_at, model, provider, prompt_tokens, completion_tokens,
                    grounded, idempotency_key, retrieval_query
                ) values (?, ?, ?, 'ASSISTANT', '', ?, 'PROCESSING', ?, '', '', 0, 0, 0, null, '')
                """,
                (message_id, conversation_id, owner_id, sequence, now),
            )
            row = self._conn.execute(
                "select * from ai_message where id = ?",
                (message_id,),
            ).fetchone()
            return self._message(row)

    def complete_assistant(
        self,
        message_id: str,
        *,
        content: str,
        model: str,
        provider: str,
        prompt_tokens: int,
        completion_tokens: int,
        grounded: bool,
        sources: list[SourceRow],
    ) -> MessageRow:
        with self._lock, self._conn:
            self._conn.execute(
                """
                update ai_message
                set content = ?, status = 'COMPLETED', model = ?, provider = ?,
                    prompt_tokens = ?, completion_tokens = ?, grounded = ?
                where id = ?
                """,
                (
                    content,
                    model,
                    provider,
                    prompt_tokens,
                    completion_tokens,
                    1 if grounded else 0,
                    message_id,
                ),
            )
            self._conn.execute("delete from ai_message_source where message_id = ?", (message_id,))
            for source in sources:
                self._conn.execute(
                    """
                    insert into ai_message_source (
                        id, message_id, content_id, chunk_id, title, content_type,
                        section, path, source_url, score, rank
                    ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    (
                        str(uuid.uuid4()),
                        message_id,
                        source.content_id,
                        source.chunk_id,
                        source.title,
                        source.content_type,
                        source.section,
                        source.path,
                        source.source_url,
                        source.score,
                        source.rank,
                    ),
                )
            conversation_id = self._conn.execute(
                "select conversation_id from ai_message where id = ?",
                (message_id,),
            ).fetchone()[0]
            self._conn.execute(
                "update ai_conversation set updated_at = ? where id = ?",
                (_now(), conversation_id),
            )
            row = self._conn.execute(
                "select * from ai_message where id = ?",
                (message_id,),
            ).fetchone()
            return self._message(row)

    def fail_assistant(self, message_id: str, content: str) -> MessageRow:
        with self._lock, self._conn:
            self._conn.execute(
                "update ai_message set content = ?, status = 'FAILED' where id = ?",
                (content, message_id),
            )
            row = self._conn.execute(
                "select * from ai_message where id = ?",
                (message_id,),
            ).fetchone()
            return self._message(row)

    def mark_processing(self, message_id: str) -> MessageRow:
        with self._lock, self._conn:
            self._conn.execute(
                """
                update ai_message
                set status = 'PROCESSING', content = '', model = '', provider = ''
                where id = ?
                """,
                (message_id,),
            )
            row = self._conn.execute(
                "select * from ai_message where id = ?",
                (message_id,),
            ).fetchone()
            return self._message(row)

    def _next_sequence(self, conversation_id: str) -> int:
        current = self._conn.execute(
            "select coalesce(max(sequence_number), 0) from ai_message where conversation_id = ?",
            (conversation_id,),
        ).fetchone()[0]
        return int(current) + 1

    def _message(self, row: sqlite3.Row) -> MessageRow:
        sources = self._conn.execute(
            """
            select * from ai_message_source
            where message_id = ?
            order by rank asc
            """,
            (row["id"],),
        ).fetchall()
        return MessageRow(
            id=row["id"],
            conversation_id=row["conversation_id"],
            role=row["role"],
            content=row["content"],
            sequence_number=int(row["sequence_number"]),
            status=row["status"],
            created_at=row["created_at"],
            model=row["model"],
            provider=row["provider"],
            prompt_tokens=int(row["prompt_tokens"]),
            completion_tokens=int(row["completion_tokens"]),
            grounded=bool(row["grounded"]),
            retrieval_query=row["retrieval_query"] or "",
            sources=tuple(_source(item) for item in sources),
        )

    def _migrate(self) -> None:
        self._conn.executescript("""
            create table if not exists ai_conversation (
                id text primary key,
                owner_id text not null,
                title text not null,
                status text not null,
                created_at text not null,
                updated_at text not null
            );
            create index if not exists idx_ai_conversation_owner_updated
                on ai_conversation (owner_id, updated_at desc);
            create table if not exists ai_message (
                id text primary key,
                conversation_id text not null references ai_conversation(id) on delete cascade,
                owner_id text not null,
                role text not null,
                content text not null,
                sequence_number integer not null,
                status text not null,
                created_at text not null,
                model text not null default '',
                provider text not null default '',
                prompt_tokens integer not null default 0,
                completion_tokens integer not null default 0,
                grounded integer not null default 0,
                idempotency_key text,
                retrieval_query text not null default '',
                unique (conversation_id, sequence_number)
            );
            create index if not exists idx_ai_message_conversation_seq
                on ai_message (conversation_id, sequence_number);
            create unique index if not exists idx_ai_message_idempotency
                on ai_message (conversation_id, idempotency_key)
                where idempotency_key is not null;
            create table if not exists ai_message_source (
                id text primary key,
                message_id text not null references ai_message(id) on delete cascade,
                content_id text not null,
                chunk_id text not null,
                title text not null,
                content_type text not null,
                section text not null,
                path text not null,
                source_url text not null,
                score real not null,
                rank integer not null
            );
            """)
        self._conn.commit()


def _conversation(row: sqlite3.Row) -> ConversationRow:
    return ConversationRow(
        id=row["id"],
        owner_id=row["owner_id"],
        title=row["title"],
        status=row["status"],
        created_at=row["created_at"],
        updated_at=row["updated_at"],
    )


def _source(row: sqlite3.Row) -> SourceRow:
    return SourceRow(
        content_id=row["content_id"],
        chunk_id=row["chunk_id"],
        title=row["title"],
        content_type=row["content_type"],
        section=row["section"],
        path=row["path"],
        source_url=row["source_url"],
        score=float(row["score"]),
        rank=int(row["rank"]),
    )
