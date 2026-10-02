# Redis as Cache

## Introduction

Redis provides low-latency key-value storage—used by ACOS AI for RAG/semantic caches, not as Business SoR.

## Problem Statement

Recomputing embeddings/LLM calls for identical work wastes money and time.

## Why ACOS Uses This

Compose `redis:7-alpine`; `RedisRagCache` / `RedisSemanticCache`; memory fallbacks in tests; flush-safe on disaster.

## Implementation Overview

Compose `redis:7-alpine`; `RedisRagCache` / `RedisSemanticCache`; memory fallbacks in tests; flush-safe on disaster.

## Best Practices

TTL everything; separate key prefixes; don't store authoritative career records.

## Common Mistakes

- Treating Redis as durable primary storage.
- No TTL → memory blowups.

## Alternative Approaches

Caffeine local-only; Memcached; no cache.

## Trade-offs

Shared speed vs another moving part. Optional behind flags.

## References to ACOS modules

- AI compose + caching docs

## Interview Questions

**Q:** Business Redis?
**A:** Not used.

**Q:** Safe to flush?
**A:** Yes for cache—expect recompute.

## Further Reading

- redis.io docs — eviction/TTL

