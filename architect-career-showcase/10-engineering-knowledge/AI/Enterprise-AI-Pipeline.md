# Enterprise AI Pipeline

## Introduction

A mandatory middleware pipeline wraps AI executions with cross-cutting concerns: policy, guardrails, routing, cache, cost, audit—before domain handlers run.

## Problem Statement

Sprinkling checks ad hoc in each route guarantees missed controls.

## Why ACOS Uses This

ACOS AI execution pipeline composes enterprise ports around handlers; Business still has its own Feign ACL for product-side concerns.

```mermaid
flowchart LR
  Req[Request] --> Pol[Policy]
  Pol --> G[Guardrails]
  G --> Cache[Cache]
  Cache --> Route[Router]
  Route --> H[Handler]
  H --> Audit[Audit/Cost]
```

## Implementation Overview

ACOS AI execution pipeline composes enterprise ports around handlers; Business still has its own Feign ACL for product-side concerns.

```mermaid
flowchart LR
  Req[Request] --> Pol[Policy]
  Pol --> G[Guardrails]
  G --> Cache[Cache]
  Cache --> Route[Router]
  Route --> H[Handler]
  H --> Audit[Audit/Cost]
```

## Best Practices

- New route must go through pipeline.
- Feature flags default safe.
- Emit metrics per stage.

## Common Mistakes

- Optional pipeline “for speed” in prod.
- Silent catch-all swallowing safety errors.

## Alternative Approaches

API gateway plugins only; service mesh; per-handler decorators.

## Trade-offs

Consistent governance vs latency hops. Acceptable for AI cost/risk profile.

## References to ACOS modules

- [06-ai-platform](../../06-ai-platform/) enterprise/pipeline docs

## Interview Questions

**Q:** Does Business duplicate the pipeline?
**A:** Partially—Resilience/feature flag at Feign; safety/model concerns on AI.

**Q:** MCP in pipeline?
**A:** Stubs today—not networked tools.

## Further Reading

- Platform engineering for AI control planes

