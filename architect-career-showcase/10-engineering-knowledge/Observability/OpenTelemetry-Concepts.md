# OpenTelemetry Concepts

## Introduction

OpenTelemetry (OTel) standardizes traces, metrics, and logs export—often via OTLP to a collector.

## Problem Statement

Vendor-specific APM agents create lock-in and inconsistent context propagation.

## Why ACOS Uses This

ACOS AI can configure TracerProvider + OTLP exporter when enabled; instrumentation libraries exist but are not fully wired; Business Checkstyle bans OTel imports; compose references missing collector service.

## Implementation Overview

ACOS AI can configure TracerProvider + OTLP exporter when enabled; instrumentation libraries exist but are not fully wired; Business Checkstyle bans OTel imports; compose references missing collector service.

## Best Practices

Adopt end-to-end or keep correlation-only—avoid dangling endpoints.

## Common Mistakes

- Enabling OTEL_ENABLED against nonexistent collector without fallback.
- Half-instrumenting one service only and claiming “tracing done.”

## Alternative Approaches

Zipkin/Brave only; Datadog agent only; no telemetry.

## Trade-offs

Future portability vs current incomplete wiring—documented as scaffold.

## References to ACOS modules

- Distributed Tracing chapter 08

## Interview Questions

**Q:** Is Business traced with OTel?
**A:** No—imports banned pending plan.

**Q:** Exporter?
**A:** OTLP when AI OTEL enabled.

## Further Reading

- opentelemetry.io

