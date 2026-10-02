# Correlation IDs

## Introduction

A correlation ID is a unique identifier propagated across services so logs/metrics for one user action can be stitched.

## Problem Statement

Distributed logs without shared IDs make AI incident debugging guesswork.

## Why ACOS Uses This

Web generates `X-Correlation-Id`; Business MDC `correlationId`; Feign forwards correlation/request/trace headers; AI structlog binds them.

## Implementation Overview

Web generates `X-Correlation-Id`; Business MDC `correlationId`; Feign forwards correlation/request/trace headers; AI structlog binds them.

## Best Practices

Generate if missing; never trust client ID for security authZ; include in error payloads when safe.

## Common Mistakes

- New UUID per hop destroying the chain.
- Using correlation ID as auth credential.

## Alternative Approaches

W3C Trace Context only; OpenTelemetry baggage; no IDs.

## Trade-offs

Cheap observability vs not replacing full tracing. ACOS leans on correlation today.

## References to ACOS modules

- [08-engineering-excellence/Observability](../../08-engineering-excellence/Observability/)

## Interview Questions

**Q:** Header name?
**A:** `X-Correlation-Id`.

**Q:** Is it a distributed trace?
**A:** Related but not full OTel spans end-to-end yet.

## Further Reading

- OWASP logging cheatsheet — correlation

