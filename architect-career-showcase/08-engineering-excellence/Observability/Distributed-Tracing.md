# Distributed Tracing

## Implemented signals

| System | Tracing reality |
| --- | --- |
| Business | Propagates `X-Trace-Id` on Feign if present; **no** OpenTelemetry SDK (Checkstyle **bans** `io.opentelemetry` imports) |
| AI | `configure_otel()` can set `TracerProvider` + OTLP exporter when `OTEL_ENABLED`; **`get_tracer()` unused**; FastAPI/httpx instrumentation deps **not imported** |
| Web | Correlation UUID only — not W3C traceparent |
| Compose | Points at `otel-collector:4317` without defining the collector service |

## LangFuse

Optional AI tracing/eval sink when `LANGFUSE_ENABLED` + keys; default **false**. Wired through adapters used by pipeline/evaluators.

## Honest summary

**Correlation IDs are real. Full distributed tracing is partial / scaffolded on AI and intentionally avoided on Business via Checkstyle.**


## Interview Discussion

### Why this approach?

Avoid forcing OTel into Business before standards mature in-repo.

### Alternative approaches

Adopt OTel everywhere early. Better long-term, cost now.

### Trade-offs

Cannot draw full trace graphs across Web→Business→AI today.

### Enterprise adoption

W3C Trace Context end-to-end; remove Checkstyle ban when ready.

### Scaling considerations

Tail-based sampling on AI spans.

### Principal Architect interview questions

**Q1. Is OpenTelemetry banned on Business?**  
Checkstyle forbids `io.opentelemetry` imports.

**Q2. LangFuse default?**  
Disabled.
