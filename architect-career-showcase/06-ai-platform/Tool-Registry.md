# Tool Registry

## Purpose

Provide named tools that agentic workflows/reasoners can invoke through `ToolRegistryPort`.

Implementation: `DefaultToolRegistry` + builtins in `app.infrastructure.agentic.tools`.

## Built-in tools

| Tool | Role |
| --- | --- |
| `CalculatorTool` | Deterministic calc helper |
| `DocumentRetrievalTool` | Document retrieval bridge |
| `KnowledgeSearchTool` | Knowledge search bridge |
| `ResumeGenerationTool` | Resume-oriented helper |

Tools implement invoke via `ToolRequest` port types under `app.intelligence.tools`.

## Registration

`build_tool_registry` constructs the registry and registers builtins at startup. Additional tools can be registered similarly without changing HTTP contracts.

## MCP relationship

Enterprise MCP tool registry (`InMemoryMcpToolRegistry`) is a **separate** extension point for future external MCP servers — not the same object as the agentic tool registry. Today MCP has no networked servers.

## Interview Discussion

### Why this architecture?

Tools isolate side effects (search, calc) from the reasoner prompt loop and keep testing easy with fakes.

### Alternative approaches

Unrestricted Python exec tools; only LLM function-calling to HTTP. Explicit registry is safer for enterprise.

### Trade-offs

Builtin set is small; product tools may still live as workflows rather than tools.

### Scaling considerations

Timeouts per tool; policy `policy_restricted_tools` can block names; audit tool calls.

### How would this evolve?

OpenAPI-derived tools; MCP-imported tools; sandboxing.

### Principal AI Architect interview questions

**Q1. Can the LLM call arbitrary HTTP?**  
Not via an unrestricted tool today — only registered tools/workflows.

**Q2. Difference vs capability registry?**  
Tools are a capability kind used for side-effecting actions; the capability registry is the broader catalog.
