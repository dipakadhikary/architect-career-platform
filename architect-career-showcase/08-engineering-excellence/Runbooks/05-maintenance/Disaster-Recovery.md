# Disaster Recovery

## Purpose

Rebuild an ACOS environment after total loss of local volumes or machines.

## Scope

Local/demo DR. **RTO/RPO are not contractually defined.** No multi-region DR (**Future enhancement**).

## Audience

- SRE
- Platform Engineer
- Architect
- DevOps Engineer

## Preconditions

- Access to git repositories
- Docker Hub / Maven / npm connectivity
- Optional: Postgres dump and notes for re-seed

## Step-by-Step Procedure

1. Recreate directories; clone four repos.
2. `docker compose up -d` for Business Postgres (+ AI redis/qdrant if needed).
3. Restore dump **or** allow empty DB + Flyway on Business boot.
4. Configure env (`ACOS_DB_*`, JWT secret, AI URLs).
5. Start Business → Web → optional AI.
6. Sync contracts into AI if required.
7. Re-create demo user data; re-index knowledge if AI enabled.
8. Run health checks and smoke tests.

## Validation

Full startup checklist in [../01-developer/Environment-Setup.md](../01-developer/Environment-Setup.md) passes.

## Rollback

N/A — this is rebuild. Keep multiple dump generations when available.

## Troubleshooting

| Issue | Fix |
| --- | --- |
| Flyway on non-empty dirty DB | Prefer restore dump over mixing |
| Lost Qdrant | Rebuild from Postgres via index APIs |

## Escalation

Escalate customer-data incidents for legal/comms — out of scope for portfolio demos.

## References

- [Backup-Restore.md](Backup-Restore.md)
- [../02-operations/Deployment.md](../02-operations/Deployment.md)
- Chapter 08 Lessons Learned / Roadmap
