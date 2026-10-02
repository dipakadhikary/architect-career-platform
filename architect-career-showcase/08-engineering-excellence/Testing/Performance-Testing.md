# Performance Testing

## Implemented scaffolds (not CI gates)

| Tool | Location | Notes |
| --- | --- | --- |
| k6 | `architect-career-ai-platform/scripts/load/k6_smoke.js` | 10 VUs, 30s; thresholds p95 `<2000` ms, fail rate `<0.05` |
| Locust | `scripts/load/locustfile.py` | Example host `http://127.0.0.1:8090` |

Locust is **not** listed in `pyproject` dependencies — operator-installed.

## Business / Web

No JMeter/Gatling configs; Web relies on bundle splitting rather than load tests.

## Future Roadmap

CI smoke k6 against ephemeral AI compose; Business CRUD load; regression budgets.


## Interview Discussion

### Why this approach?

Provide runnable smoke scripts without blocking PRs on env-heavy load tests.

### Alternative approaches

Mandatory load in every PR. Slow/expensive.

### Trade-offs

Performance regressions can merge unnoticed.

### Enterprise adoption

Nightly load + alert on budget breach.

### Scaling considerations

Distributed Locust; isolate LLM provider cost.

### Principal Architect interview questions

**Q1. k6 in CI?**  
No.

**Q2. Example k6 fail rate threshold?**  
Less than 5%.
