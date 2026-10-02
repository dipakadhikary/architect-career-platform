# Performance Checklist

## Purpose

Review performance posture using implemented knobs and smoke tools.

## Scope

Local/demo performance — not certified capacity.

## Audience

- Architect
- SRE
- Platform Engineer
- Developer

## Preconditions

- Metrics/health endpoints reachable
- Optional k6/Locust scripts installed for AI smoke

## Step-by-Step Procedure (Checklist)

- [ ] Hikari pool sized for environment (local max 10)
- [ ] AI Feign timeouts/bulkhead understood (30s / 20)
- [ ] AI rate limit / bulkhead settings reviewed
- [ ] Semantic cache threshold/TTL understood (0.92 / 3600s) + multi-replica caveat
- [ ] Web route lazy-loading / manualChunks retained
- [ ] PWA does **not** cache `/api` (NetworkOnly)
- [ ] Optional: run `scripts/load/k6_smoke.js` against AI (not CI)
- [ ] CB metrics observed under fault injection (AI stop)
- [ ] N+1 / slow SQL spot-checked for new queries
- [ ] No LLM calls inside DB transactions

## Validation

Checklist complete; any load test artifacts attached to release notes.

## Rollback

Revert perf-sensitive config if regressions appear.

## Troubleshooting

Thread exhaustion → disable AI flag; inspect bulkhead rejects.

## Escalation

Capacity commitments → Architect (no formal capacity cert yet).

## References

- [../../Performance/](../../Performance/)
- AI `scripts/load/`
