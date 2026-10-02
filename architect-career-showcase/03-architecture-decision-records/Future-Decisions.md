# Future Decisions

This document records architectural choices that are **intentionally not implemented** (or only partially specified) as of the Chapter 03 ADR freeze. They must **not** be described as Accepted product capabilities until code, config, contracts, and preferably tests exist.

Use this list when proposing `ADR-031+`. Promote an item to an Accepted ADR only after implementation lands.

---

## Integration & product seams

| ID | Topic | Current truth | Candidate decision when ready |
| --- | --- | --- | --- |
| FD-001 | Business Platform AI BFF | Web clients target `/api/v1/integration/ai/**`; Business exposes little beyond AI health | Expose BFF controllers that map Web DTOs → Feign facades with authz |
| FD-002 | Generated SDK consumption | Contracts generate Java/TS/Python artifacts; Web/Business still largely hand-maintained clients | Wire generated Feign/TS clients as the only integration path |
| FD-003 | Brokered AsyncAPI | AsyncAPI channels exist; no Kafka/Pulsar/Rabbit consumer in Business or AI | Adopt outbox + broker; implement indexed/failed event handlers |
| FD-004 | Cross-service correlation | Correlation ID on Business; end-to-end W3C trace not complete | Propagate `traceparent` / correlation across Feign and FastAPI |

## AI platform evolution

| ID | Topic | Current truth | Candidate decision when ready |
| --- | --- | --- | --- |
| FD-005 | Networked MCP | Ports + in-memory stubs | Choose transport/host; implement real MCP client/server |
| FD-006 | Networked A2A | Ports + in-memory stubs | Define agent directory, auth, and task protocol between services |
| FD-007 | Full OTel spans | TracerProvider bootstrap; limited app span usage; Compose collector incomplete | Instrument FastAPI/httpx + RAG/agent nodes; add collector service |
| FD-008 | Default production AI backends | Defaults favor `hashing` embeddings + `memory` vector store | Change defaults only with secrets, Qdrant, and eval gates in CI |
| FD-009 | Distributed rate limits / cost hard-stop | Middleware and cost ports exist locally | Shared Redis quotas + budget enforcement across replicas |
| FD-010 | Strong AI auth defaults | Platform can run with local/dev-friendly auth posture | Mandatory mTLS or signed service JWT between Business and AI |

## Data & analytics

| ID | Topic | Current truth | Candidate decision when ready |
| --- | --- | --- | --- |
| FD-011 | Analytics domain | Package/stub placeholders | First-class analytics bounded context + aggregations |
| FD-012 | Live dashboard metrics | Placeholder metrics configuration | Query-backed dashboard aggregations from PostgreSQL |
| FD-013 | Vector as product SoR | Vectors are projections for RAG | Never elevate Qdrant to system of record for career entities |

## Delivery & operations

| ID | Topic | Current truth | Candidate decision when ready |
| --- | --- | --- | --- |
| FD-014 | Unified container packaging | AI Compose strong; Business infra Docker for Postgres; Web/Business app images incomplete as a unified story | Multi-stage images + compose/helm for all runtimes |
| FD-015 | Kubernetes topology | Documented as future in system design | Deploy Business, AI, Redis, Qdrant, Postgres with HPA and secrets |
| FD-016 | Unified CI/CD across repos | Per-repo CI exists; no single release train | Contract-compatible release gates across four repos |
| FD-017 | Durable AI job workers | Indexing via in-process `@Async` | Separate worker pool / queue for long RAG and agent jobs |

## Explicitly deferred alternatives (still valid later)

These were considered in Accepted ADRs and remain valid **future** pivots—not current defaults:

- Microservices per domain (Career, Knowledge, Learning, Portfolio)
- Browser → AI Platform direct calls (rejected for security; remains rejected unless a dedicated public edge is designed)
- gRPC or GraphQL as primary Business↔AI or Web↔Business protocol
- Mandatory Kafka from day one for all domain writes
- Full hexagonal refactor of the Business Platform

---

## How to promote a Future Decision to an ADR

1. Implement and prove with tests/CI.
2. Draft `ADR-0xx` using the Chapter 03 template.
3. Set Status to Accepted (or Accepted with an honest partial scope).
4. Remove or mark the FD row as **Promoted → ADR-0xx**.
5. Update [README.md](README.md) category tables.

Return to [ADR index](README.md).
