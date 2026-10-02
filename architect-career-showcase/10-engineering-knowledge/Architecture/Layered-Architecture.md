# Layered Architecture

## Introduction

Classic layers separate transport, use-case orchestration, domain rules, and infrastructure adapters so frameworks do not own business meaning.

## Problem Statement

Mixing controllers, SQL, and LLM calls in one class makes testing and evolution expensive.

## Why ACOS Uses This

Business Platform uses controller → service/application → domain → repository/integration style packaging. AI Platform uses ports/adapters and an enterprise pipeline around handlers.

## Implementation Overview

HTTP DTOs stay at the edge; domain services enforce career/learning rules; Feign/JPA live in integration/persistence. AI keeps FastAPI routes thin over pipeline + capability services.

## Best Practices

- Depend inward (domain should not import Web/Feign types).
- Keep transactions at application boundaries.
- Test domain without Spring/FastAPI where practical.

## Common Mistakes

- Anemic domain with all logic in controllers.
- Leaking JPA entities through REST.
- Calling OpenAI from a JPA entity listener without an ACL.

## Alternative Approaches

Hexagonal/clean architecture; vertical slice architecture; feature packages without strict layers.

## Trade-offs

Clear teaching model vs ceremony for tiny features. ACOS uses layers as a readability tool, not dogma.

## References to ACOS modules

- [04-business-platform](../../04-business-platform/) layer docs
- [06-ai-platform/Layered-Architecture.md](../../06-ai-platform/Layered-Architecture.md)

## Interview Questions

**Q:** Where should Resilience4j live?
**A:** Integration/anti-corruption edge (`AiPlatformInvoker`), not inside domain entities.

## Further Reading

- Fowler, *Patterns of Enterprise Application Architecture*
