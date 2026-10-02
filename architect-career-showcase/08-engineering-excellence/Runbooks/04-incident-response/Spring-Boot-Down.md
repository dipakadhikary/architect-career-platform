# Incident: Business Platform (Spring Boot) Down

## Purpose

Restore the Business Platform API so Web and integrations can function.

## Scope

Process on port 8080 (`architect-career-operating-system`). Cascading Web outage is expected while Business is down.

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

- Web login/API calls fail (proxy errors, 502, ECONNREFUSED)
- `/actuator/health` unreachable
- No JVM process listening on 8080

## Detection

- Failed curls to `http://localhost:8080/actuator/health`
- User reports empty app / network errors
- **Future enhancement:** Alertmanager on scrape failure

## Diagnosis

1. Confirm port: `netstat` / `Get-NetTCPConnection -LocalPort 8080` (Windows) / `ss -lntp | grep 8080`
2. Read stdout for Flyway/datasource/security startup errors
3. Check Postgres health (`pg_isready`) — Business cannot start without DB
4. Check recent git/config changes (JWT secret syntax, bad YAML)
5. Review `mvn` build if jar failed to boot

## Step-by-Step Procedure (Recovery)

1. Ensure Postgres is up and `ACOS_DB_*` aligned with compose
2. Restart: `mvn spring-boot:run` or `java -jar …`
3. If Flyway blocked: follow Database migration / Backup restore runbooks
4. If OOM/crash loop: inspect heap, reduce load, check AI bulkhead not holding threads (disable AI flag)
5. Verify Web proxy target still `8080`

## Validation

- `GET /actuator/health` → UP
- `GET /actuator/health/readiness` → UP
- Web login succeeds
- Optional: `/actuator/info`

## Rollback

If recovery introduces worse failure, revert to last known-good env/git SHA and keep Business in CRUD-only mode (`AI_PLATFORM_ENABLED=false`) when AI is involved.

## Prevention

- Keep `mvn verify` before shared deploys
- Add Business CI (**Future enhancement**)
- Document credential defaults to avoid boot loops

## Troubleshooting

| Log hint | Action |
| --- | --- |
| Connection refused JDBC | Fix Postgres |
| Flyway checksum | Migration discipline |
| Port already in use | Kill stale process |

## Escalation

Escalate to Platform Engineer if data corruption suspected; Architect if repeated crash after dependency upgrade.

## References

- [../03-monitoring/Health-Checks.md](../03-monitoring/Health-Checks.md)
- [PostgreSQL-Down.md](PostgreSQL-Down.md)
- Chapter 04 Deployment docs
