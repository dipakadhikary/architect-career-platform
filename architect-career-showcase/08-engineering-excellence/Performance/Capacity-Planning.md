# Capacity Planning

## Known numeric knobs (not production capacity certs)

| Knob | Default |
| --- | --- |
| Business Hikari max pool | 10 |
| Business AI bulkhead | 20 |
| Business AI TimeLimiter | 30s |
| AI rate limit | 120/min |
| AI bulkhead | 64 |
| AI semantic cache TTL | 3600s |
| Web Axios timeout | 30s |
| k6 smoke script | 10 VUs / 30s; p95 < 2s; fail rate < 5% (scaffold) |

## Practice

Capacity is **not** formally modeled. Use smoke scripts and Actuator/AI metrics during demos to observe saturation.

## Future Roadmap

Load test gates in CI; error budget policy; Postgres size forecasting.


## Interview Discussion

### Why this approach?

Publish the actual knobs so planners don't invent numbers.

### Alternative approaches

Guess 'thousands of RPS'. Misleading.

### Trade-offs

No certified capacity statement.

### Enterprise adoption

Run controlled load against stage with prod-like data volumes.

### Scaling considerations

Plan Qdrant memory for embedding dimensions × docs.

### Principal Architect interview questions

**Q1. Is Locust a CI gate?**  
No — scripts under `scripts/load` are scaffolds.

**Q2. Hikari max connections local?**  
10.
