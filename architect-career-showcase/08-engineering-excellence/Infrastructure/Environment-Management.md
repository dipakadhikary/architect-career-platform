# Environment Management

## Profiles / APP_ENV

| System | Mechanism | Values in use |
| --- | --- | --- |
| Business | Spring profiles | default `local`; `application-local.yml`, `application-test.yml`. **No** `prod` profile file |
| AI | `APP_ENV` | `development`, `test`, `stage`, `production` (+ runtime validation in production) |
| Web | Vite modes / `.env.development` | Zod-validated `VITE_*` |

## Notable env keys

**Business:** `ACOS_JWT_*`, `ACOS_DB_*`, `AI_PLATFORM_ENABLED`, `AI_PLATFORM_BASE_URL`, `AI_PLATFORM_API_KEY`, timeouts/resilience.

**AI:** `REDIS_*`, `QDRANT_*`, `AUTH_*`, `OPENAI_*` / Azure / Ollama, `LANGFUSE_*`, `OTEL_*`, guardrails/cache/resilience flags.

**Web:** `VITE_API_BASE_URL`, `VITE_API_PROXY_TARGET`, `VITE_AI_PLATFORM_ENABLED`, app name/version.

## Credential default mismatch (local footgun)

Compose Postgres defaults user/db/password **`acos`/`acos`/`acos`**, while Business `application-local.yml` defaults often point at **`postgres`/`postgres`**. Operators must align env vars (documented in Business `.env.example` for DB).

## Gaps

- AI `.env.example` referenced but **missing**  
- No shared env matrix doc outside this handbook  
- No stage/prod cloud environments


## Interview Discussion

### Why this approach?

Env-overridable defaults enable local demos without Vault.

### Alternative approaches

Required secrets with no defaults. Safer, slower onboarding.

### Trade-offs

Insecure JWT default (`change-me...`) must never ship to prod.

### Enterprise adoption

Externalize all secrets; fail boot if defaults in prod (AI already validates some).

### Scaling considerations

Per-env config maps; sealed secrets.

### Principal Architect interview questions

**Q1. Business default Spring profile?**  
`local`.

**Q2. AI production boot guard?**  
`validate_for_runtime()` rejects weak JWT secret, requires auth mode, forbids debug and CORS `*`.
