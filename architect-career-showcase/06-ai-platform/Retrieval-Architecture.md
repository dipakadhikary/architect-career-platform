# Retrieval Architecture

## Implementation

`DefaultKnowledgeRetriever` supports three `RetrievalMode` values:

| Mode | Behavior |
| --- | --- |
| `dense` (default via `retrieval_mode`) | Embed query → vector `search` |
| `keyword` | `keyword_search` on store |
| `hybrid` | Dense + keyword fused with **RRF** (`_rrf_fuse`) |

## Filtering

`KnowledgeRetrievalQuery.filters` passed through to store search — used for ownership/document scoping from callers.

## Top-K and thresholds

- `top_k` supplied per query (summarize uses `summarize_top_k` default **5**).
- `retrieval_score_threshold` optional global setting; applied on dense search when set.
- Metrics: `knowledge_retrieval_latency`, `knowledge_embedding_latency`.

## Keyword search honesty

`keyword_search` on the Qdrant-backed store is implemented as scroll + local lexical scoring — **not** a native Qdrant full-text index. Memory store scores term overlap in-process. Hybrid mode still fuses that signal with dense ANN via RRF.

## Top-K behavior (search API)

Knowledge search typically retrieves a wider candidate set (on the order of `max(limit*2, limit)`), reranks, then truncates to the requested `limit`.

## Embedding cache on retrieve

Query embeddings are cached via `RagCachePort` when available. Redis RAG cache also exposes retrieval/prompt/LLM cache APIs, but the retriever path **uses embedding cache primarily**; other RAG cache slots are largely unused today.

## Agent-side retrieval

Agentic flows use `MultiSourceCapabilityRetriever` / `CapabilityRetrieverPort` which can call into knowledge retrieval for `mode="knowledge"` queries inside workflows.

## Future retrieval strategies

- Multi-query / HyDE
- Parent-document and sentence-window retrieval
- Learning-to-rank
- GraphRAG / knowledge-graph expansion

## Interview Discussion

### Why this architecture?

Dense + keyword + hybrid covers most enterprise note-search cases without locking to one ANN API.

### Alternative approaches

Keyword-only BM25; pure dense. Hybrid RRF is a strong default when both signals exist.

### Trade-offs

Hybrid doubles backend work; needs good keyword implementation in the store.

### Scaling considerations

Cap top_k; use score thresholds; cache embeddings; shard by tenant filters.

### How would this evolve?

Configurable fusion weights; query classifiers choosing mode dynamically.

### Principal AI Architect interview questions

**Q1. Default mode?**  
`dense`.

**Q2. Where is RRF?**  
Inside the retriever when mode is `hybrid`, after both legs return.
