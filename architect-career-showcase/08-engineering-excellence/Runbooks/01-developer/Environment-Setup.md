# Environment Setup

## Purpose

Bring a clean workstation to a verified ACOS local stack.

## Scope

First-time setup and machine rebuilds. Not cloud account provisioning (**Future enhancement**).

## Audience

- Developer
- DevOps Engineer
- Platform Engineer

## Preconditions

- Git access to the four repositories
- Docker running
- Disk for images: Postgres 17.5, Redis 7, Qdrant 1.12.5, optional AI Python image

## Step-by-Step Procedure

1. **Clone** (sibling directories):
   ```bash
   git clone <url>/architect-career-operating-system
   git clone <url>/architect-career-web
   git clone <url>/architect-career-ai-platform
   git clone <url>/architect-career-ai-contracts
   ```

2. **Build order (recommended)**
   1. Contracts (if you will run AI with fresh models): `cd architect-career-ai-contracts && mvn clean verify`
   2. Sync into AI: `cd architect-career-ai-platform && python scripts/sync_contracts.py && pip install -e ./third_party/acos_ai_contracts`
   3. Postgres compose (Business)
   4. Business `mvn -q -DskipTests package` once to warm deps
   5. Web `npm install`
   6. AI `pip install -e ".[dev]"`

3. **Environment variables — Business**
   | Variable | Purpose | Notes |
   | --- | --- | --- |
   | `ACOS_DB_*` | JDBC | Align with compose `acos` defaults |
   | `ACOS_JWT_SECRET` | HMAC JWT | ≥32 chars; change from `change-me...` |
   | `AI_PLATFORM_ENABLED` | Feign AI | default `false` |
   | `AI_PLATFORM_BASE_URL` | AI base | default `http://localhost:8090` |
   | `AI_PLATFORM_API_KEY` | Outbound key | optional |

4. **Environment variables — Web** (`.env.development` / Zod-validated)
   - `VITE_API_PROXY_TARGET=http://localhost:8080`
   - `VITE_AI_PLATFORM_ENABLED=false` (UI gate; server flag still authoritative)

5. **Environment variables — AI**
   - `REDIS_URL=redis://localhost:6379/0`, `REDIS_ENABLED=true|false`
   - `QDRANT_URL=http://localhost:6333`, `QDRANT_ENABLED=true|false`
   - `OPENAI_API_KEY` / Azure / Ollama as needed
   - `AUTH_*` for JWT/API key modes
   - **Note:** `.env.example` is referenced in docs but **missing on disk** — create a local `.env` manually from Settings fields.

6. **Docker startup**
   - Business: `infrastructure/docker/docker-compose.yml`
   - AI: `docker-compose.dev.yml` or `docker-compose.yml`

7. **Health verification** — follow [../03-monitoring/Health-Checks.md](../03-monitoring/Health-Checks.md)

## Validation

All health tables green; Web can register/login; optional AI liveness UP.

## Rollback

Remove containers/volumes only if intentionally wiping local data: `docker compose down -v` (destructive).

## Troubleshooting

| Issue | Resolution |
| --- | --- |
| `pip install` fails on contracts | Run `mvn clean verify -Ppython` in contracts first |
| Port in use | Stop conflicting process or remap publish ports |
| OTEL connection noise | Set `OTEL_ENABLED=false` (compose references missing `otel-collector`) |

## Escalation

Escalate if corporate proxy/SSL inspection blocks Maven Central, npm, or Docker Hub pulls.

## References

- [Local-Development.md](Local-Development.md)
- [../../Infrastructure/Environment-Management.md](../../Infrastructure/Environment-Management.md)
- AI `app/shared/config/settings.py`
