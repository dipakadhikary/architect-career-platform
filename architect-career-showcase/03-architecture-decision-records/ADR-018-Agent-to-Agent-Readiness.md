# ADR-018: Agent-to-Agent Readiness

# Status

Accepted (extension-point / stub)

# Date

2026-08-10

# Context

Multi-agent collaboration may be required later for distributed agent topologies.

# Problem Statement

Hardwiring single-agent flows makes future delegation difficult.

# Decision Drivers

- Future AI Evolution
- Extensibility

# Alternatives Considered

- **No A2A abstractions** — Higher future rewrite cost.
- **Networked A2A protocol now** — Rejected; no distributed agents in production path.

# Decision

Provide in-memory agent registry and local delegation ports without network implementation.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Delegation seam documented and testable locally.

# Negative Consequences

- Not a distributed agent mesh.

# Trade-offs

- Abstraction without runtime distribution.

# Risks

- Marketing language claiming multi-agent networking.

# Future Evolution

- Network transport, authn between agents, shared context stores.

# References

- `architect-career-ai-platform/app/infrastructure/enterprise/a2a.py`

## Interview Discussion

### Why was this approach selected?

Local A2A ports prove the control API before networking.

### When would you choose another approach?

Avoid A2A until single-agent workflows are stable.

### How would this decision change for 10x / 100x / 1000x users?

At scale, A2A needs identity, quotas, and backpressure.

### Common Principal Architect interview questions

**Q1. Do you have multi-agent production traffic?**

No—local stubs only.

### Common follow-up questions

- How does registry discovery work today?

