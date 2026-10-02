# Incident: Qdrant Down

## Purpose

Restore vector search or run AI without Qdrant when disabled.

## Scope

Qdrant `v1.12.5` for embeddings/ANN. Notes SoR remains Postgres.

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

- AI readiness NOT_READY when `QDRANT_ENABLED=true`
- Knowledge search/index failures
- Degraded `/api/v1/ai/health` dependency status

## Detection

- Readiness/health dependency section
- Qdrant container logs; ports `6333`/`6334`

## Diagnosis

1. Check container + `QDRANT_URL=http://localhost:6333`
2. Confirm API key settings if used
3. Distinguish empty collection vs process down
4. Plan re-index from Business knowledge after restore

## Step-by-Step Procedure (Recovery)

1. `docker compose up -d qdrant`
2. Restart AI
3. Temporary: `QDRANT_ENABLED=false` if AI must run without vectors (feature-limited)
4. After restore: trigger knowledge re-index flows when AI enabled

## Validation

- Qdrant HTTP responds on 6333
- AI readiness OK
- Sample search returns after re-index

## Rollback

If recovery introduces worse failure, revert to last known-good env/git SHA and keep Business in CRUD-only mode (`AI_PLATFORM_ENABLED=false`) when AI is involved.

## Prevention

- Volume backups for `qdrant_data` before upgrades
- Keep Postgres as SoR so rebuild is possible

## Troubleshooting

| Issue | Fix |
| --- | --- |
| Volume wiped | Re-index from Business |
| Version skew | Pin compose image |

## Escalation

Escalate if production knowledge corpus cannot be rebuilt from SoR.

## References

- Chapter 06 Vector Database docs
- [../05-maintenance/Disaster-Recovery.md](../05-maintenance/Disaster-Recovery.md)
