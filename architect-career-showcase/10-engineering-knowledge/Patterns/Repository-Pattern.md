# Repository Pattern

## Introduction

Repositories mediate between domain and data mapping, speaking in aggregates/entities rather than SQL.

## Problem Statement

Spreading EntityManager queries through services duplicates and leaks persistence.

## Why ACOS Uses This

Spring Data JPA repositories back Business domains; tests target repositories with Testcontainers.

## Implementation Overview

Spring Data JPA repositories back Business domains; tests target repositories with Testcontainers.

## Best Practices

Keep query methods intention-revealing; avoid exposing `Page` plumbing into domain core unnecessarily.

## Common Mistakes

- Repositories calling Feign.
- Returning projections that dictate UI accidentally across layers.

## Alternative Approaches

Active record; CQRS read models; raw JDBC templates.

## Trade-offs

Testability vs hidden query costs (N+1).

## References to ACOS modules

- Persistence architecture chapter 04

## Interview Questions

**Q:** Is Spring Data a pure DDD repository?
**A:** Pragmatic approximation—still valuable.

**Q:** AI vector “repository”?
**A:** Ports/adapters over Qdrant, not JPA.

## Further Reading

- Fowler Repository pattern

