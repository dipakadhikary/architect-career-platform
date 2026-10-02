"""Policy engine ports."""

from __future__ import annotations

from abc import ABC, abstractmethod
from typing import Any

from app.intelligence.enterprise.models import PolicyDecision


class PolicyEnginePort(ABC):
    @abstractmethod
    def evaluate(self, *, policy_type: str, context: dict[str, Any]) -> PolicyDecision:
        raise NotImplementedError
