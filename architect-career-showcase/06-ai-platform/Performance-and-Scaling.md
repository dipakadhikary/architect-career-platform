# Performance and Scaling

## Implemented performance features

| Feature | Detail |
| --- | --- |
| Embedding cache | Rag cache keys for query embeddings |
| Semantic cache | Enterprise semantic cache (TTL 3600, similarity 0.92) — **excludes** knowledge index/search/summarize |
| Redis | Shared cache/memory backend when enabled |
| Resilience | Retry, timeout (30s), circuit breaker, bulkhead (64), fallback |
| Rate limit | 120 req/min default |
| Metrics | Latency histograms for knowledge embed/retrieve |
| Connection reuse | httpx client factory |

## Concurrency

Async FastAPI + bulkhead limit on pipeline execution. Agentic workflows should avoid unbounded fan-out.

## Streaming

Primary APIs are synchronous JSON responses today — streaming not the default performance lever yet.

## Future scaling

| Scale | Actions |
| --- | --- |
| 10× | More API replicas; Redis/Qdrant sizing; raise bulkhead carefully |
| 100× | Async index workers; cache warmer; provider rate-limit aware router |
| 1000× | Shard vectors; dedicated embed service; queue-based workflows; multi-region |

## Interview Discussion

### Why this architecture?

Cache + bulkhead + timeouts protect upstream LLMs while keeping the API horizontally scalable (stateless app).

### Alternative approaches

Always-on GPU embed workers from day one — overkill for portfolio stage.

### Trade-offs

Semantic cache can serve stale answers if not invalidated — excluded for mutating knowledge paths.

### Scaling considerations

Watch CB open rate, embed latency, Qdrant p99, Redis memory.

### How would this evolve?

Prompt cache at provider; batch embed API; admission control per tenant.

### Principal AI Architect interview questions

**Q1. Why not cache knowledge search?**  
Incorrect stale results / consistency; pipeline marks those workflows non-cacheable.

**Q2. First bottleneck?**  
Usually embedding/LLM provider quotas, not FastAPI itself.
