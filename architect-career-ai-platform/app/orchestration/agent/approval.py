"""Human approval records. Approval does not enable a high-risk tool."""

from __future__ import annotations

from app.orchestration.agent.models import ExecutionRecord, ExecutionStatus
from app.orchestration.agent.store import AgentStore
from app.shared.exceptions import AuthorizationError, ConflictError, NotFoundError


class ApprovalManager:
    def __init__(self, store: AgentStore) -> None:
        self._store = store

    def request(
        self,
        *,
        execution_id: str,
        step_id: str,
        tool_name: str,
        requester_id: str,
    ) -> None:
        self._store.request_approval(
            execution_id=execution_id,
            step_id=step_id,
            tool_name=tool_name,
            requester_id=requester_id,
        )

    def decide(
        self,
        execution_id: str,
        *,
        approver_id: str,
        approved: bool,
    ) -> ExecutionRecord:
        execution = self._store.get(execution_id)
        if execution is None:
            raise NotFoundError("Agent execution was not found")
        if execution.owner_id != approver_id:
            raise AuthorizationError("Only the requesting user can decide this action")
        if execution.status != ExecutionStatus.WAITING_FOR_APPROVAL:
            raise ConflictError("This execution is not waiting for approval")
        if not self._store.decide(execution_id, approver_id=approver_id, approved=approved):
            raise ConflictError("This execution is not waiting for approval")
        if not approved:
            return self._store.mark(
                execution_id,
                status=ExecutionStatus.CANCELLED,
                error_code="APPROVAL_REJECTED",
                answer="The proposed action was rejected.",
                approval_required=True,
                proposed_action=execution.proposed_action,
                finished=True,
            )
        return self._store.mark(
            execution_id,
            status=ExecutionStatus.FAILED,
            error_code="HIGH_RISK_NOT_ENABLED",
            answer="The action was approved. High-risk tools are not enabled.",
            approval_required=True,
            proposed_action=execution.proposed_action,
            finished=True,
        )
