# Resilience4j

## Introduction

Resilience4j is a lightweight fault-tolerance library: retry, circuit breaker, rate limiter, bulkhead, time limiter—composable around functional calls.

## Problem Statement

Remote AI calls fail loudly and cascade without isolation.

## Why ACOS Uses This

ACOS configures instance `ai-platform` and applies Retry → CircuitBreaker → Bulkhead → TimeLimiter in `AiPlatformInvoker`. Metrics expose retries/timeouts/CB state.

## Implementation Overview

Example knobs: CB sliding window, 50% failure, 30s wait; retry 3 with backoff ignoring validation/auth errors; bulkhead 20; timelimiter 30s.

## Best Practices

- Don't retry non-idempotent calls blindly.
- Ignore business 4xx from retry.
- Observe CB state before paging humans.

## Common Mistakes

- Retrying POSTs that create duplicates.
- Bulkhead wait forever (ACOS uses maxWait 0).

## Alternative Approaches

Netflix Hystrix (legacy); Failsafe; service mesh retries only.

## Trade-offs

In-process control vs mesh-wide policy. ACOS keeps resilience next to the ACL.

## References to ACOS modules

- [08-engineering-excellence/Performance](../../08-engineering-excellence/Performance/)
- Business `application.yml` resilience section

## Interview Questions

**Q:** Order of decorators matters—why?
**A:** Retry inside/outside CB changes failure counting; ACOS documents explicit order in invoker.

**Q:** Feign CB enabled?
**A:** No—manual Resilience4j.

## Further Reading

- resilience4j.readme.io

