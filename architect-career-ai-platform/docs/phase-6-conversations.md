# Phase 6 — Persistent conversations and multi-turn context

The Ask ACOS assistant can now keep a conversation. Each turn still goes through the Phase 5 hybrid retriever and the Phase 4 RAG prompt when RAG is enabled, then the existing language-model provider. RAG off keeps the Phase 2 general assistant. This phase does not add summarization, long-term memory, agents, tools, or MCP.

## Architecture

The browser talks only to the Java API. Java forwards the caller JWT and does not accept a user id in the body.

```
User
  -> Frontend
  -> Java AI API
  -> Python AI API
  -> Conversation service
  -> Message store
  -> Context builder
  -> Hybrid retrieval
  -> RAG
  -> LLM
  -> Message store
  -> Frontend
```

```
                         ACOS user
                             |
                             v
                     +---------------+
                     | ACOS Frontend |
                     +-------+-------+
                             |
                             v
                     +---------------+
                     | Java BFF      |
                     +-------+-------+
                             |
                             v
                     +---------------+
                     | Python AI API |
                     +-------+-------+
                             |
                +------------+-------------+
                |                          |
                v                          v
       Conversation service          RAG pipeline
                |                          |
                v                          |
       Conversation store                  |
                |                          |
                +------------+-------------+
                             |
                             v
                       AI orchestrator
                             |
                             v
                           LLM
```

## Why SQLite

The Python service has no ORM and must not open the ACOS Postgres database. Conversations live in one SQLite file owned by the AI process (`CONVERSATION_DATABASE`, default `.data/ai_conversations.sqlite`). That directory is gitignored. Tests use a temporary file. This is one process and one connection behind a lock. It is not a second database server.

## Domain and database

Hard delete. ACOS business records use soft delete, but a deleted conversation must not keep message text. `ON DELETE CASCADE` removes messages and source rows with the conversation.

`ai_conversation`

- `id`, `owner_id`, `title`, `status` (`ACTIVE`), `created_at`, `updated_at`
- index `(owner_id, updated_at desc)`

`ai_message`

- `id`, `conversation_id`, `owner_id`, `role` (`USER` or `ASSISTANT`), `content`, `sequence_number`
- `status` (`COMPLETED`, `PROCESSING`, `FAILED`)
- `model`, `provider`, `prompt_tokens`, `completion_tokens`
- `grounded`, `idempotency_key`, `retrieval_query`
- unique `(conversation_id, sequence_number)`
- unique `(conversation_id, idempotency_key)` when the key is present
- index `(conversation_id, sequence_number)` covers chronological reads, so a separate `created_at` index is not used

`ai_message_source`

- snapshot of retrieval metadata: `content_id`, `chunk_id`, `title`, `content_type`, `section`, `path`, `source_url`, `score`, `rank`
- no document body is copied

A user message is `COMPLETED` as soon as it is stored. The assistant row starts as `PROCESSING` in the same transaction, then becomes `COMPLETED` or `FAILED` after the model call. The database transaction is not held open during that call.

## Authorization

`owner_id` comes from the existing caller resolver (JWT `sub`, or the internal token / API key plus `X-User-Id`). The body never supplies an owner. A missing id is 404. Another user's id is 403. The conversation id is not an authorization token.

## API

Python, under `/api/v1/ai`:

- `POST /conversations`
- `GET /conversations?page&size` — newest `updated_at` first
- `GET /conversations/{id}`
- `PATCH /conversations/{id}` with `{ "title" }`
- `DELETE /conversations/{id}`
- `POST /conversations/{id}/messages` with `{ "content" }` and optional `Idempotency-Key`

Java exposes the same operations under `/api/v1/integration/ai/conversations`. Responses use the existing `ApiResponse` envelope. Pagination matches knowledge pages: `content`, `page`, `size`, `totalElements`, `totalPages`, `first`, `last`.

`GET` returns the latest `CONVERSATION_MESSAGE_LIMIT` messages (default 50) in ascending order and `truncated` when older rows exist. The list is always paged (default 20, max 50).

The response includes user text, the assistant answer, status, model, provider, grounded, and trusted sources. It does not include the system prompt, the retrieval query, vectors, or credentials.

## Message lifecycle

1. Authenticate and check ownership.
2. If `Idempotency-Key` matches a completed assistant reply, return that pair.
3. If it matches a failed assistant reply, mark that same row `PROCESSING` and generate again. A second assistant row is not inserted.
4. Otherwise insert the user message and a `PROCESSING` assistant row together. A second in-flight turn on the same conversation returns 409.
5. Build bounded history and the retrieval query.
6. Call the existing assistant. When RAG is on, that is hybrid retrieval, then the RAG prompt, then the LLM.
7. Store the answer and source snapshot, or mark the assistant `FAILED` and return 503 `AI_CONVERSATION_FAILED`.

The user message remains after a failure. The public failure text is "Sorry, I couldn't generate a response right now. Please try again." The exception type is logged; the message body is not.

A `PROCESSING` row older than `CONVERSATION_PROCESSING_TIMEOUT_SECONDS` (default 120) is marked `FAILED` so a crashed request can be retried. Without an idempotency key, a double-click can create two turns. The Ask page sends a key and reuses it on Retry.

## Context window

History selection is deterministic. No summary is generated.

- Only completed `USER` and `ASSISTANT` messages are eligible.
- Newest messages are kept, then restored to chronological order.
- `CONVERSATION_MAX_MESSAGES` (default 12) and `CONVERSATION_MAX_CHARACTERS` (default 8000) bound the history.
- The current user message is always eligible because it is already stored as completed.
- Character length is the token proxy. A tokenizer dependency was not added.

The prompt is still system instruction, prior turns, the current question, and retrieved knowledge. Retrieved text stays untrusted data. One sentence tells the model that earlier turns are prior messages, not instructions. That instruction is not returned to the client.

## Retrieval query

The stored user message always keeps the original text. Retrieval may use a longer string when the question is short (four tokens or fewer) or contains a follow-up pronoun such as "it" or "its". In that case the previous user message is prepended, still capped by `AI_MAX_MESSAGE_CHARACTERS`. The RAG prompt question remains the original text. The retrieval string is stored on the user row for audit and is not shown in the API.

## Sources

Sources are copied from retrieval metadata at generation time. The model text cannot add a URL. History shows that snapshot. It is not re-resolved against the live index, and it does not store the chunk body.

## Privacy and retention

Message content is sent to the configured LLM provider when a reply is generated. Hashing embeddings and a local model stay on this machine. An external embedding or chat provider receives the retrieval query and the bounded prompt. Full conversations are not written to logs.

Deleting a conversation removes its rows from SQLite. There is no separate retention job. SQLite is outside the ACOS Postgres backup, so operators who need conversation backup must include the AI service data directory.

## Observability

Counters and histograms use the existing `acos_ai_` registry:

- `acos_ai_conversations_created_total`
- `acos_ai_conversations_deleted_total`
- `acos_ai_messages_total` (`role`, `status`)
- `acos_ai_conversation_failures_total`
- `acos_ai_conversation_context_messages`
- `acos_ai_conversation_context_characters`
- `acos_ai_conversation_request_latency_seconds`

Spans: `ai.conversation.create`, `ai.conversation.load`, `ai.conversation.message.persist`, `ai.conversation.context.build`, `ai.assistant.message.persist`. Retrieval and LLM spans stay inside the existing RAG engine.

Provider token counts are stored as zero until the chat response carries usage. Model and provider names are stored. Secrets are not.

## Frontend

The Ask page keeps the current shell. A sidebar lists title and updated time, and supports new, rename, and delete. Delete uses the existing confirmation dialog. Answers still use `MarkdownViewer`, which sanitizes HTML, and the existing source cards. Conversation state is React state in `useAskAi`. Streaming was not added; assistant messages already have a pending status so a later stream can fill the same row.
