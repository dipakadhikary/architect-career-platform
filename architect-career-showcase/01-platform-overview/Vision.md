# Vision

## Why ACOS exists

Architect career growth is usually fragmented: notes in one tool, learning plans in another, portfolio evidence elsewhere, and interview prep as ad-hoc documents. AI assistance—if used at all—is typically a personal ChatGPT session with no governance, no product boundary, and no audit trail.

**ACOS (Architect Career Operating System)** exists to make that work operable as a coherent platform:

- Career pipeline and interview evidence are first-class domain data.
- Knowledge and learning are structured, queryable, and owned by the user.
- Portfolio artifacts are managed alongside career outcomes.
- AI is a **platform capability** with contracts, isolation, and optional enablement—not a browser-side model key.

The showcase repository documents that ecosystem as an architecture portfolio.

---

## Problems solved

| Problem | How ACOS addresses it (implemented) |
| --- | --- |
| Scattered career tracking | Business Platform Career domain: companies, recruiters, applications, interviews, offers, status history with an explicit state machine |
| Unstructured knowledge | Knowledge notes with CRUD/search in Business Platform; Web knowledge UI |
| Learning without structure | Learning plans → milestones → topics with status transitions |
| Portfolio disconnected from career | Portfolio projects, skills, technologies, certifications, achievements |
| AI bolted on without boundaries | Separate AI Platform + AI Contracts; Business Platform Feign anti-corruption layer; Web must not call AI directly |
| Demonstrating enterprise architecture without a toy app | Real multi-repo system: modular monolith, SPA, AI runtime, contract-first integration |

---

## Design philosophy

1. **Product domains stay in the Business Platform**  
   Auth, knowledge, learning, portfolio, and career rules, transactions, and persistence live in Spring Boot.

2. **AI is a separate runtime**  
   Embeddings, vector search, LLM providers, agent graphs, and AI middleware live in the Python AI Platform.

3. **Contracts precede clients**  
   `architect-career-ai-contracts` owns OpenAPI/AsyncAPI. The AI Platform vendors generated Python models; Business Platform Feign paths align to the same `/api/v1/ai/**` surface.

4. **Progressive AI enablement**  
   `ai.platform.enabled` defaults to `false`. Domain flows work without AI; facades return graceful fallbacks when disabled.

5. **Architecture as evidence**  
   ACOS is also a demonstration vehicle for modular monolith design, ports-and-adapters AI, resilience patterns, and contract-first integration—suitable for principal-level review.

---

## Target audience

| Audience | What ACOS offers them |
| --- | --- |
| Software architects / senior engineers | A personal operating system for career, knowledge, learning, and portfolio |
| Principal Architects / interviewers | A multi-repo reference of trade-offs, boundaries, and implementation honesty |
| Engineering managers | A realistic split of product, UI, AI, and contracts ownership |
| Platform engineers | Patterns for AI middleware, observability hooks, and provider abstraction |

---

## Business objectives

1. Deliver a usable career OS for the core pillars (auth, knowledge, learning, portfolio, career tracking).
2. Keep AI optional, governed, and replaceable across providers.
3. Preserve a clear integration boundary via contracts.
4. Use the system itself as an architecture portfolio artifact.

Detailed goal statements: [Business-Goals.md](Business-Goals.md)

---

## Long-term vision

**Near term (partially started)**  
Close the Web → Business Platform AI BFF gap, enable safe AI-by-default in local/stage, and deepen RAG/agent quality with production auth defaults.

**Medium term (roadmap)**  
Broker-backed domain/AI events (AsyncAPI already drafted), generated SDK adoption, live analytics/dashboard, stronger CI/CD and packaging across repos.

**Long term (roadmap)**  
Richer agentic career coaching, networked MCP/A2A integrations, and multi-client consumption (mobile/CLI) against the same contracts—without collapsing AI concerns back into the Business Platform.

Nothing in the long-term section above should be read as shipped unless later chapters mark it implemented.
