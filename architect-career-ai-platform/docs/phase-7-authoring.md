# Phase 7 — AI content authoring

AI can draft knowledge. It cannot publish it. ACOS remains the owner of notes, authorization, Markdown storage, and the version that gets indexed.

## Architecture

```
User
  -> Knowledge editor
  -> Java AI API
  -> Knowledge service (ownership check when a note id is present)
  -> Python authoring service
  -> Hybrid retrieval when requested
  -> LLM
  -> Proposal store
  -> Editor review
  -> Explicit knowledge update
  -> Existing knowledge events
  -> Re-index
```

There is no generate-then-save path. Accept marks a proposal `APPROVED`. Save to ACOS is a separate call to `PUT /api/v1/knowledge/notes/{id}`.

## Operations

One `ContentAuthoringService` and one versioned prompt registry (`content` templates at `v1`):

- `GENERATE`
- `IMPROVE`
- `REWRITE`
- `SUMMARIZE`
- `EXPAND`
- `GENERATE_QA`
- `GENERATE_EXAMPLES`
- `GENERATE_EXPLANATION`
- `GENERATE_OBJECTIVES`
- `GENERATE_PREREQUISITES`
- `SUGGEST_STRUCTURE`
- `GENERATE_CODE`

Q&A must be JSON with `question`, `answer`, `difficulty`, and `explanation`. Difficulty is `BEGINNER`, `INTERMEDIATE`, or `ADVANCED`, matching the learning quiz vocabulary. It is draft metadata. The saved note is still Markdown, which is the knowledge content model. Code and examples must use fenced blocks. Generated code is not executed. Image generation is not part of ACOS and is not added here.

## Prompts

`app/orchestration/authoring/prompts.py` keeps templates out of the service. The system section outranks user instructions, source content, retrieved knowledge, and conversation history. Those last three are labeled untrusted data. The system text is not returned by the API.

## RAG

When `useKnowledge` is true and RAG is enabled, authoring calls the Phase 5 retriever with the caller owner id, then the Phase 4 context builder. Sources are `sources_from_chunks`. If nothing is retrieved, the proposal is `grounded: false` and includes a warning. The model is not asked to invent URLs.

Conversation context is included only when `useConversation` is true. It uses the Phase 6 history window and rejects another user's conversation.

## Proposal lifecycle

Proposals live in the AI SQLite file (`CONVERSATION_DATABASE`), table `ai_authoring_proposal`. They are not knowledge notes. `proposalId` is not a content id.

States: `GENERATED`, `EDITING`, `APPROVED`, `REJECTED`.

Rejected rows stay in the AI database for audit. They are not published content and they are not copied into Postgres. Regeneration inserts a new proposal and leaves the previous one unchanged.

Audit fields on the proposal: owner, operation, source note id, source version, model, provider, prompt version, token counts, timestamps, and status. The system prompt is not stored. Provider credentials are not stored.

## Save and versions

Java resolves `contentId` through `KnowledgeService.get` for the authenticated user before Python sees the note. A missing or unowned note is 404 and the model is not called. Client-supplied source text is ignored when a note id is present.

`KnowledgeNoteResponse.version` is the existing JPA `@Version`. `expectedVersion` on update returns `409 VERSION_CONFLICT` when the note changed. The editor tells the user to reload and reconcile. The stale proposal does not overwrite the newer note.

## Limits and observability

Existing HTTP rate limiting covers authoring routes. Output tokens, temperature, input size, output size, and question count are settings (`AI_AUTHORING_*`). Question requests above the cap are rejected by validation of the model output when they exceed `AI_AUTHORING_MAX_QUESTIONS`. The service also clamps the prompt's requested count to that cap.

Metrics use the `acos_ai_` registry: authoring requests, success, failure, regeneration, validation failure, approval, rejection, latency, and input/output tokens. Spans: `ai.authoring.request`, `ai.authoring.context`, `ai.authoring.retrieval`, `ai.authoring.llm`, `ai.authoring.validation`.

## Frontend

The knowledge note page has AI Assist. The dialog is labeled as an AI-generated draft that is not published. Improve, rewrite, and expand show the current note beside the proposal. The user can edit, regenerate, reject, or accept. Save to ACOS stays disabled until accept, then calls the existing knowledge update with `expectedVersion`.

## API

Python `/api/v1/ai/authoring/proposals` and the same paths under Java `/api/v1/integration/ai/authoring/proposals`:

- `POST` generate
- `GET /{id}`
- `PATCH /{id}` edit
- `POST /{id}/regenerate`
- `POST /{id}/accept`
- `POST /{id}/reject`

Responses include `authoritative: false`.
