# ADR-022: Observability

# Status

Accepted

# Date

2026-08-10

# Context

Distributed Web→Business→AI flows need correlation and health/metrics for operations and demos.

# Problem Statement

Without shared correlation and health signals, failures across Feign boundaries are hard to diagnose.

# Decision Drivers

- Operational Complexity
- Maintainability
- Security

# Alternatives Considered

- **Logs only without correlation** — Rejected.
- **Vendor APM only** — Possible later; open metrics chosen as baseline.
- **No AI health in Business readiness** — Rejected; `aiPlatform` indicator exists.

# Decision

Propagate `X-Correlation-Id` (Web/Business/AI). Business exposes Actuator health/info/metrics/prometheus. AI exposes liveness/readiness/metrics and structured logs; enterprise pipeline emits audit/metrics.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Cross-system correlation hooks.
- Readiness includes DB and AI dependency signal.

# Negative Consequences

- Some AI cost/token metrics remain placeholder-oriented.
- Metrics endpoints must be hardened in prod.

# Trade-offs

- More endpoints vs operability.

# Risks

- Public swagger/actuator surfaces if misconfigured in production.

# Future Evolution

- Unified dashboards; richer RED/USE metrics for AI routes.

# References

- `architect-career-operating-system/.../CorrelationIdFilter.java`
- `architect-career-ai-platform/app/api/middleware/request_context.py`
- `architect-career-ai-platform/app/shared/observability/metrics.py`

## Interview Discussion

### Why was this approach selected?

Correlation first, then metrics/health—baseline SRE needs.

### When would you choose another approach?

Add full APM when org standard mandates it.

### How would this decision change for 10x / 100x / 1000x users?

At scale, sampling, exemplars, and SLO burn-rate alerts become mandatory.

### Common Principal Architect interview questions

**Q1. What is on Business readiness?**

Includes db and aiPlatform groups.

### Common follow-up questions

- How does Feign propagate correlation?

