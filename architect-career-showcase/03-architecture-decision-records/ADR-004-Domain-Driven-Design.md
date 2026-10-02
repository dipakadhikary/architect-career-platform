# ADR-004: Domain-Driven Design

# Status

Accepted (pragmatic / partial)

# Date

2026-08-10

# Context

Career tracking requires explicit status transitions and domain language across applications, interviews, and offers.

# Problem Statement

Treating career status as free-form strings causes illegal transitions and weak auditability.

# Decision Drivers

- Maintainability
- Security
- Extensibility

# Alternatives Considered

- **Anemic CRUD only** — Rejected for career status lifecycle.
- **Full DDD tactical patterns everywhere (aggregates/repositories pure domain)** — Not implemented across all modules; JPA entities remain the persistence model.
- **Process managers/sagas microservices** — Rejected as premature.

# Decision

Use domain packages and ubiquitous language (auth/knowledge/learning/portfolio/career). Implement an explicit application status state machine and domain events in Business Platform. Do not claim full DDD hexagonal purity.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Career transitions are enforceable and auditable.
- Package language matches product language.

# Negative Consequences

- Inconsistent DDD depth across modules.
- Analytics domain remains a stub.

# Trade-offs

- Pragmatic DDD beats ceremonial DDD for current team size.

# Risks

- Calling it 'full DDD' in interviews oversells the implementation.

# Future Evolution

- Strengthen aggregates/invariants where rules grow; implement analytics domain.

# References

- `architect-career-operating-system/.../career/state`
- `architect-career-operating-system/.../*/event`

## Interview Discussion

### Why was this approach selected?

DDD is applied where business rules hurt if ignored (career state).

### When would you choose another approach?

Skip tactical DDD for trivial CRUD reference data.

### How would this decision change for 10x / 100x / 1000x users?

Larger scale increases need for clearer aggregate boundaries and eventual consistency patterns.

### Common Principal Architect interview questions

**Q1. Where is the ubiquitous language?**

Package and API names: applications, interviews, offers, milestones, notes.

### Common follow-up questions

- Show the state machine.
- How are domain events published?

