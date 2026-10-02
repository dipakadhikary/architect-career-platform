# Rollback

## Purpose

Recover from a failed configuration, binary, or migration attempt using currently available levers.

## Scope

Local/demo stacks and git-based rollback. No automated blue/green controller exists (**Future enhancement**).

## Audience

- DevOps Engineer
- SRE
- Platform Engineer

## Preconditions

- Known-good git SHA or jar/dist artifact
- Database dump if schema changed ([../05-maintenance/Backup-Restore.md](../05-maintenance/Backup-Restore.md))

## Step-by-Step Procedure

1. **Application config rollback (fastest)**
   - Revert env vars (`AI_PLATFORM_ENABLED`, JWT secret, Redis URL)
   - Restart Business / AI / Web processes

2. **Binary / static rollback**
   - Business: run previous `target/*.jar` or `git checkout <sha> && mvn package`
   - Web: redeploy previous `dist/` or checkout SHA + `npm run build`
   - AI: run previous image tag / git SHA + compose

3. **Contracts rollback**
   - Checkout previous contracts SHA → `mvn clean verify` → re-sync AI `third_party`
   - Restart AI

4. **Schema rollback**
   - Flyway does **not** auto-undo. Restore DB from dump taken pre-migration, or ship a forward-fix migration.
   - **Future enhancement:** documented undo migrations / PITR

5. **AI dependency rollback**
   - Disable AI flag to restore CRUD-only mode while AI is repaired

## Validation

Health green; auth works; no CB storm; users can complete core journeys without the failed feature.

## Rollback

N/A — this runbook is the rollback. If rollback fails, escalate to Disaster Recovery rebuild.

## Troubleshooting

| Issue | Fix |
| --- | --- |
| Flyway won't start after bad migration | Restore DB volume/dump; fix migration on a branch |
| Token invalid after JWT secret revert | Expected — users must re-login |
| Vendored contracts mismatch | Re-sync from intended SHA |

## Escalation

Escalate to Architect when rollback implies data-loss decisions or multi-tenant impact.

## References

- [Deployment.md](Deployment.md)
- [../05-maintenance/Disaster-Recovery.md](../05-maintenance/Disaster-Recovery.md)
- [../04-incident-response/AI-Platform-Down.md](../04-incident-response/AI-Platform-Down.md)
