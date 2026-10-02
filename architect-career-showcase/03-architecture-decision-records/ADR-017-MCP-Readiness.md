# ADR-017: MCP Readiness

# Status

Accepted (extension-point / stub)

# Date

2026-08-10

# Context

Future tool ecosystems may standardize on Model Context Protocol.

# Problem Statement

Waiting to design ports until MCP is needed would force invasive refactors later.

# Decision Drivers

- Future AI Evolution
- Extensibility

# Alternatives Considered

- **Ignore MCP until required** — Higher refactor risk.
- **Implement full MCP server network stack now** — Rejected; no product consumer yet.

# Decision

Ship MCP client/transport/tool/resource port abstractions with in-memory stub implementations—no external MCP servers.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Seam exists without pretending network MCP is live.

# Negative Consequences

- Stubs can be misread as production MCP.

# Trade-offs

- Small abstraction cost for future optionality.

# Risks

- Interview overclaiming MCP support.

# Future Evolution

- Real MCP transports/servers when a concrete tool host is chosen.

# References

- `architect-career-ai-platform/app/infrastructure/enterprise/mcp.py`
- `architect-career-ai-platform/app/intelligence/enterprise/mcp`

## Interview Discussion

### Why was this approach selected?

Readiness ports are cheaper than speculative full protocol builds.

### When would you choose another approach?

Build MCP now only if a platform mandate requires it.

### How would this decision change for 10x / 100x / 1000x users?

Scale concerns appear only when networked tool fan-out exists.

### Common Principal Architect interview questions

**Q1. Is MCP implemented?**

Ports/stubs yes; external servers no.

### Common follow-up questions

- How would you test a future MCP adapter?

