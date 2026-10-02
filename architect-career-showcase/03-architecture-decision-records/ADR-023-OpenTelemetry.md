# ADR-023: OpenTelemetry

# Status

Accepted (partial wiring)

# Date

2026-08-10

# Context

AI Platform aims for vendor-neutral tracing.

# Problem Statement

Logs alone are insufficient for multi-hop AI latency analysis.

# Decision Drivers

- Operational Complexity
- Extensibility

# Alternatives Considered

- **No tracing** — Rejected as end state.
- **Vendor agent only** — Less portable.
- **Full auto-instrumentation everywhere immediately** — Deps present but not fully applied in app code.

# Decision

Bootstrap OTel TracerProvider + OTLP exporter when enabled in AI Platform. Application span instrumentation and Compose collector service are incomplete—documented honestly as partial.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Foundation for portable traces.
- Can be enabled via settings.

# Negative Consequences

- `get_tracer` not broadly used; collector service referenced but not defined in Compose.

# Trade-offs

- Early exporter bootstrap vs incomplete span coverage.

# Risks

- Assuming traces exist for every request today.

# Future Evolution

- Instrument FastAPI/httpx; add collector; propagate W3C trace context end-to-end.

# References

- `architect-career-ai-platform/app/infrastructure/observability/otel.py`
- `architect-career-ai-platform/docker-compose.yml`

## Interview Discussion

### Why was this approach selected?

OTel is the right direction; implementation is staged.

### When would you choose another approach?

Delay OTel only if a single APM is mandated short-term.

### How would this decision change for 10x / 100x / 1000x users?

At scale, tail sampling and backend cardinality controls matter more than libraries.

### Common Principal Architect interview questions

**Q1. Are spans emitted per AI handler today?**

Not fully—provider is bootstrapped; app spans incomplete.

### Common follow-up questions

- How would you propagate context from Business?

