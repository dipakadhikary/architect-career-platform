# ADR-010: FastAPI for AI

# Status

Accepted

# Date

2026-08-10

# Context

AI Platform requires async Python, Pydantic models, and rapid OpenAPI alignment.

# Problem Statement

Building the AI runtime on Spring would fight the AI ecosystem and slow prompt/RAG iteration.

# Decision Drivers

- Developer Experience
- Future AI Evolution
- Maintainability
- Performance

# Alternatives Considered

- **Flask** — Weaker native async/typing story for this design.
- **Django** — Heavier for a non-CRUD AI API.
- **Node NestJS AI service** — Possible, but Python remains dominant for AI libs used here.

# Decision

Implement AI Platform with FastAPI + Uvicorn + Pydantic Settings, consuming generated contract models.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Natural fit to OpenAPI/Pydantic.
- Async handlers for I/O-bound provider calls.

# Negative Consequences

- Second language operational burden.

# Trade-offs

- Polyglot clarity vs single-stack simplicity.

# Risks

- Python packaging/runtime skew (e.g., 3.13 requirement).

# Future Evolution

- Deeper streaming endpoints if product needs token streams.

# References

- `architect-career-ai-platform/app/main.py`
- `architect-career-ai-platform/pyproject.toml`

## Interview Discussion

### Why was this approach selected?

FastAPI maximizes AI ecosystem leverage while staying API-centric.

### When would you choose another approach?

Choose Spring WebFlux only if organizational constraints forbid Python services.

### How would this decision change for 10x / 100x / 1000x users?

Scale with more Uvicorn workers/pods; isolate CPU-bound embedding workers.

### Common Principal Architect interview questions

**Q1. Why not gRPC server for AI?**

Consumers are HTTP/Feign-first today.

### Common follow-up questions

- How are settings loaded?

