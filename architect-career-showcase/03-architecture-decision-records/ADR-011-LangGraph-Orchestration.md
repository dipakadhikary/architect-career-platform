# ADR-011: LangGraph Orchestration

# Status

Accepted

# Date

2026-08-10

# Context

Agentic workflows need explicit control flow beyond linear prompt chains.

# Problem Statement

Ad-hoc Python orchestration becomes unreadable as branching/parallel capability flows grow.

# Decision Drivers

- Extensibility
- Maintainability
- Future AI Evolution

# Alternatives Considered

- **Pure LangChain chains only** — Insufficient explicit graph control for registered capability graphs.
- **Temporal/Cadence workflows** — Heavier ops; not adopted for in-process agent graphs.
- **Custom state machines only** — Would reinvent graph execution already provided by LangGraph.

# Decision

Use LangGraph for capability graph orchestration (`capability_sequential|conditional|parallel`) alongside registered agentic workflows in the AI Platform.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Explicit graph semantics.
- Fits capability registry model.

# Negative Consequences

- Additional abstraction for engineers new to graphs.
- Checkpoints currently in-memory.

# Trade-offs

- Power vs learning curve.

# Risks

- Overusing graphs for trivial single-prompt workflows.

# Future Evolution

- Durable checkpoints; richer domain graphs.

# References

- `architect-career-ai-platform/app/infrastructure/agentic/graphs`
- `architect-career-ai-platform/app/infrastructure/agentic/factory.py`

## Interview Discussion

### Why was this approach selected?

Graphs make agent control flow inspectable and testable.

### When would you choose another approach?

Use a single LLM call when no branching exists.

### How would this decision change for 10x / 100x / 1000x users?

At scale, combine graphs with external workflow engines for long-running human-in-the-loop jobs.

### Common Principal Architect interview questions

**Q1. LangGraph vs raw LangChain?**

Graphs for orchestration; ports still isolate vendors.

### Common follow-up questions

- Where are graphs registered?

