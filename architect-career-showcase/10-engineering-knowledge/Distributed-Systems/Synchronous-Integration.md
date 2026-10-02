# Synchronous Integration

## Introduction

Synchronous integration waits for a remote response within a request lifecycle—simple to reason about, sensitive to latency.

## Problem Statement

Career UX needs immediate AI assist results; fully async queues add delay and UI complexity for interactive flows.

## Why ACOS Uses This

Web→Business and Business→AI are synchronous HTTP. AsyncAPI defines future events but no broker is wired yet.

## Implementation Overview

Web→Business and Business→AI are synchronous HTTP. AsyncAPI defines future events but no broker is wired yet.

## Best Practices

Set timeouts; use idempotency where retries exist; don't hold DB transactions open across AI calls.

## Common Mistakes

- Chatty N+1 Feign calls in a loop.
- Assuming sync AI always succeeds.

## Alternative Approaches

Message queues; gRPC streaming; webhook callbacks.

## Trade-offs

Simple UX vs coupling on availability. Mitigate with flags and resilience.

## References to ACOS modules

- [02-system-design](../../02-system-design/) request flows
- Integration architecture chapter 04

## Interview Questions

**Q:** Why not Kafka for chat completion?
**A:** Interactive latency; events fit lifecycle notifications better.

**Q:** Transaction spanning AI?
**A:** Avoid—commit domain state then async index when enabled.

## Further Reading

- Enterprise integration patterns — synchronous vs async

