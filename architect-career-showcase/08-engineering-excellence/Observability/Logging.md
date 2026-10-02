# Logging

## Business Platform

- Default Spring Boot logging (no custom Logback XML)  
- Console pattern includes MDC **`correlationId`**  
- `CorrelationIdFilter` binds `X-Correlation-Id`  
- AI calls logged in key=value style via `AiPlatformCallLogger`  

## AI Platform

- **structlog** configured at lifespan (`configure_logging`)  
- JSON or console renderers; injects correlation / request / trace / user ids when present  

## Web Platform

- `console.error` in ErrorBoundary and env parse failures  
- No remote log shipper  

## Not implemented

Central ELK/Loki; Business JSON structured logging; PII scrubbing on Business logs.


## Interview Discussion

### Why this approach?

Correlation IDs first — cheapest cross-service stitch.

### Alternative approaches

Full OpenTelemetry logs signal. Heavier.

### Trade-offs

Inconsistent formats Java vs Python.

### Enterprise adoption

Unify JSON logs + shipper; redact PII.

### Scaling considerations

Sample debug logs; index by correlationId.

### Principal Architect interview questions

**Q1. Header name for correlation?**  
`X-Correlation-Id` (Web generates UUID).

**Q2. Business structured JSON logs?**  
Not configured — pattern + MDC only.
