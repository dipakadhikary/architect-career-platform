# Incident: Redis Down

## Purpose

Restore AI cache/rate-limit dependency or degrade gracefully.

## Scope

Redis 7 used by AI Platform caches. Business does **not** use Redis. Product CRUD should continue.

## Audience

- SRE
- DevOps Engineer
- Platform Engineer
- Developer
- Architect

## Preconditions

- Access to process logs and health endpoints
- Ability to change env flags and restart services
- Docker CLI if dependencies are containerized

## Symptoms

- AI readiness NOT_READY when `REDIS_ENABLED=true`
- Cache misses / errors in AI logs
- Elevated LLM latency/cost

## Detection

- AI `/api/v1/system/readiness`
- `docker ps` redis unhealthy
- Connection errors to `REDIS_URL`

## Diagnosis

1. Confirm `REDIS_URL` (compose `6379` vs settings default `6380`)
2. `docker compose ps` / logs for redis
3. Decide: restore Redis vs set `REDIS_ENABLED=false` for degraded AI (memory fallbacks may apply depending on code paths)

## Step-by-Step Procedure (Recovery)

1. Restart redis container
2. Fix URL/port env; restart AI
3. If Redis optional for incident window: disable Redis and accept degraded caching
4. Re-enable when healthy

## Validation

- Redis PING works
- AI readiness READY (if Redis required)
- Business unaffected

## Rollback

If recovery introduces worse failure, revert to last known-good env/git SHA and keep Business in CRUD-only mode (`AI_PLATFORM_ENABLED=false`) when AI is involved.

## Prevention

- Document Redis URL override in setup docs
- Don't treat Redis as SoR

## Troubleshooting

| Issue | Fix |
| --- | --- |
| Wrong port 6380 | Export 6379 |
| Memory pressure | Flush cache keys carefully (`FLUSHDB` only if acceptable) |

## Escalation

Escalate if Redis holds non-cache data unexpectedly (should not in ACOS design).

## References

- [../../Performance/Caching.md](../../Performance/Caching.md)
- AI compose
