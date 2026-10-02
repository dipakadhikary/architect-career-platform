# JPA / Hibernate

## Introduction

JPA is the persistence API; Hibernate is ACOS's provider mapping entities to PostgreSQL.

## Problem Statement

Raw JDBC everywhere slows domain modeling; uncontrolled SQL strings scatter.

## Why ACOS Uses This

Entities and repositories implement Business domains; Flyway owns schema. Tests use Testcontainers Postgres for dialect fidelity.

## Implementation Overview

Transactional services; avoid exposing entities as REST bodies; pagination where lists grow.

## Best Practices

- Migrations before entity changes.
- Watch N+1 (fetch joins / entity graphs).
- Keep Hikari pool sized to workload.

## Common Mistakes

- Open-in-view surprises.
- Bidirectional graphs serialized to JSON accidentally.

## Alternative Approaches

MyBatis; jOOQ; Spring JDBC.

## Trade-offs

Productive ORM vs leaky abstractions. ACOS accepts Hibernate for transactional domains.

## References to ACOS modules

- [04-business-platform/Persistence-Architecture.md](../../04-business-platform/Persistence-Architecture.md)

## Interview Questions

**Q:** Who owns schema?
**A:** Flyway migrations—not `ddl-auto` as source of truth.

**Q:** Shared DB for contexts?
**A:** Yes physically; logical module boundaries apply.

## Further Reading

- Hibernate ORM docs

