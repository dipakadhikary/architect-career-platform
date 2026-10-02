# ADR-020: PostgreSQL

# Status

Accepted

# Date

2026-08-10

# Context

Career/learning/portfolio/knowledge require relational integrity and migrations.

# Problem Statement

Document stores would weaken relational constraints across applications, interviews, and offers.

# Decision Drivers

- Maintainability
- Security
- Performance
- Cost

# Alternatives Considered

- **MongoDB as primary SoR** — Rejected for relational career data.
- **Separate DB per domain** — Rejected with modular monolith stage.
- **SQLite** — Insufficient for multi-user local/prod paths.

# Decision

Use PostgreSQL as Business Platform system of record with Flyway migrations and JPA validate mode; schema `acos`.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Strong consistency for product domains.
- Mature ops ecosystem.
- Testcontainers support.

# Negative Consequences

- Local Compose credentials can diverge from application-local defaults.

# Trade-offs

- Relational modeling effort vs integrity.

# Risks

- Using PostgreSQL for vectors without a conscious ADR (not current default path).

# Future Evolution

- Read replicas; partitioning strategies for hot tables.

# References

- `architect-career-operating-system/src/main/resources/db/migration`
- `architect-career-operating-system/infrastructure/docker/docker-compose.yml`

## Interview Discussion

### Why was this approach selected?

Transactional career data belongs in a relational SoR.

### When would you choose another approach?

Polyglot persistence later for specialized access patterns—not first.

### How would this decision change for 10x / 100x / 1000x users?

Scale with replicas, pooling, partitioning; keep AI vectors elsewhere.

### Common Principal Architect interview questions

**Q1. Who migrates schema?**

Flyway in Business Platform.

### Common follow-up questions

- Why ddl-auto validate?

