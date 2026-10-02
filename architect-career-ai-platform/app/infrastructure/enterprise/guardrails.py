"""Input/output guardrails: validation, PII, injection, jailbreak, moderation, redaction."""

from __future__ import annotations

import re

from app.intelligence.enterprise.guardrails.ports import GuardrailsPort
from app.intelligence.enterprise.models import GuardrailFinding, GuardrailResult, GuardrailVerdict
from app.shared.config.settings import AppSettings

_PII_PATTERNS = [
    ("email", re.compile(r"[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+")),
    ("phone", re.compile(r"\b(?:\+?\d{1,3}[-.\s]?)?(?:\(?\d{3}\)?[-.\s]?){2}\d{4}\b")),
    ("ssn", re.compile(r"\b\d{3}-\d{2}-\d{4}\b")),
]

_INJECTION_PATTERNS = [
    re.compile(r"ignore\s+(all\s+)?previous\s+instructions", re.I),
    re.compile(r"system\s*prompt", re.I),
    re.compile(r"jailbreak", re.I),
    re.compile(r"do\s+anything\s+now", re.I),
    re.compile(r"<\|?system\|>?", re.I),
]

_MODERATION_PATTERNS = [
    re.compile(r"\b(bomb making|credit card dump|child exploitation)\b", re.I),
]


class HeuristicGuardrails(GuardrailsPort):
    def __init__(self, settings: AppSettings) -> None:
        self._settings = settings

    async def validate_input(self, text: str, *, capability: str) -> GuardrailResult:
        return self._validate(text, stage="input", capability=capability)

    async def validate_output(self, text: str, *, capability: str) -> GuardrailResult:
        return self._validate(text, stage="output", capability=capability)

    def _validate(self, text: str, *, stage: str, capability: str) -> GuardrailResult:
        if not self._settings.guardrails_enabled:
            return GuardrailResult(verdict=GuardrailVerdict.ALLOW, text=text)

        findings: list[GuardrailFinding] = []
        redacted = text

        if not text or not text.strip():
            findings.append(
                GuardrailFinding(rule="input_validation", severity="high", message="Empty input")
            )
            return GuardrailResult(verdict=GuardrailVerdict.BLOCK, text=text, findings=findings)

        if len(text) > self._settings.guardrails_max_input_chars and stage == "input":
            findings.append(
                GuardrailFinding(
                    rule="input_validation",
                    severity="high",
                    message="Input exceeds maximum allowed length",
                )
            )
            return GuardrailResult(verdict=GuardrailVerdict.BLOCK, text=text, findings=findings)

        for pattern in _INJECTION_PATTERNS:
            if pattern.search(text):
                findings.append(
                    GuardrailFinding(
                        rule="prompt_injection",
                        severity="critical",
                        message="Potential prompt injection or jailbreak detected",
                    )
                )
                return GuardrailResult(verdict=GuardrailVerdict.BLOCK, text=text, findings=findings)

        for pattern in _MODERATION_PATTERNS:
            if pattern.search(text):
                findings.append(
                    GuardrailFinding(
                        rule="content_moderation",
                        severity="critical",
                        message="Disallowed content detected",
                    )
                )
                return GuardrailResult(verdict=GuardrailVerdict.BLOCK, text=text, findings=findings)

        redactions: list[str] = []
        if self._settings.guardrails_pii_redaction_enabled:
            for label, pattern in _PII_PATTERNS:
                if pattern.search(redacted):
                    findings.append(
                        GuardrailFinding(
                            rule="pii_detection",
                            severity="medium",
                            message=f"PII detected: {label}",
                        )
                    )
                    redactions.append(label)
                    redacted = pattern.sub(f"[REDACTED_{label.upper()}]", redacted)

        verdict = GuardrailVerdict.REDACT if redactions else GuardrailVerdict.ALLOW
        return GuardrailResult(
            verdict=verdict,
            text=redacted,
            findings=findings,
            redactions=redactions,
        )
