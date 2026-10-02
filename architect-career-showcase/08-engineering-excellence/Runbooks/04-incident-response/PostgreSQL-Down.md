# Incident: PostgreSQL Down

## Purpose

Restore the system of record for Business Platform.

## Scope

Compose service `postgres` (`postgres:17.5-alpine`) or equivalent local Postgres. AI may still run but Business will not.

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

- Business boot failure / readiness `db` DOWN
- JDBC connection errors
- Web entirely broken

## Detection

- `pg_isready` fails
- `docker compose ps` shows postgres unhealthy/exited
- Actuator db component DOWN

## Diagnosis

1. `docker compose -f infrastructure/docker/docker-compose.yml ps`
2. `docker logs acos-postgres` (container name from compose)
3. Verify host port `5432` free/published
4. Confirm credentials `ACOS_DB_*` vs `POSTGRES_*`
5. Check disk for volume `acos_postgres_data`

## Step-by-Step Procedure (Recovery)

1. `docker compose up -d postgres`
2. Wait for healthcheck `pg_isready`
3. Restart Business
4. If data directory corrupted: restore from dump ([../05-maintenance/Backup-Restore.md](../05-maintenance/Backup-Restore.md)) or recreate volume (**data loss**)
5. Re-run Flyway on Business start

## Validation

- `pg_isready` OK
- Business readiness UP
- Login works; Flyway history intact

## Rollback

If recovery introduces worse failure, revert to last known-good env/git SHA and keep Business in CRUD-only mode (`AI_PLATFORM_ENABLED=false`) when AI is involved.

## Prevention

- Monitor disk on Docker volumes
- Take manual dumps before risky migrations
- **Future enhancement:** automated backups / PITR

## Troubleshooting

| Issue | Fix |
| --- | --- |
| Auth failed | Align acos vs postgres defaults |
| Port conflict | Stop other Postgres |
| Volume permission | Recreate carefully |

## Escalation

Escalate on suspected data loss before recreating volumes.

## References

- [../05-maintenance/Database-Maintenance.md](../05-maintenance/Database-Maintenance.md)
- Business docker-compose
