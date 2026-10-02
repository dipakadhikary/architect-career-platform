# DTO Pattern

## Introduction

Data Transfer Objects carry data across process/layer boundaries without exposing internal entities.

## Problem Statement

Serializing JPA entities creates lazy-load surprises and couples API to schema.

## Why ACOS Uses This

Business REST/AI Feign DTOs; Web API types; generated contracts models in Python/Java/TS.

## Implementation Overview

Business REST/AI Feign DTOs; Web API types; generated contracts models in Python/Java/TS.

## Best Practices

Map explicitly at boundaries; don't reuse entities as DTOs; generate where contracts exist.

## Common Mistakes

- Bidirectional entity graphs as JSON.
- Divergent hand DTOs vs OpenAPI.

## Alternative Approaches

JSON objects untyped; GraphQL types only; shared mutable models.

## Trade-offs

Mapping cost vs stability. Contracts reduce hand-mapping long-term.

## References to ACOS modules

- Chapters 04/05/07

## Interview Questions

**Q:** Why not return entities?
**A:** Encapsulation, lazy loading, versioning.

**Q:** Generated vs hand DTOs?
**A:** AI Python generated; Business Feign largely hand-aligned today.

## Further Reading

- Fowler DTO

