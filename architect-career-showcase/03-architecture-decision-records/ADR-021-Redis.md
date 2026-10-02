# ADR-021: Redis

# Status

Accepted (AI Platform)

# Date

2026-08-10

# Context

AI needs low-latency cache/memory primitives distinct from PostgreSQL.

# Problem Statement

Without a cache tier, repeated embeddings/responses and agent memory become expensive/slow.

# Decision Drivers

- Performance
- Cost
- Operational Complexity

# Alternatives Considered

- **No cache** — Rejected for AI platform efficiency goals.
- **Cache in PostgreSQL** — Wrong latency profile.
- **Business Platform Redis for sessions** — Not implemented; Web uses localStorage tokens today.

# Decision

Use Redis in AI Platform for semantic/RAG caches and agentic memory when enabled; Business Platform does not depend on Redis.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Optional via flag.
- Supports readiness checks.
- Separates AI cache from SoR.

# Negative Consequences

- Port/default mismatches possible locally (6379 vs settings).
- In-memory rate limiting still not Redis-backed.

# Trade-offs

- Another dependency for AI DX/prod fidelity.

# Risks

- Treating Redis as Business session store without implementation.

# Future Evolution

- Redis-backed distributed rate limiting; Business cache if needed.

# References

- `architect-career-ai-platform/app/infrastructure/cache/redis_adapter.py`
- `architect-career-ai-platform/docker-compose.yml`

## Interview Discussion

### Why was this approach selected?

Redis is an AI performance tier, not the product SoR.

### When would you choose another approach?

Skip Redis for pure in-memory demo modes when acceptable.

### How would this decision change for 10x / 100x / 1000x users?

At scale, Redis clusters for cache + rate limits; careful TTLs and stampede control.

### Common Principal Architect interview questions

**Q1. Does login require Redis?**

No.

### Common follow-up questions

- Which AI features use Redis?

