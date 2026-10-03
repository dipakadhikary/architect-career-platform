# Phase 3 — Knowledge ingestion and embedding

Phase 3 creates the AI index but does not use it for answer generation.

`POST /api/v1/ai/chat` is unchanged. It does not search vectors, merge full-text results, or rerank. Persistent conversations are not part of this phase.

## Source of truth

ACOS Knowledge notes and tutorial concept/question rows in PostgreSQL remain authoritative. The Python service stores a derived projection: normalized Markdown chunks, embeddings, and vector metadata. Changing a note in ACOS does not edit that projection in place; the next successful index replaces it.

## Synchronization

ACOS already publishes `KnowledgeCreatedEvent` and `KnowledgeUpdatedEvent` after the business transaction commits. There is no Kafka broker, and Phase 0 did not add one. Delete previously had no event.

The business platform therefore keeps the existing after-commit Spring events and adds:

- `KnowledgeDeletedEvent` when a note is deleted
- `TutorialContentEvent` when a concept or question is saved or deleted

An async listener calls the Python index API. The note or tutorial request does not wait for embedding. If the AI service is down, the ACOS write still commits. The index stays stale until ACOS sends the document again.

Python does not read the ACOS database.

## API

All index routes require an ACOS JWT or a service credential (`X-Internal-Service` or `X-API-Key`). A browser cannot submit an arbitrary document anonymously.

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/v1/ai/index/content` | Index or replace one document |
| POST | `/api/v1/ai/index/content/{content_id}/reindex` | Same operation; path id must match the body |
| DELETE | `/api/v1/ai/index/content/{content_id}` | Remove vectors for that content id |
| GET | `/api/v1/ai/index/content/{content_id}` | Last in-process index status |
| POST | `/api/v1/ai/index/rebuild` | Accept a full rebuild. ACOS then replays documents one by one |

A JWT caller may index only `ownerId` equal to the token subject. Service credentials are used by the async listener, which often has no user token, and the `ownerId` in that trusted payload is stored on every vector.

## Pipeline

1. Normalize Markdown for the index only. Fenced code is kept. HTML tags outside fences are removed. The ACOS column is not updated.
2. Chunk on headings, then paragraphs. Code fences stay in one chunk even when they exceed the character budget. Each chunk is prefixed with the document title and section path.
3. Embed in batches of `EMBEDDING_BATCH_SIZE` through `EmbeddingPort`.
4. Delete existing vectors for the content id, then upsert replacements.

Images stay as Markdown image links. They are not sent to a multimodal embedding model.

Questions and answers are separate chunks with `qa_type` `QUESTION` or `ANSWER`. Concepts use `content_type` `CONCEPT`. Notes use `NOTE`.

## Identity, updates, and deletes

Chunk ids are UUID v5 of `content_id`, `content_version`, chunk index, and question/answer role. Indexing the same payload twice does not create a second active copy: the checksum matches and embedding is skipped. A content change replaces the previous vectors for that content id. Delete removes every vector whose document id is the content id.

Status values are `PROCESSING`, `SUCCESS`, `FAILED`, and `DELETED`. The status registry is in memory in this phase. Vectors remain in the configured store after a process restart; status does not. A failed embedding or vector write leaves status `FAILED` and does not report success. Validation errors are not retried. Timeouts, rate limits, and connection failures are retried up to `INDEX_RETRY_ATTEMPTS`.

`index_version`, the embedding model, the provider, and the chunk settings are part of the checksum. Changing them causes the next index call to rebuild that document. Do not mix dimensions in one Qdrant collection. Point a new collection at the new model and replay ACOS content through the index API.

## Authorization and external data

Every vector stores `owner_id`. Notes and tutorials are owner-scoped in ACOS; this phase does not invent a second permission model. Later retrieval must filter on that owner.

`EMBEDDING_PROVIDER=hashing` runs locally and is the default. `openai` and `azure_openai` send chunk text to that provider. `ollama` stays on the configured local server. Do not enable a remote embedding provider unless that data flow is acceptable.

## Configuration

`EMBEDDING_PROVIDER`, `EMBEDDING_MODEL`, `EMBEDDING_DIMENSIONS`, `EMBEDDING_BATCH_SIZE`, `CHUNK_SIZE`, `CHUNK_OVERLAP`, `VECTOR_STORE_PROVIDER`, `QDRANT_URL`, `QDRANT_COLLECTION`, `INDEX_VERSION`, `INDEX_RETRY_ATTEMPTS`, `INDEX_RETRY_BACKOFF_SECONDS`, `INDEX_TIMEOUT_SECONDS`.

Java sends `AI_PLATFORM_API_KEY` as `X-API-Key`. That key must be listed in `AUTH_API_KEYS` with `AUTH_API_KEY_ENABLED=true`, or the listener must send `X-Internal-Service` matching `AUTH_INTERNAL_SERVICE_TOKENS`.

## Local development

`architect-career-ai-platform/docker-compose.yml` already starts Qdrant 1.12.5. Leave `VECTOR_STORE_PROVIDER=memory` to run without it. To use Qdrant locally, set `QDRANT_ENABLED=true`, `VECTOR_STORE_PROVIDER=qdrant`, and `QDRANT_URL=http://localhost:6333`.

## Tests

Unit tests cover normalization, headings, lists, code, tables, images, links, overlap, stable ids, batching, retries, dimension mismatch, timeouts, update, delete, and question/answer metadata. The API test uses the hashing embedder and the in-memory store. No OpenAI or Qdrant process is required.

## Observability

Counters and histograms: `acos_ai_index_documents_total`, `acos_ai_index_chunks_total`, `acos_ai_embedding_requests_total`, `acos_ai_embedding_failures_total`, `acos_ai_vector_upserts_total`, `acos_ai_vector_deletes_total`, `acos_ai_index_duration_seconds`, `acos_ai_index_retries_total`. Logs include content ids and error types, not document bodies.
