# Runtime Architecture

Runtime interactions among implemented containers and components.

## Runtime topology

```mermaid
flowchart LR
  B[Browser]
  W[Web SPA]
  S[Business API]
  P[(PostgreSQL)]
  A[AI API]
  L[LLM Provider]
  R[(Redis)]
  V[(Qdrant/Memory)]

  B --> W --> S --> P
  S -.->|enabled?| A
  A --> L
  A --> R
  A --> V
```

---

## User login

```mermaid
sequenceDiagram
  participant U as User
  participant W as Web
  participant B as Business Auth
  participant DB as PostgreSQL

  U->>W: Submit credentials
  W->>B: POST /api/v1/auth/login
  B->>DB: Verify user (BCrypt)
  B->>DB: Persist refresh token hash
  B-->>W: Access JWT + opaque refresh
  W->>W: tokenService saves localStorage
  W-->>U: Authenticated app shell
```

Refresh: Web interceptor calls `POST /api/v1/auth/refresh` on 401, retries original request once.

---

## Knowledge search (product data)

```mermaid
sequenceDiagram
  participant W as Web
  participant B as Business Knowledge
  participant DB as PostgreSQL

  W->>B: GET /api/v1/knowledge/notes?search=... (Bearer)
  B->>B: JwtAuthenticationFilter
  B->>DB: Query notes
  B-->>W: ApiResponse list
```

This is **business search**, not vector RAG.

---

## Knowledge AI indexing (when AI enabled)

```mermaid
sequenceDiagram
  participant W as Web
  participant B as Business Knowledge
  participant DB as PostgreSQL
  participant L as IndexingListener
  participant F as KnowledgeAiFacade
  participant AI as AI Platform
  participant PIPE as Enterprise Pipeline
  participant KS as KnowledgeService

  W->>B: POST note create/update
  B->>DB: Persist note
  B->>B: Publish KnowledgeCreated/Updated after commit
  B-->>W: ApiResponse success
  L->>F: indexKnowledge (async)
  F->>AI: POST /api/v1/ai/knowledge/index
  AI->>PIPE: PipelinedKnowledgeFacade
  PIPE->>KS: ingest→chunk→embed→upsert
  KS-->>AI: document_id/status
  AI-->>F: contract response
```

If `ai.platform.enabled=false`, Feign clients are not registered; facades use graceful fallbacks and indexing does not reach AI.

---

## Resume generation (AI path)

```mermaid
sequenceDiagram
  participant S as Business CareerAiService
  participant F as CareerAiFacade
  participant AI as AI Platform
  participant PIPE as Pipeline
  participant AG as AgenticOrchestrationService

  S->>F: generate resume request
  F->>AI: POST /api/v1/ai/career/resume/generate
  AI->>PIPE: policy/guardrails/route/execute
  PIPE->>AG: workflow resume
  AG-->>AI: content payload
  AI-->>F: Cover/Resume contract response
```

**Future enhancement:** Web-triggered path via Business `/integration/ai/**` BFF controllers (UI clients exist; public BFF largely missing).

---

## Interview analysis

Same pattern as resume: Business career AI service → `CareerAiFacade` → `POST /api/v1/ai/career/interview/analyze` → pipelined agentic workflow `interview`.

---

## Generic AI request (platform edge)

```mermaid
sequenceDiagram
  participant C as Caller Feign/Test
  participant MW as AI Middleware
  participant FAC as Pipelined Facade
  participant PIPE as AiExecutionPipeline
  participant H as Handler Service

  C->>MW: HTTP /api/v1/ai/...
  MW->>MW: CORS, correlation IDs, rate limit
  MW->>FAC: endpoint handler
  FAC->>PIPE: PipelineRequest
  PIPE->>PIPE: tenant/capability policy
  PIPE->>PIPE: sanitize + input guardrails
  PIPE->>PIPE: prompt governance (if named)
  PIPE->>PIPE: route + model/execution policy
  PIPE->>H: resilient execute
  PIPE->>PIPE: output guardrails, mask, cost, eval, audit
  PIPE-->>FAC: output
  FAC-->>C: contract response / Problem Details on error
```

---

## Contract generation (build-time runtime)

```mermaid
sequenceDiagram
  participant Dev as Engineer/CI
  participant Spec as OpenAPI/AsyncAPI YAML
  participant Maven as mvn verify
  participant Out as target/generated/*
  participant Sync as AI Platform third_party sync

  Dev->>Spec: Edit contracts
  Dev->>Maven: Validate Contracts and Generate
  Maven->>Out: Java/Python/TypeScript (+ asyncapi)
  Dev->>Sync: Copy Python models into AI Platform
```

Not part of user request latency.

---

## Interview discussion

### Why async indexing after commit?

Keeps note create/update responsive; AI failures should not roll back product persistence. Listener is `@Async`.

### Alternatives

Synchronous Feign in the request thread — simpler, but couples UX latency to LLM/embedding time.

### Trade-offs

Eventual consistency between notes and vector index; failure handler currently logs with future retry noted in code comments.

### Evolution

Outbox + broker + dedicated indexer workers; Web BFF for interactive AI tools.

### Scale

Separate index worker pool; rate-limit embedding calls; shard Qdrant collections per tenant.

### Common questions

1. **Does login touch AI?** No.
2. **Does knowledge list use Qdrant?** No — PostgreSQL. Qdrant is for AI RAG paths.
