# Timeouts & Bulkheads

## Introduction

Timeouts bound wait time; bulkheads limit concurrent calls so one dependency cannot exhaust the whole process.

## Problem Statement

LLM latency tails pin Business request threads.

## Why ACOS Uses This

Business AI TimeLimiter 30s; bulkhead max 20; AI bulkhead settings (e.g., 64) and rate limits (120/min in-memory).

## Implementation Overview

Business AI TimeLimiter 30s; bulkhead max 20; AI bulkhead settings (e.g., 64) and rate limits (120/min in-memory).

## Best Practices

Choose timeouts from SLO + provider p99; bulkhead sizes from pool size; fail immediately when bulkhead full (ACOS maxWait 0).

## Common Mistakes

- Timeout > client UX patience without messaging.
- Bulkhead = unbounded queue.

## Alternative Approaches

Only gateway timeouts; queue-based isolation; thread-per-request without limits.

## Trade-offs

Protects process vs more rejected load. Correct for shared JVM.

## References to ACOS modules

- Performance architecture docs chapter 08
- AiPlatformInvoker

## Interview Questions

**Q:** Why maxWait 0?
**A:** Prefer reject over pile-up.

**Q:** Rate limit distributed?
**A:** Not yet—in-memory on AI.

## Further Reading

- Bulkhead pattern — Nygard / Azure architecture center

