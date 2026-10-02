# ADR-015: Model Router

# Status

Accepted

# Date

2026-08-10

# Context

Multiple LLM providers may be enabled with different cost/latency/quality profiles.

# Problem Statement

Hardcoding a provider in each workflow prevents policy-driven selection and failover.

# Decision Drivers

- Cost
- Performance
- Extensibility
- Operational Complexity

# Alternatives Considered

- **Single provider forever** — Rejected for enterprise portability.
- **Manual provider argument on every API call** — Rejected; pushes policy to callers.
- **Always cheapest model** — Too naive; capability/context constraints exist.

# Decision

Introduce `ModelRouterPort` with configurable policy router (cost/latency/quality/preferred/capability/context/availability/fallback) selecting among enabled providers.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Policy-driven selection.
- Fallback when no providers enabled.

# Negative Consequences

- Routing heuristics are config-driven, not learned.

# Trade-offs

- Simplicity of one provider vs portability.

# Risks

- Misconfigured allowlists block all routes.

# Future Evolution

- Latency-aware telemetry-based routing.

# References

- `architect-career-ai-platform/app/intelligence/agentic/router`
- `architect-career-ai-platform/app/infrastructure/enterprise/router.py`

## Interview Discussion

### Why was this approach selected?

Routing encodes business policy at the platform edge.

### When would you choose another approach?

Skip router until a second provider is real.

### How would this decision change for 10x / 100x / 1000x users?

At scale, routing must incorporate budgets, tenant tiers, and provider SLOs.

### Common Principal Architect interview questions

**Q1. What if no provider is enabled?**

Fallback/extractive path from settings.

### Common follow-up questions

- How do execution policies interact with routing?

