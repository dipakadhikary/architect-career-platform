# MDC & Correlation

## Introduction

Mapped Diagnostic Context (MDC) attaches key/value context to log events on a thread.

## Problem Statement

Without MDC, each log line needs manual ID parameters.

## Why ACOS Uses This

Business `CorrelationIdFilter` populates MDC `correlationId` from `X-Correlation-Id`. AI binds ids in structlog processors.

## Implementation Overview

Business `CorrelationIdFilter` populates MDC `correlationId` from `X-Correlation-Id`. AI binds ids in structlog processors.

## Best Practices

Clear MDC after request; propagate on async threads carefully (`aiTaskExecutor` awareness).

## Common Mistakes

- Forgetting MDC in async workers.
- Trusting client-supplied IDs for authorization.

## Alternative Approaches

Only access logs; OpenTelemetry baggage exclusively.

## Trade-offs

Simple stitching vs incomplete async propagation risk.

## References to ACOS modules

- Correlation filter + Feign headers

## Interview Questions

**Q:** Header?
**A:** `X-Correlation-Id`.

**Q:** Async risk?
**A:** Thread handoff may drop MDC without TaskDecorator.

## Further Reading

- SLF4J MDC docs

