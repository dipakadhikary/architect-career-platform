# Architecture Principles

Only principles that are **visible in the current implementation** are listed. Aspirational principles without code evidence are omitted or marked as roadmap.

---

## 1. Modular Monolith (Business Platform)

### Why

Career, learning, portfolio, and knowledge data are tightly related. Early microservices would force distributed transactions before the product boundaries were stable.

### Evidence

Single Spring Boot application with domain packages (`auth`, `knowledge`, `learning`, `portfolio`, `career`, `integration`, `common`).

### Benefits

- One deployable for product APIs
- Straightforward transactional consistency
- Clear module seams for a later extract if needed

### Trade-offs

- Deployment granularity is coarse
- Requires package discipline to avoid a “ball of mud”

---

## 2. Layered Domain Packaging (not strict Clean Architecture everywhere)

### Why

Business Platform favors pragmatic controller → service → repository layering per domain for delivery speed.

### Evidence

Consistent layered folders under each `com.acos.*` domain; MapStruct mappers; shared `ApiResponse` and exception handling.

### Benefits

- Easy onboarding for Spring teams
- Predictable navigation

### Trade-offs

- Not a pure hexagonal core (JPA entities are the persistence model)
- Domain logic can leak toward frameworks if unchecked

**Note:** The AI Platform applies Clean/hexagonal more strictly than the Business Platform.

---

## 3. Clean Architecture / Ports & Adapters (AI Platform)

### Why

LLM providers, vector DBs, and observability vendors change faster than orchestration use cases.

### Evidence

- `intelligence/*` ports and models
- `infrastructure/*` adapters (OpenAI, Qdrant, Redis, LangFuse, enterprise components)
- `orchestration/*` use-case services and enterprise pipeline/facades
- Factories select adapters from settings

### Benefits

- Swap OpenAI ↔ Ollama ↔ Azure without rewriting workflows
- Test with hashing embeddings + memory vector store

### Trade-offs

- More types and wiring
- Requires DI discipline (`dependency-injector`)

---

## 4. Dependency Inversion

### Why

High-level AI policy and RAG flow must not import vendor SDKs.

### Evidence

`ModelRouterPort`, `EmbeddingPort`, `VectorStorePort`, `GuardrailsPort`, `AiExecutionPipelinePort`, etc., consumed by orchestration; implemented only in infrastructure.

### Benefits

- Stable use-case layer
- Mockable unit tests

### Trade-offs

- Indirection cost for simple scripts

---

## 5. Anti-Corruption Layer for AI (Business Platform)

### Why

Python AI DTOs and failure modes must not pollute domain services.

### Evidence

`integration` package: Facades → Gateways → Feign clients; domain `*AiService` classes call facades; Resilience4j on the AI client path; graceful fallbacks when AI disabled.

### Benefits

- Domains keep working with AI off
- Central place for retries/circuit breaking

### Trade-offs

- Extra mapping layer
- Feature flag + fallback semantics must stay honest in UX

---

## 6. Contract-First AI Integration

### Why

Java, Python, and future clients diverge quickly if each invents request shapes.

### Evidence

- `architect-career-ai-contracts` OpenAPI 3.1 aggregate and domain YAML
- AsyncAPI 3.0 event channels
- AI Platform handlers use generated `acos_ai_contracts` models
- Path alignment with Business Platform Feign `/api/v1/ai/**`

### Benefits

- Single schema SoT for AI HTTP
- Generator pipeline for Java/Python/TypeScript artifacts

### Trade-offs

- Generator/publish workflow still maturing
- Business/Web do not fully consume generated SDKs yet (hand-maintained clients remain)

---

## 7. Browser Isolation from AI Runtime

### Why

Provider keys, model policy, and service auth must not live in the browser.

### Evidence

Web Axios clients target Business Platform only; README/architecture guidance forbids direct AI Platform calls; Vite proxies `/api` to `:8080`.

### Benefits

- Central authorization and product rules
- AI credentials stay server-side

### Trade-offs

- Requires a Business Platform BFF for Web AI UX (currently incomplete beyond health)

---

## 8. Progressive / Optional AI Enablement

### Why

Core career OS value must not depend on LLM availability.

### Evidence

`ai.platform.enabled` defaults to `false`; Feign clients not registered when disabled; facades return fallbacks; AI Platform supports local hashing/memory modes.

### Benefits

- Reliable demos of CRUD domains without AI infra
- Safer rollout

### Trade-offs

- Dual-path testing (AI on/off)
- UX must not promise AI when disabled

---

## 9. Observability by Design

### Why

AI and career workflows need correlation for support and interviews/demo debugging.

### Evidence

- Business: Actuator health/metrics/prometheus; AI health indicator
- Web: correlation ID on Axios requests
- AI: request context middleware, structlog, Prometheus metrics, optional LangFuse/OTEL, enterprise audit events

### Benefits

- Cross-service request tracing hooks
- Platform metrics without bolting later

### Trade-offs

- Some AI cost/token metrics are still placeholder-oriented
- OTEL auto-instrumentation not fully applied in AI app code

---

## 10. Security by Design (staged)

### Why

Auth and AI abuse controls are required for any external demo/prod path.

### Evidence

- Business: JWT filter, password hashing, refresh token persistence, public route allowlist
- AI: optional JWT/API key/internal service auth; rate limiting; guardrails; production settings validation helpers

### Benefits

- Clear hardening path
- Local DX with auth disabled where intentional

### Trade-offs

- AI auth defaults off (must be enabled for production)
- In-memory rate limit is not distributed
- Method-level `@PreAuthorize` not broadly used on Business controllers

---

## 11. Testability

### Why

Architecture claims must be executable.

### Evidence

- Business: broad unit/integration suite + Testcontainers + Maven verify gates
- Web: Vitest + Playwright
- AI: pytest unit/integration + CI coverage gate
- Contracts: validate-and-generate CI

### Benefits

- Refactors are safer across repos
- Contract breakage caught in generation CI

### Trade-offs

- Uneven coverage gates (Business JaCoCo minimum currently permissive)
- E2E depth still limited for full CRUD/AI journeys

---

## 12. Extensibility via Factories and Capability Registries

### Why

New embedding providers, rerankers, workflows, and routing policies will appear.

### Evidence

AI Platform factories for LLM/embeddings/vector store/rerank; agentic capability/workflow registration; policy-driven model router; enterprise pipeline stages as composable ports.

### Benefits

- Add providers without rewriting endpoints
- Policy changes via configuration

### Trade-offs

- Configuration surface area grows
- Stub extension points (MCP/A2A) must not be mistaken for full features

---

## 13. Explicit State Machines for Critical Domains

### Why

Career application status transitions are business rules, not free-form strings.

### Evidence

Career application status state machine in Business Platform domain code; status history/timeline APIs.

### Benefits

- Prevents illegal transitions
- Auditable career pipeline

### Trade-offs

- Requires careful evolution of allowed transitions

---

## 14. Feature-Sliced Frontend

### Why

UI complexity tracks product domains; technical-only folders do not scale.

### Evidence

`src/features/{auth,knowledge,learning,portfolio,career,ai,dashboard}` with thin pages and shared HTTP/theme foundations.

### Benefits

- Parallel feature work
- Clear ownership boundaries

### Trade-offs

- Shared kit must stay thin or features fork utilities

---

## Principles intentionally not claimed as fully realized

| Claim sometimes used in narrative | Current honesty |
| --- | --- |
| Full Clean Architecture on Business Platform | Layered modular monolith; not strict hexagonal |
| Event-driven cross-service architecture | AsyncAPI drafted; runtime is mostly REST + in-process events |
| Complete AI product UX end-to-end | AI Platform + Feign exist; Web BFF incomplete |
| Distributed MCP/A2A mesh | Local stubs only |

These belong in Chapter 15 (Roadmap) until code catches up.
