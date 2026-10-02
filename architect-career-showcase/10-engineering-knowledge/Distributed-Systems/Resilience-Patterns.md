# Resilience Patterns

## Introduction

Patterns that keep a system available when dependencies fail: retries with backoff, circuit breakers, bulkheads, fallbacks.

## Problem Statement

Without them, AI brownouts take down threads/pools on Business.

## Why ACOS Uses This

Implemented primarily via Resilience4j on Business AI calls and similar enterprise resilience settings on AI Platform.

## Implementation Overview

Implemented primarily via Resilience4j on Business AI calls and similar enterprise resilience settings on AI Platform.

## Best Practices

Prefer fail-fast + fallback for assist features; alert on CB open; don't retry auth failures.

## Common Mistakes

- Infinite retries.
- Same CB config for all dependency types.

## Alternative Approaches

Mesh-only retries; manual try/catch; chaos-only learning.

## Trade-offs

More knobs vs safer demos. Essential teaching surface for ACOS.

## References to ACOS modules

- Resilience4j topic in Backend/
- Engineering Excellence performance docs

## Interview Questions

**Q:** Fallback example?
**A:** Disable AI or return mock/degraded response when flag off or CB open.

**Q:** Slow call detection?
**A:** CB slow-call thresholds in Business config.

## Further Reading

- Release It! (Nygard)

