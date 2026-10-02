# AI Architect Interview Guide

## System sketch

FastAPI AI Platform: enterprise pipeline, RAG (optional Qdrant), Redis caches, LangGraph demos vs WorkflowEngine HTTP paths, model router, heuristic guardrails, Prometheus metrics, optional LangFuse/OTEL.

## Questions & answers

**Q: How is prompt injection handled?**  
A: HeuristicGuardrails regex + sanitizer + length limits — not a full ML moderator; acknowledge residual risk.

**Q: Semantic cache correctness across replicas?**  
A: Payloads in Redis; similarity vectors process-local today — call out as limitation.

**Q: Contract consumption?**  
A: Vendored `third_party/acos_ai_contracts` from OpenAPI generation; CI compatibility import check.

**Q: Why not call LLMs from Java?**  
A: Python AI ecosystem; keep Business domain-pure; govern AI centrally.

**Q: Eval strategy?**  
A: Unit tests deterministic; DeepEval disabled in pytest; LangFuse optional; nightly evals are roadmap.


## Interview Discussion

### Why this approach?

AI architects must separate scaffold from production controls.

### Alternative approaches

Demo only happy-path GPT wrappers. Insufficient.

### Trade-offs

Many enterprise toggles increase cognitive load.

### Enterprise adoption

Model cards; approved tool allowlists when MCP goes live.

### Scaling considerations

Queue async jobs; shared Redis rate limits.

### Principal Architect interview questions

**Q1. Default rate limit?**  
120/min in-memory.

**Q2. MCP status?**  
Stub/in-memory — no external MCP servers.
