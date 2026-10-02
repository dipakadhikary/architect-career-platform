# Alerting

## Implemented

**No alert manager, PagerDuty, or GitHub issue automation for runtime SLOs.**

Soft CI signals (non-blocking): AI Platform mypy, pip-audit, bandit, Trivy.

## Practical manual alerts (ops checklist)

| Signal | Suggest action |
| --- | --- |
| Business readiness DOWN (db) | Check Postgres compose / credentials |
| `aiPlatform` indicator DOWN | Check AI process / `ai.platform.enabled` / network |
| AI readiness NOT_READY | Redis/Qdrant connectivity |
| Circuit breaker open metrics | Inspect AI latency/errors; consider disable AI flag |
| Rate limit spikes | Investigate abuse or misconfig |

## Future Roadmap

Alertmanager rules on error rate, CB state, p95 latency, queue depth; on-call rotation.


## Interview Discussion

### Why this approach?

Don't fake an alerting stack that isn't wired.

### Alternative approaches

Cloud watchdog on day one. Needs deploy targets.

### Trade-offs

Human-driven incident detection today.

### Enterprise adoption

Page on burn-rate SLO alerts.

### Scaling considerations

Severity taxonomy; silence windows.

### Principal Architect interview questions

**Q1. Any Alertmanager config in repo?**  
No.

**Q2. What CI 'alerts' exist?**  
Soft-fail security scanners on AI CI.
