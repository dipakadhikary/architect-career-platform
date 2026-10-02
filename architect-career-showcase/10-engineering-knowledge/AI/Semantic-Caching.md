# Semantic Caching

## Introduction

Semantic caches store responses keyed by embedding similarity so paraphrased prompts can reuse prior answers.

## Problem Statement

Exact-string HTTP caches miss near-duplicate AI calls that still cost tokens.

## Why ACOS Uses This

ACOS `RedisSemanticCache` with similarity threshold default 0.92 and TTL 3600s; metrics for cache hits. Similarity vectors are process-local today—cross-replica caveat.

## Implementation Overview

ACOS `RedisSemanticCache` with similarity threshold default 0.92 and TTL 3600s; metrics for cache hits. Similarity vectors are process-local today—cross-replica caveat.

## Best Practices

Tune threshold carefully; exclude personalized/private prompts when unsafe; monitor hit rate vs wrong hits.

## Common Mistakes

- Caching confidential answers broadly.
- Assuming Redis alone equals multi-replica similarity.

## Alternative Approaches

Exact Redis keys only; provider-side caches; no cache.

## Trade-offs

Cost savings vs correctness risk. Thresholding is the core trade.

## References to ACOS modules

- [08-engineering-excellence/Performance/Caching.md](../../08-engineering-excellence/Performance/Caching.md)

## Interview Questions

**Q:** Default threshold?
**A:** 0.92.

**Q:** Multi-process limitation?
**A:** Local `_vectors` index—not fully shared.

## Further Reading

- Semantic cache industry posts; embedding similarity basics

