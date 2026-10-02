# Docker

## Business Platform

**File:** `architect-career-operating-system/infrastructure/docker/docker-compose.yml` (`name: acos-local`)

| Service | Image | Ports |
| --- | --- | --- |
| postgres | `postgres:17.5-alpine` | `${ACOS_DB_PORT:-5432}:5432` |
| pgadmin | `dpage/pgadmin4:9.4` | `${ACOS_PGADMIN_PORT:-5050}:80` |

Healthcheck: `pg_isready`. Volume: `acos_postgres_data`. Network: `acos-network`.

**No Business application Dockerfile.**

## AI Platform

| File | Role |
| --- | --- |
| `Dockerfile` | `python:3.13-slim`, install package + contracts, `EXPOSE 8090`, HEALTHCHECK → `/api/v1/system/liveness`, CMD uvicorn |
| `docker-compose.yml` | `ai-platform`, redis, qdrant; sets `REDIS_URL`, `QDRANT_URL`, `APP_ENV=production` |
| `docker-compose.dev.yml` | reload uvicorn, `APP_DEBUG=true`, references `.env.example` (**file missing on disk**) |

Qdrant image: `qdrant/qdrant:v1.12.5` (6333/6334). Redis: `redis:7-alpine`.

**Gap:** compose sets `OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317` but **no otel-collector service** is defined.

## Web / Contracts

No Dockerfiles.

## CI container use

AI Platform CI builds `acos-ai-platform:ci` and runs Trivy (soft fail).


## Interview Discussion

### Why this approach?

Containerize AI + data deps first; Business stays JVM-native for speed.

### Alternative approaches

Single compose for entire ecosystem. Useful later.

### Trade-offs

Two compose roots; credential default mismatch risk (see Environment-Management).

### Enterprise adoption

Multi-stage Business Dockerfile + non-root users.

### Scaling considerations

Image registries; digest pins; SBOM.

### Principal Architect interview questions

**Q1. AI HEALTHCHECK path?**  
`GET /api/v1/system/liveness`.

**Q2. Web Dockerfile?**  
Not implemented.
