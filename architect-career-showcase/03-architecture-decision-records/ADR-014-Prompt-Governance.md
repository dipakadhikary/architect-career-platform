# ADR-014: Prompt Governance

# Status

Accepted

# Date

2026-08-10

# Context

Prompts are production assets that require versioning and controlled rollout.

# Problem Statement

Hardcoded prompts in code prevent safe iteration, audit, and rollback.

# Decision Drivers

- Security
- Maintainability
- Extensibility
- Future AI Evolution

# Alternatives Considered

- **Hardcoded strings in services** — Rejected.
- **External CMS without version files** — Not implemented; file registry chosen.
- **Provider-side prompt hub only** — Rejected as sole approach; platform must own versions.

# Decision

Store versioned prompt files and manage approve/deprecate/rollback/audit via prompt governance service integrated into the enterprise pipeline.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Auditable prompt changes.
- Pipeline can require approved prompts when configured.

# Negative Consequences

- Governance defaults are relaxed locally (`prompt_require_approved=false`).

# Trade-offs

- Process overhead vs prompt safety.

# Risks

- Teams skip approval in production if misconfigured.

# Future Evolution

- Stricter prod defaults; UI for prompt ops.

# References

- `architect-career-ai-platform/prompts`
- `architect-career-ai-platform/app/infrastructure/enterprise/governance.py`

## Interview Discussion

### Why was this approach selected?

Prompts are controlled configuration, not incidental strings.

### When would you choose another approach?

Hardcode only in spikes.

### How would this decision change for 10x / 100x / 1000x users?

Large orgs need multi-env promotion of prompt versions like code.

### Common Principal Architect interview questions

**Q1. Where do prompts live?**

Versioned files under prompts/ plus governance metadata.

### Common follow-up questions

- How do you rollback a bad prompt?

