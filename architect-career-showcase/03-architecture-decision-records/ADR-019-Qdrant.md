# ADR-019: Qdrant

# Status

Accepted (optional adapter)

# Date

2026-08-10

# Context

RAG requires a vector index with filtering and simple local/prod ops paths.

# Problem Statement

Relying only on OLTP search cannot satisfy embedding retrieval.

# Decision Drivers

- Performance
- Extensibility
- Operational Complexity
- Cost

# Alternatives Considered

- **pgvector in PostgreSQL** — Viable alternative; not the implemented primary adapter.
- **Pinecone-only** — Managed lock-in; not default.
- **No vector DB—LLM context stuffing only** — Rejected for knowledge scale.

# Decision

Implement `QdrantVectorStore` adapter selectable via settings; default remains in-memory store for local/tests. Compose ships Qdrant for local AI stacks.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Dedicated vector ops.
- Easy Docker local dependency.
- Memory fallback preserves DX.

# Negative Consequences

- Extra moving part when enabled.
- Defaults mean Qdrant is not always used.

# Trade-offs

- Ops complexity vs AI retrieval quality.

# Risks

- Assuming Qdrant is always on in demos without config.

# Future Evolution

- Tenant collections; managed Qdrant in prod.

# References

- `architect-career-ai-platform/app/infrastructure/knowledge/vectorstore`
- `architect-career-ai-platform/docker-compose.yml`

## Interview Discussion

### Why was this approach selected?

Qdrant is the durable vector option behind a port—not a hard dependency.

### When would you choose another approach?

Choose pgvector if you must minimize infrastructure count.

### How would this decision change for 10x / 100x / 1000x users?

Shard/replicate Qdrant at large retrieval QPS; keep Business DB unaffected.

### Common Principal Architect interview questions

**Q1. Default vector store?**

memory; qdrant when configured.

### Common follow-up questions

- How do you rebuild a document's vectors?

