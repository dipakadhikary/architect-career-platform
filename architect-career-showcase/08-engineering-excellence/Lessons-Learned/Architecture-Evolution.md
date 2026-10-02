# Architecture Evolution

## Arc

1. Business modular monolith + Postgres + JWT  
2. Web SPA with BFF-style access to Business only  
3. AI Platform extracted for RAG/agents/governance  
4. AI Contracts repo to freeze the AI edge  
5. Showcase portfolio chapters 01–08 documenting truth  

## Important design changes

- Feign CB disabled in favor of explicit `AiPlatformInvoker` stack  
- Opaque refresh tokens hashed at rest  
- Contracts generation outputs to `target/` (not committed) with AI vendoring  
- AsyncAPI published as vocabulary before broker  


## Interview Discussion

### Why this approach?

Evolution shows learning, not a big-bang perfect design.

### Alternative approaches

Rewrite narratives. Less credible.

### Trade-offs

Interim duplication (hand Feign + generated SDK unused).

### Enterprise adoption

Plan the deletion of interim adapters.

### Scaling considerations

Extraction criteria for future services.

### Principal Architect interview questions

**Q1. Why invoker wrapping?**  
Unified retry/CB/bulkhead/timelimiter + metrics/logging.

**Q2. Why vendor Python?**  
No PyPI publish yet — file dependency is pragmatic.
