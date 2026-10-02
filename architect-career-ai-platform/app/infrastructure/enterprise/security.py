"""Prompt sanitization and sensitive data masking."""

from __future__ import annotations

import re
from typing import ClassVar

from app.intelligence.enterprise.security.ports import DataMaskerPort, PromptSanitizerPort


class DefaultPromptSanitizer(PromptSanitizerPort):
    _CONTROL = re.compile(r"[\x00-\x08\x0b\x0c\x0e-\x1f]")

    def sanitize(self, text: str) -> str:
        cleaned = self._CONTROL.sub("", text)
        return cleaned.replace("```", "'''").strip()


class RegexDataMasker(DataMaskerPort):
    _PATTERNS: ClassVar[list[re.Pattern[str]]] = [
        re.compile(r"[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+"),
        re.compile(r"\b\d{3}-\d{2}-\d{4}\b"),
        re.compile(r"\b(?:sk|rk)-[A-Za-z0-9]{16,}\b"),
    ]

    def mask(self, text: str) -> str:
        masked = text
        for pattern in self._PATTERNS:
            masked = pattern.sub("[MASKED]", masked)
        return masked
