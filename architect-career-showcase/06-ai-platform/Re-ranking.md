# Re-ranking

## Purpose

Reorder retrieval hits before context packing to improve precision@k.

## Providers (`reranker_provider`)

| Value | Class | Status |
| --- | --- | --- |
| `none` | `NoOpReranker` | Pass-through |
| `identity` (**default**) | `IdentityScoreReranker` | Lexical term overlap + original score |
| `cross_encoder` | `CrossEncoderReranker` | sentence-transformers CrossEncoder; requires optional dependency |
| `bge` | BGE reranker adapter | Optional heavy dependency path |
| `cohere` | Cohere rerank API | Needs `cohere_api_key` |

Factory: `build_reranker` in `app.infrastructure.knowledge.reranking.providers`.

## Flow

```mermaid
flowchart LR
  Hits[RetrievalHit list] --> Rerank[KnowledgeRerankerPort]
  Rerank --> Ordered[Ordered hits]
  Ordered --> Context[Context Builder]
```

## Trade-offs

- Identity rerank is cheap and dependency-free — limited quality lift.
- Cross-encoder / Cohere improve quality but add latency, cost, and ops weight.
- Default `identity` matches offline-first philosophy of hashing/memory defaults.

## Future

- Late-interaction models
- Diversity-aware re-ranking (MMR)
- Learned rerankers trained on ACOS eval sets

## Interview Discussion

### Why this architecture?

Rerank port keeps retrieval stores simple (recall-oriented) while allowing precision stages to vary by environment.

### Alternative approaches

Rerank inside the vector DB; no rerank. Port keeps flexibility.

### Trade-offs

Two-stage pipelines add latency; must tune top_k before/after rerank.

### Scaling considerations

Rerank only top N (e.g. 50→5); GPU for cross-encoders; batch where possible.

### How would this evolve?

Per-capability rerank policies via model router metadata.

### Principal AI Architect interview questions

**Q1. What is the production default in code?**  
`identity` unless operators change settings.

**Q2. Does Cohere ship as a hard dependency?**  
No — activated when configured; API key required.
