# Observability

## Structured logging

- `structlog` configured in lifespan (`configure_logging`)
- `log_json` default **true**
- Request context middleware binds correlation identifiers into context

## Correlation IDs

`RequestContextMiddleware` + `get_request_context()` used by pipeline audit/metrics paths. Propagated from Business Feign when present.

## Metrics

- Prometheus client via `PlatformMetrics`
- Exposed at `GET /api/v1/system/metrics`
- Includes knowledge embedding/retrieval latencies and enterprise pipeline metrics counters/histograms as instrumented

## OpenTelemetry

- `configure_otel` builds `TracerProvider` + OTLP gRPC exporter when `otel_enabled=true` (default **true**)
- Endpoint default `http://localhost:4317`
- `get_tracer` helper available
- **Honesty:** OTel FastAPI/httpx instrumentation packages are listed in `pyproject.toml` but **not applied** in `create_app`; `get_tracer` is largely unused across handlers. Treat tracing as bootstrap-only until spans are wired. Production Compose references `otel-collector` without defining that service in the same file.

## LangFuse

Optional LLM/ops tracing when enabled — see Evaluation doc.

## Health

| Endpoint | Role |
| --- | --- |
| `/api/v1/system/liveness` | Process alive |
| `/api/v1/system/readiness` | Dependency readiness |
| `/api/v1/ai/health` | AI health payload for Business probes |

## Interview Discussion

### Why this architecture?

Logs + Prometheus + optional LangFuse/OTel cover platform, system, and LLM-ops views without mandating a single vendor.

### Alternative approaches

Vendor APM only; log-only. Multi-signal is intentional for AI workloads.

### Trade-offs

OTel enabled by default may error/noise without a collector — operators should disable or provide collector.

### Scaling considerations

Tail sampling; exemplars linking metrics to traces; cardinality controls on labels.

### How would this evolve?

Auto-instrument FastAPI/httpx fully; propagate W3C `traceparent` end-to-end from Business.

### Principal AI Architect interview questions

**Q1. Default metrics path?**  
`/api/v1/system/metrics` (Prometheus text).

**Q2. Is tracing complete?**  
Provider bootstrapped; treat app-level span completeness as partial.
