# Bounded Contexts

## Introduction

A bounded context is an explicit linguistic and model boundary. The same word (e.g., “skill”) can mean different things in Portfolio vs Learning.

## Problem Statement

One enterprise-wide data model collapses nuance and creates brittle coupling.

## Why ACOS Uses This

ACOS separates Knowledge, Learning, Career, Portfolio (and Auth) as domains inside the Business modular monolith, with AI capabilities mirroring those nouns on `/api/v1/ai/...` without owning business persistence.

## Implementation Overview

Each domain owns tables/migrations relevant to it; Integration is a context for outbound AI. Web feature folders mirror domains for UI cohesion.

## Best Practices

- Name APIs with context prefixes.
- Integrate contexts via application services or events—not shared mutable entities.

## Common Mistakes

- One `Skill` entity reused everywhere with conflicting invariants.
- Letting AI Platform become the system of record for career applications.

## Alternative Approaches

Microservices per context; single shared schema without module rules.

## Trade-offs

Clear language vs mapping overhead between contexts. ACOS shares one database physically while aiming for logical separation.

## References to ACOS modules

- [04-business-platform](../../04-business-platform/) domain docs
- [02-system-design](../../02-system-design/) context views

## Interview Questions

**Q:** Is one Postgres incompatible with bounded contexts?
**A:** No—logical boundaries can precede physical DB split.

## Further Reading

- Evans, *Domain-Driven Design*
