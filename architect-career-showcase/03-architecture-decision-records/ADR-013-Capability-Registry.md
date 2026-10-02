# ADR-013: Capability Registry

# Status

Accepted

# Date

2026-08-10

# Context

AI Platform exposes many workflows (resume, quiz, retrieval, etc.) that must be discoverable and invocable uniformly.

# Problem Statement

Hardcoding workflow dispatch in routers duplicates concerns and blocks extension.

# Decision Drivers

- Extensibility
- Maintainability
- Future AI Evolution

# Alternatives Considered

- **Switch statements in each endpoint** — Rejected; poor open/closed behavior.
- **One mega-prompt for all tasks** — Rejected; weak evaluation and routing.

# Decision

Register agentic capabilities/workflows/graphs in factories and execute via orchestration services and pipeline facades.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Open for extension.
- Uniform enterprise pipeline application.

# Negative Consequences

- Registry/factory indirection.

# Trade-offs

- More wiring files for clearer extension points.

# Risks

- Unused registered graphs confuse newcomers.

# Future Evolution

- Runtime capability discovery API if product needs it.

# References

- `architect-career-ai-platform/app/infrastructure/agentic/factory.py`
- `architect-career-ai-platform/app/infrastructure/agentic/workflows`

## Interview Discussion

### Why was this approach selected?

Registries encode the open/closed principle for AI workflows.

### When would you choose another approach?

Direct calls are fine for a single workflow prototype.

### How would this decision change for 10x / 100x / 1000x users?

At scale, registries feed routers, policy allowlists, and cost attribution.

### Common Principal Architect interview questions

**Q1. Name registered workflows.**

resume, interview, quiz, portfolio_review, ...

### Common follow-up questions

- How does chat map to capabilities?

