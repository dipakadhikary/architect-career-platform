# Production Readiness Checklist

## Purpose

Decide whether an environment may be called “production.” Most items are **currently unmet** for ACOS — checklist documents honesty gates.

## Scope

Shared/production-like deployments. Local demos may waive with documented risk.

## Audience

- Architect
- SRE
- Platform Engineer
- DevOps Engineer

## Preconditions

- Architecture and Engineering Excellence docs reviewed

## Step-by-Step Procedure (Checklist)

### Must-have (many Future)

- [ ] Business & Web GitHub Actions required on `main` (**Future** — not implemented)
- [ ] Container images for Business & Web (**Future** — AI only today)
- [ ] Secrets not using `change-me` defaults; vault or sealed secrets (**partial**)
- [ ] AI auth mandatory (`validate_for_runtime` path) — verify `APP_ENV=production`
- [ ] CORS allowlist explicit (**gap**)
- [ ] TLS ingress (**Future**)
- [ ] Automated Postgres backups + tested restore (**Future**)
- [ ] Prometheus scrape + alerts (**Future**)
- [ ] Pager / on-call (**Future**)
- [ ] Contracts Packages publish + pinned consumer versions (**stubbed**)

### Implemented strengths to keep

- [ ] AI kill switch `AI_PLATFORM_ENABLED`
- [ ] Resilience4j on Feign path
- [ ] Health/liveness/readiness endpoints
- [ ] Flyway migrations
- [ ] Web never calls AI directly

## Validation

Unsigned checklist items explicitly waived with owner + date, or environment is **not** labeled production.

## Rollback

Do not promote; remain on demo/local posture.

## Troubleshooting

N/A — governance checklist.

## Escalation

Architect owns production label disputes.

## References

- [../../Roadmap/Short-Term.md](../../Roadmap/Short-Term.md)
- Security & Infrastructure chapters
