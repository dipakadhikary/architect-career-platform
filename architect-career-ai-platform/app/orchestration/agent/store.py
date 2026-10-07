"""Durable agent execution state. This is not the ACOS business database."""

from __future__ import annotations

import json
import sqlite3
import threading
import uuid
from datetime import UTC, datetime
from typing import Any

from app.orchestration.agent.models import ExecutionRecord, StepRecord


def _now() -> str:
    return datetime.now(UTC).isoformat()


class AgentStore:
    """One connection. Writes are serialized. State survives a process restart."""

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

    def create(
        self,
        *,
        owner_id: str,
        conversation_id: str | None,
        goal: str,
        prompt_version: str,
    ) -> ExecutionRecord:
        now = _now()
        execution_id = str(uuid.uuid4())
        with self._lock, self._conn:
            self._conn.execute(
                """
                insert into ai_agent_execution (
                    id, owner_id, conversation_id, goal, status, answer, error_code,
                    model, provider, prompt_version, prompt_tokens, completion_tokens,
                    estimated_cost_usd, started_at, completed_at, cancel_requested,
                    sources_json, approval_required, proposed_action
                ) values (
                    ?, ?, ?, ?, 'CREATED', '', '', '', '', ?, 0, 0, 0, ?, null, 0, '[]', 0, ''
                )
                """,
                (execution_id, owner_id, conversation_id, goal, prompt_version, now),
            )
        loaded = self.get(execution_id)
        if loaded is None:
            raise RuntimeError("agent execution was not stored")
        return loaded

    def get(self, execution_id: str) -> ExecutionRecord | None:
        with self._lock:
            row = self._conn.execute(
                "select * from ai_agent_execution where id = ?",
                (execution_id,),
            ).fetchone()
            if row is None:
                return None
            steps = self._conn.execute(
                """
                select * from ai_agent_step
                where execution_id = ?
                order by sequence_number asc
                """,
                (execution_id,),
            ).fetchall()
        return _execution(row, steps)

    def mark(
        self,
        execution_id: str,
        *,
        status: str,
        answer: str = "",
        error_code: str = "",
        model: str = "",
        provider: str = "",
        prompt_tokens: int = 0,
        completion_tokens: int = 0,
        estimated_cost_usd: float = 0,
        sources: list[dict[str, Any]] | None = None,
        approval_required: bool = False,
        proposed_action: str = "",
        finished: bool = False,
    ) -> ExecutionRecord:
        completed = _now() if finished else None
        with self._lock, self._conn:
            self._conn.execute(
                """
                update ai_agent_execution
                set status = ?, answer = ?, error_code = ?, model = ?, provider = ?,
                    prompt_tokens = ?, completion_tokens = ?, estimated_cost_usd = ?,
                    sources_json = ?, approval_required = ?, proposed_action = ?,
                    completed_at = coalesce(?, completed_at)
                where id = ?
                """,
                (
                    status,
                    answer,
                    error_code,
                    model,
                    provider,
                    prompt_tokens,
                    completion_tokens,
                    estimated_cost_usd,
                    json.dumps(sources or []),
                    int(approval_required),
                    proposed_action,
                    completed,
                    execution_id,
                ),
            )
        loaded = self.get(execution_id)
        if loaded is None:
            raise RuntimeError("agent execution disappeared")
        return loaded

    def add_step(
        self,
        *,
        execution_id: str,
        sequence_number: int,
        tool_name: str,
        tool_version: str,
        arguments: dict[str, Any],
        status: str,
        success: bool | None,
        error_code: str,
        result: dict[str, Any],
        label: str,
    ) -> StepRecord:
        step_id = str(uuid.uuid4())
        with self._lock, self._conn:
            self._conn.execute(
                """
                insert into ai_agent_step (
                    id, execution_id, sequence_number, tool_name, tool_version,
                    arguments_json, status, success, error_code, result_json, label, created_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                (
                    step_id,
                    execution_id,
                    sequence_number,
                    tool_name,
                    tool_version,
                    json.dumps(arguments)[:2000],
                    status,
                    None if success is None else int(success),
                    error_code,
                    json.dumps(result)[:2000],
                    label,
                    _now(),
                ),
            )
        return StepRecord(
            id=step_id,
            execution_id=execution_id,
            sequence_number=sequence_number,
            tool_name=tool_name,
            tool_version=tool_version,
            status=status,
            success=success,
            error_code=error_code,
            label=label,
        )

    def request_approval(
        self,
        *,
        execution_id: str,
        step_id: str,
        tool_name: str,
        requester_id: str,
    ) -> None:
        with self._lock, self._conn:
            self._conn.execute(
                """
                insert into ai_agent_approval (
                    id, execution_id, step_id, tool_name, status, requester_id,
                    approver_id, created_at, decided_at
                ) values (?, ?, ?, ?, 'REQUESTED', ?, null, ?, null)
                """,
                (str(uuid.uuid4()), execution_id, step_id, tool_name, requester_id, _now()),
            )

    def decide(
        self,
        execution_id: str,
        *,
        approver_id: str,
        approved: bool,
    ) -> bool:
        status = "APPROVED" if approved else "REJECTED"
        with self._lock, self._conn:
            updated = self._conn.execute(
                """
                update ai_agent_approval
                set status = ?, approver_id = ?, decided_at = ?
                where execution_id = ? and status = 'REQUESTED'
                """,
                (status, approver_id, _now(), execution_id),
            ).rowcount
        return updated > 0

    def request_cancel(self, execution_id: str, owner_id: str) -> bool:
        with self._lock, self._conn:
            updated = self._conn.execute(
                """
                update ai_agent_execution
                set cancel_requested = 1
                where id = ? and owner_id = ? and status in ('CREATED', 'PLANNING', 'EXECUTING')
                """,
                (execution_id, owner_id),
            ).rowcount
        return updated == 1

    def cancel_requested(self, execution_id: str) -> bool:
        with self._lock:
            row = self._conn.execute(
                "select cancel_requested from ai_agent_execution where id = ?",
                (execution_id,),
            ).fetchone()
        return bool(row and row[0])

    def _migrate(self) -> None:
        self._conn.executescript("""
            create table if not exists ai_agent_execution (
                id text primary key,
                owner_id text not null,
                conversation_id text,
                goal text not null,
                status text not null,
                answer text not null,
                error_code text not null,
                model text not null,
                provider text not null,
                prompt_version text not null,
                prompt_tokens integer not null,
                completion_tokens integer not null,
                estimated_cost_usd real not null,
                started_at text not null,
                completed_at text,
                cancel_requested integer not null,
                sources_json text not null,
                approval_required integer not null,
                proposed_action text not null
            );
            create index if not exists ix_ai_agent_execution_owner
                on ai_agent_execution (owner_id, started_at desc);
            create table if not exists ai_agent_step (
                id text primary key,
                execution_id text not null,
                sequence_number integer not null,
                tool_name text not null,
                tool_version text not null,
                arguments_json text not null,
                status text not null,
                success integer,
                error_code text not null,
                result_json text not null,
                label text not null,
                created_at text not null,
                unique (execution_id, sequence_number)
            );
            create table if not exists ai_agent_approval (
                id text primary key,
                execution_id text not null,
                step_id text not null,
                tool_name text not null,
                status text not null,
                requester_id text not null,
                approver_id text,
                created_at text not null,
                decided_at text
            );
            """)


def _execution(row: sqlite3.Row, steps: list[sqlite3.Row]) -> ExecutionRecord:
    sources = json.loads(row["sources_json"] or "[]")
    return ExecutionRecord(
        id=row["id"],
        owner_id=row["owner_id"],
        conversation_id=row["conversation_id"],
        goal=row["goal"],
        status=row["status"],
        answer=row["answer"],
        error_code=row["error_code"],
        model=row["model"],
        provider=row["provider"],
        prompt_version=row["prompt_version"],
        prompt_tokens=int(row["prompt_tokens"]),
        completion_tokens=int(row["completion_tokens"]),
        estimated_cost_usd=float(row["estimated_cost_usd"]),
        started_at=row["started_at"],
        completed_at=row["completed_at"],
        steps=tuple(_step(item) for item in steps),
        sources=tuple(sources),
        approval_required=bool(row["approval_required"]),
        proposed_action=row["proposed_action"],
    )


def _step(row: sqlite3.Row) -> StepRecord:
    success = None if row["success"] is None else bool(row["success"])
    return StepRecord(
        id=row["id"],
        execution_id=row["execution_id"],
        sequence_number=int(row["sequence_number"]),
        tool_name=row["tool_name"],
        tool_version=row["tool_version"],
        status=row["status"],
        success=success,
        error_code=row["error_code"],
        label=row["label"],
    )
