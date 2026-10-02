# Metrics

## Purpose

Scrape and interpret Prometheus-format metrics exposed by ACOS services.

## Scope

AI `/api/v1/system/metrics` and Business Actuator `prometheus`/`metrics`. No Grafana dashboards in-repo (**Future enhancement**).

## Audience

- SRE
- Platform Engineer
- Architect

## Preconditions

- Services up
- curl

## Step-by-Step Procedure

1. **AI metrics**
   ```bash
   curl -s http://localhost:8090/api/v1/system/metrics | head
   ```
   Inspect HTTP histograms, cache hit counters, enterprise series.

2. **Business metrics**
   ```bash
   curl -s http://localhost:8080/actuator/metrics
   curl -s http://localhost:8080/actuator/prometheus
   ```
   Custom: `acos.ai.platform.requests`, `.request.duration`, `.retries`, `.timeouts`, `.circuitbreaker.state`

3. **Caveat** — confirm `micrometer-registry-prometheus` dependency presence if `/actuator/prometheus` is empty/broken.

4. **Performance monitoring (manual)**
   - Watch CB state and latency while load-testing with optional `scripts/load/k6_smoke.js` (not CI-gated)

## Validation

Metrics endpoints respond; key series present during a test AI call.

## Rollback

N/A

## Troubleshooting

| Issue | Fix |
| --- | --- |
| Empty prometheus | Missing registry dependency / wrong exposure |
| No AI series | `METRICS_ENABLED` unused flag — metrics still typically wired; check middleware |

## Escalation

Escalate when metrics needed for capacity decisions without historical scrape storage — propose Prometheus install (**Future**).

## References

- [../../Observability/Metrics.md](../../Observability/Metrics.md)
- AI Platform CI does not ship Grafana
