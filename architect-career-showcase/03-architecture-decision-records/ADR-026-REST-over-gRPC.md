# ADR-026: REST over gRPC

# Status

Accepted

# Date

2026-08-10

# Context

Browser, Spring Feign, and FastAPI must interoperate with broad tooling support.

# Problem Statement

gRPC-first would complicate browser consumption and Feign-centric Business integration.

# Decision Drivers

- Developer Experience
- Extensibility
- Maintainability

# Alternatives Considered

- **gRPC between Business and AI** — Better perf potentially; worse browser/tooling fit today.
- **GraphQL BFF** — Not selected; REST resources already match domains.
- **SOAP** — Rejected.

# Decision

Use versioned REST/JSON for Web↔Business and Business↔AI. Document AI REST with OpenAPI. Keep gRPC as a non-chosen alternative for internal AI mesh unless needs change.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Universal HTTP debugging.
- Natural OpenAPI/Feign alignment.
- Browser-friendly via Business.

# Negative Consequences

- Less efficient than gRPC for chattery internal RPC.
- Streaming patterns less native than gRPC streams.

# Trade-offs

- Ubiquity over peak efficiency.

# Risks

- Chatty over-fetch without careful API design.

# Future Evolution

- Consider gRPC for internal AI worker protocols if latency profiles demand.

# References

- `architect-career-ai-contracts/openapi`
- `architect-career-operating-system/.../integration/client`
- `architect-career-web/src/features`

## Interview Discussion

### Why was this approach selected?

REST matches current consumers and contract tooling.

### When would you choose another approach?

Choose gRPC for service meshes with codegen-heavy internal APIs and no browser clients.

### How would this decision change for 10x / 100x / 1000x users?

At extreme internal QPS, hybrid REST edge + gRPC interior can appear.

### Common Principal Architect interview questions

**Q1. Why Feign loves REST?**

HTTP interfaces map cleanly to declarative clients.

### Common follow-up questions

- Would you expose gRPC to Web?

