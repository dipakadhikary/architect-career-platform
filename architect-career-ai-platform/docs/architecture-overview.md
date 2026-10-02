# Architecture Overview

The AI Platform follows Clean Architecture with inward-only dependencies.

```text
api -> orchestration -> intelligence (ports)
                 \-> shared
infrastructure -> implements intelligence ports
```

- **API**: transport, validation, Problem Details, metrics, health
- **Orchestration**: workflow/graph/agent extension points
- **Intelligence**: reusable ports (LLM, RAG, tools, memory, evaluation)
- **Infrastructure**: vendor adapters (OpenAI, Azure, Ollama, Qdrant, Redis, Langfuse, HTTPX)
- **Shared**: config, logging, security, DI, context, observability

Business domains remain external consumers.
