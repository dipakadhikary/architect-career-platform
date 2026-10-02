# ADR-016: Guardrails

# Status

Accepted

# Date

2026-08-10

# Context

AI inputs/outputs can contain PII, injection attempts, or disallowed content.

# Problem Statement

Unfiltered prompts increase security and compliance risk; per-endpoint checks drift.

# Decision Drivers

- Security
- Maintainability
- Operational Complexity

# Alternatives Considered

- **No guardrails** — Rejected.
- **Only external moderation APIs** — Useful later; local heuristics provide baseline without mandatory SaaS.
- **Guardrails only in Business Platform** — Rejected; AI edge must defend itself.

# Decision

Run heuristic guardrails (validation, PII, injection/jailbreak, moderation, redaction/masking) inside the enterprise pipeline for AI requests.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Consistent safety checks.
- Redaction integrated with sanitize/mask stages.

# Negative Consequences

- Heuristics are limited vs advanced classifiers.

# Trade-offs

- False positives/negatives vs zero protection.

# Risks

- Over-reliance on regexes for compliance claims.

# Future Evolution

- Pluggable moderation providers; stronger hallucination detectors.

# References

- `architect-career-ai-platform/app/infrastructure/enterprise/guardrails.py`
- `architect-career-ai-platform/app/orchestration/enterprise/pipeline.py`

## Interview Discussion

### Why was this approach selected?

Central pipeline guardrails beat scattered if-statements.

### When would you choose another approach?

Add vendor moderation when risk model demands it.

### How would this decision change for 10x / 100x / 1000x users?

At scale, push coarse filters to gateway and keep model-specific checks in pipeline.

### Common Principal Architect interview questions

**Q1. Do guardrails block empty structured outputs?**

Output checks skip empty non-text answers for structured workflows.

### Common follow-up questions

- How is PII redacted?

