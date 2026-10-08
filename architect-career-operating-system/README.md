# Architect Career Operating System (ACOS)

Production-oriented backend foundation for the Architect Career Operating System.

## Stack

- Java 21
- Spring Boot 3.5.x
- Spring Cloud OpenFeign
- Resilience4j
- Maven

## Requirements

- JDK 21+
- Maven 3.9+

## Run

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

## Build

```bash
mvn clean package
```

Quality gates (Spotless, Checkstyle, PMD, SpotBugs, tests) run during `verify`.

## Package layout

Base package: `com.acos`

| Package | Responsibility |
| --- | --- |
| `com.acos.common` | Shared utilities and cross-cutting types |
| `com.acos.config` | Application configuration (including async) |
| `com.acos.auth` | Authentication and authorization |
| `com.acos.dashboard` | Dashboard aggregation |
| `com.acos.knowledge` | Knowledge management |
| `com.acos.learning` | Learning paths and progress |
| `com.acos.portfolio` | Portfolio artifacts |
| `com.acos.career` | Career planning and tracking |
| `com.acos.analytics` | Metrics and insights |
| `com.acos.integration` | AI Integration Layer (sole Java↔AI Platform boundary) |

## AI Integration Layer

The Business Platform never calls OpenAI, LangChain, or LangGraph directly. All AI traffic goes
through `com.acos.integration`, which will talk to the future Python AI Platform over HTTP.

### Architecture

```
Business *AiService
    → *AiFacade (validation, feature toggle, fallback, exception shielding)
        → *AiGateway (Resilience4j + metrics + logging)
            → OpenFeign *AiClient  (when ai.platform.enabled=true)
```

| Component | Role |
| --- | --- |
| Facades (`KnowledgeAiFacade`, `LearningAiFacade`, `CareerAiFacade`, `PortfolioAiFacade`) | Business-facing API; graceful fallbacks when AI is disabled/unavailable |
| Gateways | Delegate to Feign with Resilience4j (Retry, Circuit Breaker, Time Limiter, Bulkhead) |
| OpenFeign clients | Capability contracts against `${ai.platform.base-url}` |
| Health | `GET /api/v1/integration/ai/health` and Actuator indicator `aiPlatform` |

Business modules depend **only on facades**, never on Feign clients.

### Feature toggle

```yaml
ai:
  platform:
    enabled: false          # AI_PLATFORM_ENABLED
    base-url: http://localhost:8090
    api-key: ""
    connection-timeout: 3s
    read-timeout: 30s
```

When `enabled=false`:

- Feign clients are not registered
- Facades return graceful fallbacks (index acknowledgement, empty recommendations, “AI service unavailable”, empty analysis)
- Normal business flows continue

### Resilience

Externalized under `resilience4j.*` for instance `ai-platform`:

- Retry
- Circuit Breaker
- Time Limiter
- Bulkhead

### Observability

- Micrometer metrics: `acos.ai.platform.requests`, `request.duration`, `retries`, `timeouts`, circuit-breaker state
- Structured logs: correlation id, feature, capability, endpoint, latency, status (no secrets)
- Outbound headers: `X-Correlation-Id`, `X-Request-Id`, `X-Trace-Id` (when present), `X-User-Id`, `Authorization`, `X-API-Key`

### Knowledge indexing

After a knowledge note is created/updated, a domain event is published after commit. An `@Async`
listener calls `KnowledgeAiFacade.indexKnowledge`. AI failures never roll back the business
transaction.

### Account recovery

Login identifier is the account email. `POST /api/v1/auth/forgot-user-id` and
`POST /api/v1/auth/forgot-password` return the same acknowledgement whether or not the address is
registered. Password reset tokens are stored as SHA-256 hashes. Messages go through the
notification module. See [docs/account-recovery.md](docs/account-recovery.md) and
[docs/notifications.md](docs/notifications.md).

### OpenAPI

SpringDoc scans `com.acos`. AI health is documented under tag **AI Integration**. Swagger UI:
`http://localhost:8080/swagger-ui.html`.
