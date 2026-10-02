# Configuration Management

## Profiles

| Profile | Role |
| --- | --- |
| `local` (default) | Local Postgres datasource, DEBUG `com.acos`, Flyway schema `acos` |
| `test` | `application-test.yml` for tests |
| base `application.yml` | Shared app name, JPA, Feign, Actuator, JWT, domain limits, AI + Resilience4j |

Activation: `spring.profiles.default: local`.

## Configuration areas

### Server & app

- `server.port=8080`
- `spring.application.name=acos-platform`

### Datasource (local)

Env overrides: `ACOS_DB_HOST`, `ACOS_DB_PORT`, `ACOS_DB_NAME`, `ACOS_DB_USER`, `ACOS_DB_PASSWORD`.

HikariCP: pool name `acos-local-pool`, max 10, min idle 2, timeouts configured.

**Note:** Compose defaults DB name/user to `acos`, while `application-local.yml` defaults DB name to `postgres` — align env vars when starting local stacks.

### JWT (`acos.jwt`)

Secret, issuer, access/refresh TTLs via env `ACOS_JWT_*`.

### Domain knobs (`acos.*`)

Dashboard placeholders; knowledge/learning/portfolio/career limits.

### AI platform (`ai.platform`)

Enabled flag, base URL, API key, timeouts, compression, resilience instance name, retry settings.

### Resilience4j

Instance `ai-platform` for circuit breaker, retry (ignores AI validation/auth exceptions), time limiter 30s, bulkhead max 20 concurrent.

### Spring Cloud OpenFeign

Per-client timeouts; compression; circuitbreaker enabled **false** (custom invoker).

### Management / springdoc

Actuator exposure and springdoc paths as in Observability/API docs.

## Code configuration

`com.acos.config` and feature `*Configuration` / `@EnableConfigurationProperties` classes bind typed properties (JWT, dashboard, AI platform properties, etc.).

`.env.example` documents local secrets/variables for developers.

## Interview Discussion

### Why this architecture?

12-factor style env overrides on a YAML base keep local demos easy and deployments configurable without rebuilds.

### Alternative approaches

Config server; only env vars; Vault for all secrets. Vault/OIDC secret injection is a future hardening step.

### Trade-offs

Default JWT secret and AI disabled defaults favor local DX over secure-by-default production — operators must override.

### Scaling considerations

Externalize config per environment (dev/stage/prod) and ban placeholder secrets in prod charts.

### Principal Architect interview questions

**Q1. How do you disable AI in prod temporarily?**  
`AI_PLATFORM_ENABLED=false` — facades short-circuit.

**Q2. Why is Feign CB off?**  
Resilience composed in `AiPlatformInvoker` with shared instance config.
