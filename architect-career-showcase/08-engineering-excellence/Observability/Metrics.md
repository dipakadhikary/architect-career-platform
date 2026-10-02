# Metrics

## Business Platform

Actuator exposes `health`, `info`, `metrics`, **`prometheus`**.

Custom Micrometer meters (`AiPlatformMetrics`):

- `acos.ai.platform.requests`  
- `acos.ai.platform.request.duration`  
- `acos.ai.platform.retries` / `.timeouts` / `.circuitbreaker.state`  

**Gap:** `micrometer-registry-prometheus` dependency is **not** in the POM — Prometheus endpoint is configured but the registry may be incomplete without the dependency.

## AI Platform

Prometheus text at **`GET /api/v1/system/metrics`** via `PlatformMetrics` + HTTP middleware counters/histograms; knowledge/agentic/enterprise metrics (including cache hits).

`METRICS_ENABLED` setting exists but is **not read** in code paths reviewed — metrics path is effectively always wired when app runs.

## Web

No product metrics SDK.


## Interview Discussion

### Why this approach?

Prometheus exposition is the portable metrics contract.

### Alternative approaches

CloudWatch EMF only. Less portable.

### Trade-offs

Business prometheus dependency gap; METRICS_ENABLED dead flag.

### Enterprise adoption

Add registry dep; Grafana dashboards; RED/USE methods.

### Scaling considerations

Recording rules; exemplars linked to traces.

### Principal Architect interview questions

**Q1. AI metrics path?**  
`/api/v1/system/metrics`.

**Q2. Business custom AI metric prefix?**  
`acos.ai.platform.*`.
