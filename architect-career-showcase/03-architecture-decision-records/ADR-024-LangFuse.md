# ADR-024: LangFuse

# Status

Accepted (optional)

# Date

2026-08-10

# Context

LLM evaluation/tracing needs a specialized loop beyond infra metrics.

# Problem Statement

Prometheus alone does not capture prompt/version qualitative traces well.

# Decision Drivers

- Future AI Evolution
- Maintainability
- Cost

# Alternatives Considered

- **No LLM observability** — Rejected for platform ambitions.
- **Build internal prompt trace UI now** — Too costly.
- **Only OpenTelemetry GenAI spans** — Complementary; LangFuse chosen for eval UX hooks.

# Decision

Integrate optional LangFuse adapter; enable only with keys/flags. Evaluators call `.trace()` when available; disabled by default.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Optional SaaS/self-host hook.
- Tied into enterprise/agentic evaluation paths.

# Negative Consequences

- Another vendor to operate when enabled.
- Not required for core CRUD.

# Trade-offs

- Capability vs dependency burden.

# Risks

- Sending sensitive prompts to SaaS without policy review.

# Future Evolution

- Standardize trace schemas; dual-write OTel+LangFuse carefully.

# References

- `architect-career-ai-platform/app/infrastructure/observability/langfuse_adapter.py`
- `architect-career-ai-platform/app/infrastructure/enterprise/evaluation.py`

## Interview Discussion

### Why was this approach selected?

LLM ops needs prompt-centric tooling; LangFuse is optional by design.

### When would you choose another approach?

Skip until evaluation workflows are actively used.

### How would this decision change for 10x / 100x / 1000x users?

At scale, sampling and redaction policies are mandatory.

### Common Principal Architect interview questions

**Q1. Default enabled?**

No.

### Common follow-up questions

- Where are traces flushed?

