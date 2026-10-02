"""Unit tests for prompt governance."""

from __future__ import annotations

import pytest
from app.infrastructure.enterprise.factory import build_prompt_governance
from app.intelligence.enterprise.models import PromptLifecycleStatus
from app.shared.config.settings import AppSettings
from app.shared.exceptions import ValidationFailedError


@pytest.mark.asyncio
async def test_bootstrap_approves_chat_prompt(settings: AppSettings) -> None:
    governance = build_prompt_governance(settings)
    rendered = await governance.resolve(
        "chat",
        {"question": "hi", "context": "", "history": ""},
        require_approved=True,
    )
    assert rendered.version == "v1"
    assert rendered.system or rendered.user
    history = governance.audit_history("chat")
    assert any(entry.action == "approve" for entry in history)


@pytest.mark.asyncio
async def test_deprecate_blocks_when_require_approved(settings: AppSettings) -> None:
    governance = build_prompt_governance(settings)
    governance.deprecate("chat", "v1", actor="test")
    with pytest.raises(ValidationFailedError):
        await governance.resolve(
            "chat",
            {"question": "hi", "context": "", "history": ""},
            version="v1",
            require_approved=True,
        )


@pytest.mark.asyncio
async def test_rollback_restores_active(settings: AppSettings) -> None:
    governance = build_prompt_governance(settings)
    rolled = governance.rollback("chat", to_version="v1", actor="test")
    assert rolled.status == PromptLifecycleStatus.APPROVED
