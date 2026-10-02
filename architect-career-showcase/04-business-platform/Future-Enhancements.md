# Future Enhancements

Items below are **not implemented** (or only partially present) in `architect-career-operating-system`. Do not describe them as current capabilities.

## Product & API

| Item | Current state | Target |
| --- | --- | --- |
| AI BFF controllers | Only `GET /api/v1/integration/ai/health` | Controllers matching Web `/api/v1/integration/ai/**` calling facades with authz |
| Live dashboard metrics | Config placeholders | Aggregate from career/learning/portfolio/knowledge repositories |
| Analytics domain | `package-info` stub | Real bounded context + APIs |
| Admin-only APIs | Roles exist; little `@PreAuthorize` usage | Explicit ADMIN policies where needed |
| Formal Business OpenAPI SoT | springdoc from code | Optional contract-first like AI contracts |

## Integration & async

| Item | Current state | Target |
| --- | --- | --- |
| Brokered events | Spring in-process + `@Async` | Outbox + Kafka/Pulsar per AsyncAPI |
| Generated Feign from AI Contracts | Hand-maintained clients/DTOs | Wire generated artifacts in CI |
| Durable indexing retry | Best-effort async + failure handler logging | Retry queue / DLQ / poison handling |
| mTLS / service JWT to AI | API key header pattern | Stronger service identity |

## Persistence & performance

| Item | Current state | Target |
| --- | --- | --- |
| Soft-delete outside career | Career aggregates soft-archive (`archived` / `archived_at`); other domains hard-delete | Decide explicit per-domain policy if knowledge/learning/portfolio need archive |
| Career/Portfolio AI stubs | `recommendCareer`, `generateProjectSummary` return empty | Implement via facades + BFF when product-ready |
| Business read cache | None | Cache-aside for hot aggregates if measured |
| DB prod sizing profiles | Local Hikari defaults | Env-specific pools + replicas |

## Observability & delivery

| Item | Current state | Target |
| --- | --- | --- |
| OpenTelemetry on Business | Not present | Traces + W3C propagation to AI |
| Application container image | Compose is DB-only | Multi-stage Dockerfile + compose/helm |
| Kubernetes manifests | Absent | Deployments, secrets, HPA, probes |
| JaCoCo enforcement | Minimum `0.00` | Raise coverage gates gradually |
| Production config pack | No full prod profile pack | Locked-down actuator exposure, secret management |

## Explicit non-goals (for now)

- Browser calling AI Platform directly
- Embedding LLM SDKs inside Business JVM as the primary AI runtime
- Microservices split without a measured scaling or team boundary need

## Promotion rule

When an item lands in code + tests, document it in the relevant Chapter 04 file and remove it from this list. Cross-link a new ADR in Chapter 03 if the decision is architecturally significant.
