# Pipeline Pattern

## Introduction

A pipeline chains processing stages with a shared context—ideal for cross-cutting AI controls.

## Problem Statement

Copy-pasting guardrail+cache+audit into every route diverges.

## Why ACOS Uses This

AI enterprise execution pipeline runs ordered stages around a handler callable; Business logging/metrics wrap Feign similarly at a smaller scale.

## Implementation Overview

AI enterprise execution pipeline runs ordered stages around a handler callable; Business logging/metrics wrap Feign similarly at a smaller scale.

## Best Practices

Keep stages idempotent where possible; short-circuit on deny; measure each stage.

## Common Mistakes

- Hidden global mutable pipeline state.
- Stages that perform unbounded I/O silently.

## Alternative Approaches

Servlet filters only; AOP aspects; middleware in FastAPI exclusively without enterprise ports.

## Trade-offs

Uniformity vs debugging longer stacks—use correlation IDs.

## References to ACOS modules

- Enterprise-AI-Pipeline.md in AI/

## Interview Questions

**Q:** Similar web concept?
**A:** Middleware / filter chains.

**Q:** Failure policy?
**A:** Guardrail block should not call LLM.

## Further Reading

- Pipes and filters architecture

