# Vector Database

## Architecture

Vector persistence is behind `VectorStorePort`, built by `build_vector_store`:

| Provider | Class | Default |
| --- | --- | --- |
| `memory` | `MemoryVectorStore` | **Yes** (`vector_store_provider=memory`) |
| `qdrant` | `QdrantVectorStore` (+ `QdrantAdapter`) | Optional |

Settings: `qdrant_url` (default `http://localhost:6333`), `qdrant_api_key`, `qdrant_collection` default **`acos_knowledge`**, `qdrant_enabled`.

Docker Compose runs `qdrant/qdrant:v1.12.5` alongside the app for local/prod compose stacks.

## Collection strategy

- Single configured collection name for knowledge vectors (`acos_knowledge` by default).
- Points store chunk text, embeddings, and metadata (document id, owner, tags, etc.).
- No multi-collection tenant sharding implemented yet (`tenant_isolation_enabled` exists as a setting flag for future policy).

## Metadata

Metadata travels with each upserted record and is available for filtered search (owner/document constraints used by retrieval queries).

## Indexes / similarity search

- Dense search: embedding nearest-neighbor via store `search` with `top_k` and optional `score_threshold`.
- Keyword search: store `keyword_search` for lexical mode / hybrid fusion (Qdrant path = scroll + local term scoring, not native full-text)
- Memory store implements both for offline use; Qdrant adapter targets production density search.

## Future vector databases

- pgvector (if consolidating on Postgres)
- Weaviate / Milvus
- Per-tenant collections and alias swap for blue/green reindex

## Interview Discussion

### Why this architecture?

Port + memory default keeps unit/integration tests free of Docker when desired, while Qdrant is the production-shaped option already composed.

### Alternative approaches

Vectors only in Postgres; managed OpenAI vector stores. Qdrant chosen for dedicated ANN ops and Compose simplicity.

### Trade-offs

Two backends to understand; memory store is not durable across process restarts.

### Scaling considerations

Qdrant clustering, payload indexes on owner_id, snapshot/backup, reindex jobs on model change.

### How would this evolve?

Collection-per-tenant or partition keys; hybrid sparse vectors in Qdrant.

### Principal AI Architect interview questions

**Q1. Is Qdrant required to boot?**  
No — default memory provider. Compose still starts Qdrant for convenience.

**Q2. Are vectors the system of record?**  
No — Business PostgreSQL notes are SoR; vectors are derived.
