# How To Add a New Tool

1. Implement `app.intelligence.tools.ports.ToolPort` (or compose multiple tools behind one executor).
2. Keep side effects in infrastructure adapters when external systems are required.
3. Expose tool execution through orchestration, not directly from API routers in domain fashion.
