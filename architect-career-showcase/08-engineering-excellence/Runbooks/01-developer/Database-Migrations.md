# Database Migrations (Flyway)

## Purpose

Apply and validate schema changes safely for the Business Platform Postgres database.

## Scope

Flyway migrations under `architect-career-operating-system/src/main/resources/db/migration` (`V1`–`V12` today). Does not cover Qdrant collection changes.

## Audience

- Developer
- Platform Engineer
- SRE

## Preconditions

- Postgres up and reachable with correct `ACOS_DB_*`
- Ability to run `mvn spring-boot:run` or tests
- **Never** edit an already-applied migration checksum on a shared DB

## Step-by-Step Procedure

1. **Create migration** — add `V{next}__short_description.sql` in `db/migration/`.

2. **Expand/contract** — prefer additive columns/indexes first; avoid destructive drops without backup ([../05-maintenance/Backup-Restore.md](../05-maintenance/Backup-Restore.md)).

3. **Apply** — start Business (`mvn spring-boot:run`) or run IT suite; Flyway runs at startup against schema `acos`, history table `flyway_schema_history`.

4. **Verify history**
   ```sql
   SELECT version, description, success FROM acos.flyway_schema_history ORDER BY installed_rank;
   ```

5. **Run tests**
   ```bash
   mvn verify
   ```
   Integration tests use Testcontainers `postgres:17.5-alpine` when Docker is available.

## Validation

- Application starts without Flyway error
- New version row `success=true`
- Domain smoke API still works (register/login + one CRUD)

## Rollback

1. If migration fails mid-way: fix SQL, repair only with Flyway-aware process — **do not** casually delete history rows.
2. Restore from `pg_dump` if data damaged ([Backup-Restore](../05-maintenance/Backup-Restore.md)).
3. **Future enhancement:** Flyway `undo` scripts / dedicated migrate job in CI.

## Troubleshooting

| Issue | Cause | Fix |
| --- | --- | --- |
| Checksum mismatch | Edited old `V*` file | Restore file; add new version instead |
| Connection refused | DB down / wrong creds | Fix compose + `ACOS_DB_*` |
| Lock timeout | Long migration | Run offline maintenance window |

## Escalation

Escalate to Platform Engineer / Architect for production-like shared DBs before destructive DDL.

## References

- [../../Database-Optimization.md](../../Performance/Database-Optimization.md) (via Performance)
- Chapter 04 Persistence Architecture
- Flyway official docs
- Repo: `architect-career-operating-system`
