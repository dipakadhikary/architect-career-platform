# Request Flows

Mermaid sequence diagrams for primary ACOS flows. Each diagram shows repository interactions as implemented.

---

## 1. User login

```mermaid
sequenceDiagram
  participant U as User
  participant Web as Web Platform
  participant BP as Business Platform
  participant DB as PostgreSQL

  U->>Web: Open /login
  Web->>BP: POST /api/v1/auth/login
  BP->>DB: Load user + verify BCrypt password
  BP->>DB: Store refresh token hash
  BP-->>Web: accessToken + refreshToken
  Web->>Web: Persist tokens in localStorage
  Web-->>U: Navigate to protected app
```

---

## 2. Create knowledge

```mermaid
sequenceDiagram
  participant Web as Web Platform
  participant BP as Business Platform
  participant DB as PostgreSQL
  participant L as KnowledgeAiIndexingListener
  participant AI as AI Platform

  Web->>BP: POST /api/v1/knowledge/notes (Bearer)
  BP->>DB: Insert note
  BP->>BP: After-commit KnowledgeCreatedEvent
  BP-->>Web: ApiResponse note
  alt ai.platform.enabled=true
    L->>AI: Feign POST /api/v1/ai/knowledge/index
    AI-->>L: INDEXED document payload
  else AI disabled
    L-->>L: Facade fallback / no Feign client
  end
```

---

## 3. Knowledge search (Business)

```mermaid
sequenceDiagram
  participant Web as Web Platform
  participant BP as Business Platform
  participant DB as PostgreSQL

  Web->>BP: GET /api/v1/knowledge/notes?query=...
  BP->>DB: Search notes
  BP-->>Web: ApiResponse hits
```

Vector RAG search is a separate AI API (`/api/v1/ai/knowledge/search`) used via Business AI facade paths—not the default notes list UI query.

---

## 4. AI chat

```mermaid
sequenceDiagram
  participant Web as Web Platform
  participant BP as Business Platform
  participant AI as AI Platform

  Web->>BP: POST /api/v1/integration/ai/chat/completions
  Note over Web,BP: Web client exists; Business BFF controller is largely future
  BP->>AI: (intended) proxy/Feign to /api/v1/ai/chat/completions
  AI->>AI: Enterprise pipeline + conversation workflow
  AI-->>BP: ChatCompletionResponse
  BP-->>Web: ApiResponse / BFF mapping
```

**Status:** AI Platform chat endpoint is implemented. Business Feign chat client/BFF and end-to-end Web chat backend wiring are **future enhancements** (Web currently also keeps local chat session state).

---

## 5. Resume generation

```mermaid
sequenceDiagram
  participant Caller as Business Career AI service
  participant Facade as CareerAiFacade
  participant AI as AI Platform
  participant Pipe as Enterprise Pipeline
  participant WF as Agentic workflow resume

  Caller->>Facade: generateResume(...)
  Facade->>AI: POST /api/v1/ai/career/resume/generate
  AI->>Pipe: policy/guardrails/route
  Pipe->>WF: execute
  WF-->>AI: content/format
  AI-->>Facade: ResumeResponse model
```

Web UI path depends on Business `/integration/ai/**` BFF completion.

---

## 6. Interview analysis

```mermaid
sequenceDiagram
  participant Caller as Business Career AI service
  participant Facade as CareerAiFacade
  participant AI as AI Platform

  Caller->>Facade: analyzeInterview(...)
  Facade->>AI: POST /api/v1/ai/career/interview/analyze
  AI->>AI: Pipeline + interview workflow
  AI-->>Facade: summary/strengths/improvements/score
```

---

## 7. Learning recommendation

```mermaid
sequenceDiagram
  participant Caller as Business Learning AI service
  participant Facade as LearningAiFacade
  participant AI as AI Platform

  Caller->>Facade: recommendNextTopic(...)
  Facade->>AI: POST /api/v1/ai/learning/topics/recommend-next
  AI->>AI: Pipeline + recommend_topic workflow
  AI-->>Facade: topic/rationale/relatedTopics
```

Related implemented AI learning endpoints: quiz generate, progress evaluate.

---

## 8. Portfolio review

```mermaid
sequenceDiagram
  participant Caller as Business Portfolio AI service
  participant Facade as PortfolioAiFacade
  participant AI as AI Platform

  Caller->>Facade: reviewPortfolio(...)
  Facade->>AI: POST /api/v1/ai/portfolio/review
  AI->>AI: Pipeline + portfolio_review workflow
  AI-->>Facade: summary/strengths/improvements/score
```

Skill-gap analysis follows the same facade pattern to `/api/v1/ai/portfolio/skill-gap/analyze`.

---

## Repository interaction legend

| Participant | Repository |
| --- | --- |
| Web Platform | `architect-career-web` |
| Business Platform | `architect-career-operating-system` |
| AI Platform | `architect-career-ai-platform` |
| PostgreSQL | Business data plane |

Contracts participate at build/sync time, not in these runtime sequences.

---

## Interview discussion

### Why show “future” edges in sequence diagrams?

Interviewers must see seams that are designed but unfinished—especially Web AI BFF—without claiming production E2E AI UX.

### Alternatives

Omit incomplete flows — would hide the integration strategy.

### Trade-offs

Documentation complexity vs honesty.

### Evolution

Replace future notes with solid lines as BFF/Feign chat land; add AsyncAPI event sequences when brokered.

### Scale

Convert synchronous Feign AI assists into asynchronous job APIs for heavy resume/interview workloads; keep login/CRUD sequences short.

### Common questions

1. **Which flow never touches AI?** Login and pure Business CRUD searches.
2. **Which flow is eventually consistent?** Knowledge create → vector index.
3. **Where do contracts appear in sequences?** They don’t at runtime; they constrain AI request/response shapes used by Feign/AI handlers.
