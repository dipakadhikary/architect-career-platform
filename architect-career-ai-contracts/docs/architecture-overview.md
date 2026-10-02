# Architecture overview

## Purpose

`architect-career-ai-contracts` is the only source of truth for communication involving the ACOS AI Platform.

```text
React / Mobile / CLI
        │
        ▼
Spring Boot Business Platform  ←── Feign / REST ──→  Python AI Platform
        │                                                 │
        └────────────── Events (AsyncAPI) ────────────────┘
```

## Principles

1. **Contract first** — implementations are generated from contracts, never the reverse.
2. **No duplicated DTOs** — shared schemas live once under `openapi/common` and `asyncapi/common`.
3. **Sync + async** — REST for request/response capabilities; events for eventual consistency and workflows.
4. **Language agnostic** — Java, Python, and TypeScript are equal consumers of the same contracts.
5. **Transport agnostic events** — AsyncAPI defines messages and channels, not Kafka topics implementation details.

## Boundaries

| In scope           | Out of scope                        |
| ------------------ | ----------------------------------- |
| OpenAPI / AsyncAPI | Business rules                      |
| Shared schemas     | Spring/Python/React implementations |
| Generators + CI    | Broker provisioning                 |
| Versioning policy  | Model-provider SDK wrappers         |
