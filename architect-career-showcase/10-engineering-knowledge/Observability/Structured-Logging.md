# Structured Logging

## Introduction

Structured logs emit key/value or JSON fields for machine query rather than only free-text lines.

## Problem Statement

Grepping unstructured AI logs across instances fails during incidents.

## Why ACOS Uses This

AI Platform uses structlog (JSON/console). Business uses console pattern + MDC correlation—not full JSON Logback yet.

## Implementation Overview

AI Platform uses structlog (JSON/console). Business uses console pattern + MDC correlation—not full JSON Logback yet.

## Best Practices

Log correlation IDs; avoid secrets; prefer consistent field names across services.

## Common Mistakes

- Logging Authorization headers.
- Inconsistent field names (`corr_id` vs `correlationId`).

## Alternative Approaches

ELK only; printf debugging; OpenTelemetry logs signal only.

## Trade-offs

Queryability vs verbosity/cost. Unify formats as next step.

## References to ACOS modules

- Observability Logging chapter 08

## Interview Questions

**Q:** Is Business JSON structured?
**A:** Not fully—pattern + MDC.

**Q:** AI logger?
**A:** structlog.

## Further Reading

- structlog documentation

