# OWASP LLM Risks (ACOS Lens)

## Introduction

OWASP LLM Top 10 catalogs risks like prompt injection, data leakage, supply chain, excessive agency.

## Problem Statement

Traditional AppSec checklists miss LLM-specific abuse.

## Why ACOS Uses This

ACOS maps: injection→guardrails; sensitive info→masking/PII flags; supply chain→pip-audit/bandit/Trivy (soft); excessive agency→MCP stubs (limited agency today); unbounded consumption→rate limits/bulkheads/cost hooks.

## Implementation Overview

ACOS maps: injection→guardrails; sensitive info→masking/PII flags; supply chain→pip-audit/bandit/Trivy (soft); excessive agency→MCP stubs (limited agency today); unbounded consumption→rate limits/bulkheads/cost hooks.

## Best Practices

Track residual risks explicitly; don't claim Top 10 “solved.”

## Common Mistakes

- Equating regex guardrails with full Top 10 compliance.
- Enabling tools/MCP without allowlists.

## Alternative Approaches

Ignore LLM-specific risks; outsource entirely to one vendor's safety.

## Trade-offs

Framework awareness vs incomplete controls—honesty is the ACOS stance.

## References to ACOS modules

- AI Security + Guardrails docs chapters 06/08

## Interview Questions

**Q:** Which risk is reduced by “Web never calls AI”?
**A:** Key exposure / direct prompt abuse from browser.

**Q:** Soft CI scans?
**A:** pip-audit/bandit/Trivy continue-on-error today.

## Further Reading

- OWASP Top 10 for LLM Applications

