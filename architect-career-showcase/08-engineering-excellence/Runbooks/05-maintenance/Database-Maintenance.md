# Database Maintenance

## Purpose

Perform routine PostgreSQL care for the Business Platform without claiming managed DBA automation.

## Scope

Compose Postgres (`acos` DB/schema) used by Business. Excludes Qdrant (vector) except cross-notes.

## Audience

- SRE
- Platform Engineer
- DevOps Engineer

## Preconditions

- `psql` or `docker compose exec postgres`
- Valid credentials (`acos`/`acos` by compose default)
- Recent dump before destructive work

## Step-by-Step Procedure

1. **Connectivity:** `docker compose exec postgres pg_isready -U acos`
2. **Connections:** inspect `pg_stat_activity` for stuck sessions
3. **Size:** use `psql` size commands (`\l+`, `\dt+`)
4. **Flyway history:** `SELECT * FROM acos.flyway_schema_history ORDER BY installed_rank;`
5. **Vacuum/analyze (manual):** `VACUUM (ANALYZE);` during low activity — tune per environment
6. **Index review:** after slow queries appear in logs (no automated EXPLAIN CI yet)
7. **Hikari:** keep Business `maximum-pool-size` aligned with Postgres `max_connections`

## Validation

Business readiness UP; sample CRUD OK; no long idle-in-transaction sessions.

## Rollback

Restore from dump if maintenance corrupted data ([Backup-Restore.md](Backup-Restore.md)).

## Troubleshooting

| Issue | Fix |
| --- | --- |
| Too many connections | Lower Hikari max; kill idle backends |
| Bloat | VACUUM; schedule maintenance window |

## Escalation

Escalate unexplained corruption or multi-hour locks to Platform Lead.

## References

- [../01-developer/Database-Migrations.md](../01-developer/Database-Migrations.md)
- Hikari settings in `application-local.yml`
