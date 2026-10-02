# Anti-Corruption Layer (ACL)

## Introduction

An ACL translates and isolates an external model (AI Platform HTTP) so core domain models are not polluted by foreign DTOs, error codes, or latency semantics.

## Problem Statement

Directly mapping AI JSON into domain entities couples product rules to prompt/RAG APIs and makes provider outages look like domain failures.

## Why ACOS Uses This

ACOS Business Integration module owns Feign clients, gateways/facades, DTO mapping, feature flag `ai.platform.enabled`, and Resilience4j via `AiPlatformInvoker`.

## Implementation Overview

Domain services call facades; when AI is disabled, mocks/fallbacks keep product flows usable. Correlation and API-key headers are added outbound without leaking into domain types.

## Best Practices

- Map at the boundary; never persist raw AI payloads as source of truth without intent.
- Fail soft for non-critical AI assist paths.
- Align paths with OpenAPI contracts.

## Common Mistakes

- Sharing one DTO JAR across AI and domain packages.
- Letting controllers call Feign directly without policy/metrics.

## Alternative Approaches

Shared kernel DTOs; BFF-only translation; sync domain events instead of sync HTTP.

## Trade-offs

Extra mapping code vs long-term independence. ACOS still hand-aligns Feign vs generated SDK—an interim ACL smell to retire.

## References to ACOS modules

- [04-business-platform/Integration-Architecture.md](../../04-business-platform/Integration-Architecture.md)
- [08-engineering-excellence/Security/AI-Security.md](../../08-engineering-excellence/Security/AI-Security.md)

## Interview Questions

**Q:** What happens when AI is down?
**A:** Flag/resilience path degrades assist features; CRUD continues.

## Further Reading

- Domain-Driven Design (Evans) — ACL chapter
