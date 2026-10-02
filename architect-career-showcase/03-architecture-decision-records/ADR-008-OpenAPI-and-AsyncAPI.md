# ADR-008: OpenAPI and AsyncAPI

# Status

Accepted (OpenAPI runtime-aligned); AsyncAPI accepted as spec-first readiness

# Date

2026-08-10

# Context

ACOS needs synchronous AI APIs now and a documented event vocabulary for later choreography.

# Problem Statement

Without AsyncAPI, future event names drift; without OpenAPI, REST clients diverge.

# Decision Drivers

- Extensibility
- Maintainability
- Future AI Evolution

# Alternatives Considered

- **OpenAPI only** — Insufficient for planned cross-service lifecycle events.
- **Implement Kafka immediately with no AsyncAPI** — Rejected; broker ops without schema governance.
- **gRPC proto as sole IDL** — Rejected for current HTTP/Feign-first integration.

# Decision

Maintain OpenAPI 3.1 for AI REST (implemented by AI Platform) and AsyncAPI 3.0 for event channels (specified; broker bindings out of scope). Runtime eventing today uses in-process Spring events in Business, not AsyncAPI transport.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Clear REST SoT.
- Event names reserved early.

# Negative Consequences

- AsyncAPI can be mistaken for a live bus.

# Trade-offs

- Spec-first events ahead of infrastructure.

# Risks

- Documentation drift if channels change without consumers.

# Future Evolution

- Add broker bindings and publishers/consumers for indexed/failed processing events.

# References

- `architect-career-ai-contracts/openapi`
- `architect-career-ai-contracts/asyncapi`

## Interview Discussion

### Why was this approach selected?

OpenAPI unlocks today's Feign/FastAPI alignment; AsyncAPI prepares tomorrow without forcing Kafka now.

### When would you choose another approach?

Skip AsyncAPI until at least one cross-process event is scheduled for delivery.

### How would this decision change for 10x / 100x / 1000x users?

At 100x, event-driven indexing becomes likely; having channel names ready reduces redesign.

### Common Principal Architect interview questions

**Q1. Is AsyncAPI implemented?**

Spec yes; runtime bus no—say this explicitly.

### Common follow-up questions

- Which OpenAPI aggregate is canonical?

