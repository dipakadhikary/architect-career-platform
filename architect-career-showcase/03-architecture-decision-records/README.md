# Architecture Decision Records (ADR) — Index

Chapter 03 is the **Architecture Knowledge Base** for ACOS: every important decision that is reflected in the current implementation is captured as an Architecture Decision Record.

Audience: Principal Architects, Enterprise Architects, Staff Engineers, Engineering Managers, and interviewers.

Integrity rule: if a choice is not visible in code, config, contracts, or CI, it is **not** an Accepted ADR. See [Future-Decisions.md](Future-Decisions.md) for intentional non-implementations and roadmap choices.

---

## How to read ADRs

1. Start with [ADR-001 Overall Architecture](ADR-001-Overall-Architecture.md) and [ADR-002 Repository Strategy](ADR-002-Repository-Strategy.md) for the shape of the system.
2. Read category clusters below for depth (Platform → Backend / AI → Infrastructure → Security).
3. Treat **Status** literally:
   - **Accepted** — shipped and used as designed.
   - **Accepted (partial …)** — direction chosen; wiring incomplete (called out in the ADR).
   - **Accepted (extension-point / stub)** — ports exist; networked product behavior does not.
4. Use **References** to verify claims in the four product repositories.
5. Use **Interview Discussion** for narrative practice; do not treat it as a substitute for code review.

Each ADR follows a fixed template: Status, Date, Context, Problem Statement, Decision Drivers, Alternatives, Decision, Diagram (when useful), Consequences, Trade-offs, Risks, Future Evolution, References, Interview Discussion.

---

## ADR lifecycle

| State | Meaning |
| --- | --- |
| Proposed | Decision under discussion; not yet coded as the system default |
| Accepted | Decision matches current implementation (or an explicitly partial/stub scope) |
| Superseded | Replaced by a newer ADR; keep the old file for history |
| Deprecated | Still in code temporarily but scheduled for removal |
| Rejected | Considered and not chosen; may live only in Future Decisions or Alternatives |

**Process for new ADRs**

1. Confirm the decision exists in at least one of: application code, infrastructure config, OpenAPI/AsyncAPI, or CI gates.
2. Allocate the next number (`ADR-031-…`). Prefer a short kebab Title matching the filename.
3. Copy the template sections exactly; omit Decision Drivers that did not influence the choice.
4. Link repositories/paths under References.
5. If the topic is planned but not implemented, add it to [Future-Decisions.md](Future-Decisions.md) instead of marking Accepted.
6. Update this index (category table + count).
7. Cross-link from Chapters 01–02 / domain chapters when those exist.

Do not regenerate Chapters 01–02 when adding ADRs; only extend this chapter and navigation.

---

## Architecture Decision Index

### Platform

| ADR | Title | Status |
| --- | --- | --- |
| [001](ADR-001-Overall-Architecture.md) | Overall Architecture | Accepted |
| [002](ADR-002-Repository-Strategy.md) | Repository Strategy | Accepted |
| [006](ADR-006-Separate-AI-Platform.md) | Separate AI Platform | Accepted |

### Backend

| ADR | Title | Status |
| --- | --- | --- |
| [003](ADR-003-Clean-Architecture.md) | Clean Architecture | Accepted (AI); Partial (Business) |
| [004](ADR-004-Domain-Driven-Design.md) | Domain-Driven Design | Accepted (pragmatic) |
| [005](ADR-005-Package-by-Feature.md) | Package-by-Feature | Accepted |
| [009](ADR-009-OpenFeign-Integration.md) | OpenFeign Integration | Accepted |
| [020](ADR-020-PostgreSQL.md) | PostgreSQL | Accepted |
| [026](ADR-026-REST-over-gRPC.md) | REST over gRPC | Accepted |
| [027](ADR-027-Async-Events-Strategy.md) | Async Events Strategy | Accepted (dual-track) |

### Frontend

| ADR | Title | Status |
| --- | --- | --- |
| [001](ADR-001-Overall-Architecture.md) | Web as SPA talking only to Business | Covered in ADR-001 / ADR-002 |
| [025](ADR-025-JWT-Security.md) | JWT client session pattern | Accepted (with Business) |
| [029](ADR-029-Testing-Strategy.md) | Vitest / Playwright | Accepted (cross-cutting) |

Frontend-specific structural choices (feature-sliced React, Axios + TanStack Query, MUI) are documented primarily in Chapters 01–02 and in ADR-001/002/029 rather than as duplicate ADRs.

### AI

| ADR | Title | Status |
| --- | --- | --- |
| [007](ADR-007-Contract-First-AI.md) | Contract-First AI | Accepted |
| [010](ADR-010-FastAPI-for-AI.md) | FastAPI for AI | Accepted |
| [011](ADR-011-LangGraph-Orchestration.md) | LangGraph Orchestration | Accepted |
| [012](ADR-012-Enterprise-RAG.md) | Enterprise RAG | Accepted |
| [013](ADR-013-Capability-Registry.md) | Capability Registry | Accepted |
| [014](ADR-014-Prompt-Governance.md) | Prompt Governance | Accepted |
| [015](ADR-015-Model-Router.md) | Model Router | Accepted |
| [016](ADR-016-Guardrails.md) | Guardrails | Accepted |
| [017](ADR-017-MCP-Readiness.md) | MCP Readiness | Accepted (stub) |
| [018](ADR-018-Agent-to-Agent-Readiness.md) | Agent-to-Agent Readiness | Accepted (stub) |
| [019](ADR-019-Qdrant.md) | Qdrant | Accepted (optional provider) |
| [024](ADR-024-LangFuse.md) | LangFuse | Accepted (optional) |

### Infrastructure

| ADR | Title | Status |
| --- | --- | --- |
| [019](ADR-019-Qdrant.md) | Qdrant | Accepted (optional) |
| [020](ADR-020-PostgreSQL.md) | PostgreSQL | Accepted |
| [021](ADR-021-Redis.md) | Redis | Accepted (optional / adapter) |
| [028](ADR-028-Docker-Strategy.md) | Docker Strategy | Accepted (partial) |
| [030](ADR-030-Code-Generation.md) | Code Generation | Accepted |

### Security

| ADR | Title | Status |
| --- | --- | --- |
| [016](ADR-016-Guardrails.md) | Guardrails | Accepted |
| [025](ADR-025-JWT-Security.md) | JWT Security | Accepted |

### Observability

| ADR | Title | Status |
| --- | --- | --- |
| [022](ADR-022-Observability.md) | Observability | Accepted |
| [023](ADR-023-OpenTelemetry.md) | OpenTelemetry | Accepted (partial) |
| [024](ADR-024-LangFuse.md) | LangFuse | Accepted (optional) |

### Integration

| ADR | Title | Status |
| --- | --- | --- |
| [007](ADR-007-Contract-First-AI.md) | Contract-First AI | Accepted |
| [008](ADR-008-OpenAPI-and-AsyncAPI.md) | OpenAPI and AsyncAPI | Accepted |
| [009](ADR-009-OpenFeign-Integration.md) | OpenFeign Integration | Accepted |
| [026](ADR-026-REST-over-gRPC.md) | REST over gRPC | Accepted |
| [027](ADR-027-Async-Events-Strategy.md) | Async Events Strategy | Accepted |
| [030](ADR-030-Code-Generation.md) | Code Generation | Accepted |

### Quality / Engineering practice

| ADR | Title | Status |
| --- | --- | --- |
| [029](ADR-029-Testing-Strategy.md) | Testing Strategy | Accepted |

---

## Complete numeric index (ADR-001 … ADR-030)

1. [Overall Architecture](ADR-001-Overall-Architecture.md)
2. [Repository Strategy](ADR-002-Repository-Strategy.md)
3. [Clean Architecture](ADR-003-Clean-Architecture.md)
4. [Domain-Driven Design](ADR-004-Domain-Driven-Design.md)
5. [Package-by-Feature](ADR-005-Package-by-Feature.md)
6. [Separate AI Platform](ADR-006-Separate-AI-Platform.md)
7. [Contract-First AI](ADR-007-Contract-First-AI.md)
8. [OpenAPI and AsyncAPI](ADR-008-OpenAPI-and-AsyncAPI.md)
9. [OpenFeign Integration](ADR-009-OpenFeign-Integration.md)
10. [FastAPI for AI](ADR-010-FastAPI-for-AI.md)
11. [LangGraph Orchestration](ADR-011-LangGraph-Orchestration.md)
12. [Enterprise RAG](ADR-012-Enterprise-RAG.md)
13. [Capability Registry](ADR-013-Capability-Registry.md)
14. [Prompt Governance](ADR-014-Prompt-Governance.md)
15. [Model Router](ADR-015-Model-Router.md)
16. [Guardrails](ADR-016-Guardrails.md)
17. [MCP Readiness](ADR-017-MCP-Readiness.md)
18. [Agent-to-Agent Readiness](ADR-018-Agent-to-Agent-Readiness.md)
19. [Qdrant](ADR-019-Qdrant.md)
20. [PostgreSQL](ADR-020-PostgreSQL.md)
21. [Redis](ADR-021-Redis.md)
22. [Observability](ADR-022-Observability.md)
23. [OpenTelemetry](ADR-023-OpenTelemetry.md)
24. [LangFuse](ADR-024-LangFuse.md)
25. [JWT Security](ADR-025-JWT-Security.md)
26. [REST over gRPC](ADR-026-REST-over-gRPC.md)
27. [Async Events Strategy](ADR-027-Async-Events-Strategy.md)
28. [Docker Strategy](ADR-028-Docker-Strategy.md)
29. [Testing Strategy](ADR-029-Testing-Strategy.md)
30. [Code Generation](ADR-030-Code-Generation.md)

---

## Related documents

- [Future Decisions](Future-Decisions.md) — planned or deferred choices **not** claimed as implemented
- [../01-platform-overview/](../01-platform-overview/) — vision and principles
- [../02-system-design/](../02-system-design/) — HLA/LLA/C4 and runtime design
- [../README.md](../README.md) — portfolio home

No additional ADRs beyond 001–030 were justified as separate Accepted records at generation time; further decisions belong in Future Decisions until code lands.
