# AI Platform Overview

## Purpose

The AI Platform (`architect-career-ai-platform`) is a **reusable, domain-agnostic AI runtime**. It exposes contract-aligned REST APIs for knowledge indexing/search/summarize, chat, learning, career, and portfolio AI workflows. Product rules and user auth for the SPA live in the Business Platform; this service focuses on RAG, agentic orchestration, LLM/embedding adapters, and enterprise AI controls.

## Responsibilities

| Responsibility | Evidence |
| --- | --- |
| AI HTTP API on port **8090** | FastAPI `app.main`, routers under `app.api.v1` |
| Enterprise execution middleware | `AiExecutionPipeline` (guardrails → governance → route → policy → execute → eval → audit) |
| Knowledge RAG | Ingest → preprocess → chunk → embed → store → retrieve → rerank → context → prompt → generate → evaluate |
| Agentic workflows | Workflow engine (HTTP product paths) + LangGraph engine (demo graphs / HITL substrate) + capability registry + tools + memory |
| Provider abstraction | LLM, embeddings, vector store, reranker factories |
| Observability hooks | structlog JSON, Prometheus metrics, optional LangFuse, OTel TracerProvider bootstrap |
| Optional Redis | Cache / agentic memory when enabled |

**Not** this platform’s responsibility: browser UX, JWT session issuance for end users (Business owns that), PostgreSQL career SoR, or OpenAPI generation (Contracts repo).

## Technology Stack

| Area | Implemented choice |
| --- | --- |
| Language | Python **≥3.13** |
| API | FastAPI + Uvicorn |
| DI | `dependency-injector` |
| Contracts models | Vendored `acos-ai-contracts` from `third_party/` |
| Orchestration | LangGraph + LangChain-core |
| Vector | Qdrant client + in-memory store |
| Cache | Redis client |
| LLM SDKs | OpenAI, Ollama; Azure OpenAI adapter |
| Docs loaders | pypdf, python-docx, HTML/MD/JSON/text loaders |
| Observability | structlog, prometheus-client, OpenTelemetry SDK/OTLP, LangFuse client |
| Quality | pytest, ruff, black, mypy (strict), pre-commit |

## Layer responsibilities (summary)

| Layer | Package | Role |
| --- | --- | --- |
| API | `app.api` | Routes, middleware, exception handlers, auth dependencies |
| Orchestration | `app.orchestration` | Use-case facades (knowledge, agentic, enterprise pipeline wrappers) |
| Intelligence | `app.intelligence` | Ports, models, enterprise/agentic/knowledge contracts |
| Infrastructure | `app.infrastructure` | Adapters: LLM, embeddings, vector, Redis, graphs, guardrails, etc. |
| Shared | `app.shared` | Settings, DI container, logging, metrics, resilience, exceptions |

## Implemented HTTP surface

| Method | Path |
| --- | --- |
| GET | `/api/v1/ai/health` |
| POST | `/api/v1/ai/knowledge/index\|search\|summarize` |
| POST | `/api/v1/ai/chat/completions` |
| POST | `/api/v1/ai/learning/quiz/generate`, `/topics/recommend-next`, `/progress/evaluate` |
| POST | `/api/v1/ai/career/resume/generate`, `/interview/analyze`, `/cover-letter/generate` |
| POST | `/api/v1/ai/portfolio/review`, `/skill-gap/analyze` |
| GET | `/api/v1/system/liveness`, `/readiness`, `/metrics` |

## Interaction with sibling systems

### Business Platform

- Sole production caller today (OpenFeign when `ai.platform.enabled=true`).
- Propagates correlation / API key headers.
- Business never embeds LLM SDKs; AI outages must not block CRUD.

### AI Contracts

- Request/response models come from vendored generated package.
- Path shapes match OpenAPI (`/api/v1/ai/...`).

### Frontend

- **Does not call this service directly.** Web → Business only. Incomplete Business AI BFF is a Business gap, not an AI Platform gap.

### Vector database

- Default **`memory`** store for local/tests.
- Optional **Qdrant** (`qdrant_collection=acos_knowledge`) when `vector_store_provider=qdrant`.

### LLM providers

- Selected via `llm_provider`: `openai` (default), `azure_openai`, `ollama`.
- Embeddings separately default to **`hashing`** so demos work without cloud keys.

## Interview Discussion

### Why this architecture?

Separates volatile AI runtime from transactional product domains while keeping a contract-stable HTTP boundary for Java callers.

### Alternative approaches

- LangChain-only monolith inside Spring — rejected (Python ecosystem for RAG/agents).
- Per-capability microservices — premature for current surface area.
- Browser→provider — rejected for secrets and governance.

### Trade-offs

Dual-runtime ops (Java + Python). Defaults favor local DX (hashing/memory) over production RAG quality until operators reconfigure.

### Scaling considerations

Stateless API replicas + shared Qdrant/Redis; durable queues for heavy ingest later.

### How would this evolve?

Stronger auth defaults, real MCP/A2A transports, durable graph checkpoints, LLM-as-judge evaluation, collector-backed OTel.

### Principal AI Architect interview questions

**Q1. What is the system of record for career data?**  
PostgreSQL via Business Platform. Vectors are retrieval projections.

**Q2. Can the platform run without OpenAI?**  
Yes — hashing embeddings + memory store + optional Ollama/extractive fallbacks.

**Q3. Where do guardrails run?**  
Inside `AiExecutionPipeline` before/around handler execution, not only in prompts.
