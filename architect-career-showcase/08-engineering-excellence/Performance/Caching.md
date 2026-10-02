# Caching

## AI Platform (implemented)

| Cache | Behavior |
| --- | --- |
| `RedisRagCache` | Keys for embeddings/retrieval/prompts/LLM; memory fallback |
| `RedisSemanticCache` | Enterprise pipeline; cosine threshold `SEMANTIC_SIMILARITY_THRESHOLD` (0.92); TTL 3600s |
| Metrics | `acos_ai_enterprise_cache_hits_total{cache_type}` |

**Caveat:** similarity vectors for semantic cache are kept in process-local `_vectors`; Redis stores payloads but cross-process similarity reload is incomplete.

## Web PWA

Workbox: CacheFirst fonts (365d), StaleWhileRevalidate images (30d), NetworkOnly API.

## Business

No Spring Cache / Redis / Caffeine.


## Interview Discussion

### Why this approach?

Cache expensive AI computations nearest the AI runtime.

### Alternative approaches

HTTP cache on Business BFF. Weaker for semantic similarity.

### Trade-offs

Local vector index limits multi-replica correctness.

### Enterprise adoption

Shared vector index in Redis/Qdrant for semantic cache.

### Scaling considerations

TTL + tenant partitioning; stampede protection.

### Principal Architect interview questions

**Q1. Business Redis?**  
Not used.

**Q2. Default semantic threshold?**  
0.92.
