# Business Goals

Each goal below maps to domains and code that exist today. Gaps are marked as **Future roadmap**.

---

## 1. Career Management

### Business problem

Job search and interview processes are tracked in spreadsheets or memory. Status transitions are informal; interview and offer history is hard to reconstruct.

### Solution (implemented)

Business Platform `career` package and Web career feature:

- Companies and recruiters
- Applications with archive/search/status updates
- Nested interviews and offers
- Application status history / timeline
- Explicit application status **state machine** in domain code
- Career dashboard API (Web consumes it)

### Expected benefits

- Auditable career pipeline
- Consistent status transitions
- Foundation for later AI interview analysis / resume generation (AI Platform endpoints exist; BFF completion is roadmap)

---

## 2. Knowledge Management

### Business problem

Architecture notes and decisions are scattered across files and chat history, with no product-owned search or lifecycle.

### Solution (implemented)

- Business Platform Knowledge notes CRUD, list, and search
- Categories/tags via note model
- Web knowledge pages and API client
- Optional after-commit event → async AI indexing when `ai.platform.enabled=true`
- AI Platform Knowledge APIs: `/api/v1/ai/knowledge/index|search|summarize`

### Expected benefits

- Durable personal knowledge base
- Path to RAG over user notes without putting vectors inside the Business Platform database

### Future roadmap

- Robust indexing retry after failure
- End-to-end Web AI knowledge tools via Business Platform BFF controllers

---

## 3. Learning Management

### Business problem

Learning goals for architects lack structured plans, milestones, and topic progress tracking.

### Solution (implemented)

- Learning plans with nested milestones and topics
- Topic status updates
- Web learning UI and clients
- AI Platform learning workflows: quiz generation, next-topic recommendation, progress evaluation (contract + platform implemented)

### Expected benefits

- Visible learning progression
- Structured hooks for AI coaching without mixing plan CRUD into the AI service

### Future roadmap

- Learning domain events driving AI automatically beyond current hooks
- Web learning AI pages fully backed by Business Platform BFF

---

## 4. Portfolio Management

### Business problem

Portfolio evidence (projects, skills, certifications) is disconnected from career applications and hard to keep current.

### Solution (implemented)

Business Platform + Web support for:

- Projects (including search)
- Technologies, skills, certifications, achievements

AI Platform portfolio workflows exist: portfolio review, skill-gap analysis.

### Expected benefits

- Single product home for portfolio artifacts
- Ability to correlate portfolio data with career targets later

### Future roadmap

- Deeper AI portfolio recommendations and Web BFF exposure
- Event-driven portfolio review triggers (AsyncAPI channels specified, not brokered)

---

## 5. AI-assisted Productivity

### Business problem

Using public LLMs for resume, interview, or knowledge work lacks enterprise controls: no contracts, no isolation, no rate/policy layer, and often keys in the browser.

### Solution (implemented)

- Dedicated AI Platform with FastAPI contract APIs
- Enterprise pipeline: policy, guardrails, routing, cost, evaluation, audit, resilience
- Provider abstractions (OpenAI, Azure OpenAI, Ollama) and RAG stack (embeddings, vector store, rerank)
- Business Platform AI Integration Layer (Facade → Gateway → Feign → Resilience4j)
- AI Contracts OpenAPI surface for chat, knowledge, learning, career, portfolio AI
- Web AI UI shell and capability catalog (many capabilities marked planned/coming soon)

### Expected benefits

- AI can evolve without rewriting domain schemas
- Feature-flagged adoption
- Contract-stable integration for Java and Python

### Future roadmap

- Complete Business Platform BFF for Web AI routes
- Chat completions Feign + BFF path
- Real MCP/A2A networking (stubs only today)
- Broker-backed AsyncAPI processing events

---

## 6. Enterprise Architecture Demonstration

### Business problem

Architecture interviews and portfolio reviews need more than diagrams: evidence of boundaries, trade-offs, and honest delivery status.

### Solution (implemented)

The ACOS ecosystem itself:

- Modular monolith Business Platform (domain packages, shared kernel, quality gates)
- Feature-sliced Web SPA/PWA
- Ports-and-adapters AI Platform with DI and enterprise middleware
- Separate contracts repository with CI validation/generation
- This showcase repository as the architecture narrative, grounded in code

### Expected benefits

- Credible principal-level portfolio artifact
- Clear story of what shipped vs what remains roadmap
- Teaching vehicle for Clean/hexagonal AI boundaries and contract-first integration

### Future roadmap

- Formal ADR chapter, C4 deep dives, unified DevOps chapter reflecting real pipelines across all repos
