# Phase 5 — Hybrid retrieval and reranking

Phase 5 improves the Phase 4 RAG path. `POST /api/v1/ai/chat` is unchanged. When `RAG_ENABLED` is false, the assistant stays the Phase 2 general model. When RAG is on and `RAG_HYBRID_ENABLED` is true, retrieval is hybrid. `RAG_HYBRID_ENABLED=false` keeps Phase 4 vector-only retrieval.

No conversation store, agent, or tool-calling behavior was added.

## Architecture

```
User query
    |
    +---------------------------+
    |                           |
    v                           v
Lexical retrieval          Vector retrieval
(indexed chunks)           (Phase 3/4 store)
    |                           |
    +-------------+-------------+
                  |
                  v
        Authorization filter
                  |
                  v
     Normalize, dedupe, fuse
                  |
                  v
         Optional reranker
                  |
                  v
           Final top-K
                  |
                  v
      Phase 4 context and prompt
                  |
                  v
        LLM answer + sources
```

Lexical and vector search run concurrently. Fusion does not compare a `ts_rank` number with a cosine.

## Why this lexical path

ACOS already has search:

- Tutorials: PostgreSQL full-text search in `TutorialSearchRepositoryImpl`, owner-scoped, `websearch_to_tsquery` and `ts_rank`. It returns a topic, a snippet, and a rank. It does not return Phase 3 chunk ids.
- Notes: `KnowledgeServiceImpl.search` matches title or summary. It is not full-text over the note body.

The chat path is Java calling Python. Python does not open the ACOS database. Calling the tutorial search API from Python would search tutorials only, miss notes, and return documents rather than the chunks the context builder uses. A matching chunk id is required so a hit found by both legs collapses to one candidate.

Lexical retrieval therefore runs on the Phase 3 index through `VectorStorePort.keyword_search`, then re-ranks those chunks in process. Tokenization keeps class names, property names, and phrases intact. It does not stem them. This is retrieval over the AI index, not a second product search engine. Tutorial full-text search remains the tutorials UI search.

PostgreSQL full-text search is not replaced. Elasticsearch and OpenSearch were not added.

## Vector retrieval

`VectorRetriever` is unchanged in role. It still embeds with the indexing model and searches `VectorStorePort`. Hybrid calls it with `RAG_VECTOR_TOP_K`. The similarity threshold remains `RAG_MIN_SCORE`.

## Fusion

Default strategy is reciprocal rank fusion:

```
RRF(chunk) = Σ 1 / (RAG_RRF_K + rank)
```

Ranks are 1-based and come from each leg's order. A chunk found by both legs keeps `retrieval_sources = ("lexical", "vector")` and one metadata record. Identity is `content_id` + `chunk_id`.

`RAG_FUSION_STRATEGY=weighted` min-max normalizes each leg on its own, then applies `RAG_LEXICAL_WEIGHT` and `RAG_VECTOR_WEIGHT`. A missing leg contributes 0. A leg whose scores are equal normalizes every member to 1. Weighted fusion is not the default because the two raw score ranges are not comparable.

## Reranking

`RagReranker` is the port. Chat uses it only when `RAG_RERANKING_ENABLED=true`.

| `RERANKER_PROVIDER` | Behavior |
| --- | --- |
| `none` | Keep fused order |
| `identity` (default) | Local token and phrase overlap. No download |
| `cross_encoder` or `bge` | Local `sentence-transformers` cross-encoder, model `RAG_RERANK_MODEL` or `cross_encoder_model` |
| `cohere` | Not called. The chat path uses the local overlap reranker instead |

A reranker timeout or exception keeps the fused order and logs `rag.reranker.degraded`. The answer is not presented as a failed request.

## Authorization

Each leg is asked for the caller's `owner_id`. Hybrid then drops any chunk whose `owner_id` does not match, before fusion and before the reranker. Foreign text never reaches the context builder or the model. The public source list is still built only from retrieval metadata.

## Failure behavior

| Condition | Result |
| --- | --- |
| Lexical fails, vector succeeds | Vector hits only. Logged as degraded |
| Vector fails, lexical succeeds | Lexical hits only. Logged as degraded |
| Both fail | `503` `AI_KNOWLEDGE_UNAVAILABLE` |
| No authorized hits | Phase 4 no-context answer. The model is not called |
| Reranker fails | Fused order |

## Configuration

| Variable | Default |
| --- | --- |
| `RAG_HYBRID_ENABLED` | `true` |
| `RAG_LEXICAL_TOP_K` | `20` |
| `RAG_VECTOR_TOP_K` | `20` |
| `RAG_FUSION_STRATEGY` | `rrf` |
| `RAG_RRF_K` | `60` |
| `RAG_FUSION_TOP_K` | `20` |
| `RAG_LEXICAL_WEIGHT` / `RAG_VECTOR_WEIGHT` | `0.5` |
| `RAG_RERANKING_ENABLED` | `false` |
| `RAG_RERANK_CANDIDATE_K` | `20` |
| `RAG_RERANK_TOP_K` | `10` |
| `RAG_RERANK_MIN_SCORE` | unset |
| `RAG_LEXICAL_TIMEOUT_SECONDS` | `5` |
| `RAG_RERANK_TIMEOUT_SECONDS` | `5` |

`RAG_TOP_K` is still the final chunk count passed to the context builder. The public response does not add fusion scores, rerank scores, or a debug payload. Ranks and content ids are written at debug level, not the chunk text.

## Observability

Hybrid metrics use the existing Prometheus registry: lexical and vector leg counts, hybrid outcome, no-result count, reranker calls and failures, and latency for the lexical leg, vector leg, fusion, reranker, and total retrieval. Candidate counts are recorded before and after the owner filter. Spans follow the existing tracer: `ai.query_processing`, `ai.lexical_retrieval`, `ai.vector_retrieval`, `ai.authorization_filter`, `ai.result_normalization`, `ai.result_deduplication`, `ai.result_fusion`, `ai.reranking`, plus the Phase 4 context and model spans.

## Evaluation

`HybridEvalCase` and `score_observation` extend the Phase 4 recall, precision, and MRR helpers with citation precision and a groundedness check. `tests/fixtures/hybrid_eval_cases.json` holds eight question shapes. A unit test scores a hand-built fixture. That measurement is not a claim about production ACOS content. The default hashing embedder is still not a semantic model.

## Security

Retrieved text stays in the user turn, outside the system instruction. Source URLs still come from index metadata. Relative ACOS paths are the only links the UI opens. Owner filtering runs on the server before context is built.
