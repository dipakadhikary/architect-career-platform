# Distributed Tracing

## Purpose

Describe what tracing capability exists today and how to operate within its limits.

## Scope

Correlation IDs are implemented end-to-end-ish. Full OpenTelemetry/LangFuse are partial/optional. Business Checkstyle bans `io.opentelemetry` imports.

## Audience

- SRE
- Platform Engineer
- Architect
- Developer

## Preconditions

- Understanding that **correlation ≠ full traces** today

## Step-by-Step Procedure

1. **Primary tool: correlation IDs** — use [../01-developer/Debugging.md](../01-developer/Debugging.md).

2. **AI OpenTelemetry**
   - Settings: `OTEL_ENABLED`, `OTEL_EXPORTER_OTLP_ENDPOINT` (default `http://localhost:4317`)
   - Compose may set `http://otel-collector:4317` **without** defining the collector service
   - FastAPI/httpx instrumentation deps exist but are not fully wired; `get_tracer()` largely unused
   - **Operational guidance:** set `OTEL_ENABLED=false` unless you run a collector

3. **LangFuse**
   - Enable only with `LANGFUSE_ENABLED=true` + public/secret keys
   - Default off; production validation requires keys if enabled

4. **Future enhancement**
   - W3C `traceparent` Web→Business→AI
   - Remove Business OTel ban when end-to-end plan lands
   - Deploy otel-collector sidecar/service

## Validation

Team uses correlation IDs successfully; no accidental dependency on missing collector.

## Rollback

Disable OTEL/LangFuse flags if they destabilize the environment.

## Troubleshooting

| Symptom | Action |
| --- | --- |
| Connection errors to otel-collector | Disable OTEL or add collector |
| Expecting Jaeger UI | Not shipped — **Future** |

## Escalation

Escalate to Architect for tracing standard adoption decisions.

## References

- [../../Observability/Distributed-Tracing.md](../../Observability/Distributed-Tracing.md)
- Chapter 06 Observability
