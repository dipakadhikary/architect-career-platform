# ADR-007: Contract-First AI

# Status

Accepted

# Date

2026-08-10

# Context

Java Feign clients and Python handlers must agree on AI request/response shapes.

# Problem Statement

Code-first divergent DTOs across languages cause silent breakage and weak governance.

# Decision Drivers

- Maintainability
- Extensibility
- Developer Experience
- Future AI Evolution

# Alternatives Considered

- **Share a Java jar into Python** — Rejected; wrong coupling direction.
- **Informal JSON examples only** — Rejected; not enforceable.
- **GraphQL unified API** — Not chosen; REST contracts already match Feign usage.

# Decision

Own AI HTTP schemas in `architect-career-ai-contracts` (OpenAPI). Generate Python models consumed by AI Platform; align Business Feign paths to the same `/api/v1/ai/**` surface.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Single SoT for AI HTTP.
- CI validation/generation exists for contracts.

# Negative Consequences

- Business/Web still partially hand-maintain clients/DTOs.

# Trade-offs

- Generator friction vs drift prevention.

# Risks

- Hand DTOs can lag specs without Business-side contract gates.

# Future Evolution

- Publish Java/TS SDKs and make Business/Web depend on artifacts.

# References

- `architect-career-ai-contracts`
- `architect-career-ai-platform/third_party/acos_ai_contracts`

## Interview Discussion

### Why was this approach selected?

Contracts are the only safe cross-language API truth.

### When would you choose another approach?

Code-first may be fine inside one language monolith.

### How would this decision change for 10x / 100x / 1000x users?

At scale, contract compatibility becomes a release gate across many consumers.

### Common Principal Architect interview questions

**Q1. Consumer-driven contracts?**

Possible later; currently provider-owned OpenAPI aggregate.

### Common follow-up questions

- How do you sync Python models?

