# Deployment Considerations

## What ships today

### Local data plane (Docker Compose)

Path: `infrastructure/docker/docker-compose.yml`

| Service | Image | Port |
| --- | --- | --- |
| postgres | `postgres:17.5-alpine` | 5432 |
| pgadmin | `dpage/pgadmin4:9.4` | 5050 |

Named volumes for data; healthcheck on Postgres; bridge network `acos-network`.

**There is no Business Platform application container defined in this Compose file.** The app runs via Maven/Spring Boot on the host (or a future image not present here).

### Application process

```bash
cd architect-career-operating-system
./mvnw spring-boot:run
# http://localhost:8080
```

Default profile `local` expects reachable Postgres and applies Flyway.

## Configuration & environment variables

Critical env vars (see `.env.example` / YAML):

- `ACOS_DB_*` — datasource
- `ACOS_JWT_SECRET`, `ACOS_JWT_ISSUER`, TTLs
- `AI_PLATFORM_ENABLED`, `AI_PLATFORM_BASE_URL`, `AI_PLATFORM_API_KEY`
- Compose: `ACOS_PGADMIN_*`, ports

Align DB name/user between Compose (`acos` defaults) and `application-local.yml` (`postgres`/`postgres` defaults) via env.

## Profiles

- `local` — developer workstation
- `test` — automated tests
- Production profile is not fully packaged as a first-class `application-prod.yml` story in-tree — treat secure env injection as an operational requirement

## Health checks

- Liveness/readiness probes enabled under Actuator
- Readiness includes DB + `aiPlatform`
- Orchestrators should use `/actuator/health/liveness` and `/actuator/health/readiness`

## Future Kubernetes

Not implemented. Expected shape when introduced:

- Deployment for Business Platform JVM image
- Service + Ingress
- Secret for JWT + DB + AI key
- Postgres operator or managed SQL
- HPA on CPU/RPS
- NetworkPolicy denying browser→AI

## Interview Discussion

### Why this architecture?

Compose for Postgres keeps onboarding light; running the JVM via Maven matches active development of a portfolio monolith.

### Alternative approaches

Full Dockerized app+db from day one; Tilt/Skaffold; serverless JDBC. App image is the obvious next packaging step.

### Trade-offs

Drift risk between documented Quick Start DB defaults and Compose defaults — mitigate with explicit `.env`.

### Scaling considerations

Immutable images, Flyway on startup (or init job), zero-downtime rolling updates with readiness gates.

### Principal Architect interview questions

**Q1. Does `docker compose up` start the API?**  
No — only Postgres/pgAdmin. Start Spring Boot separately.

**Q2. How do you know the app is ready?**  
Readiness probe including DB; optionally tolerate AI down depending on product policy.
