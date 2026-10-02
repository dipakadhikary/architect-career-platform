# How To Add a New Agent

1. Implement `app.intelligence.agents.ports.AgentPort`.
2. Register orchestration wiring in `app.orchestration.container`.
3. Keep prompts/tools behind ports.
4. Business domains call the platform API/orchestration boundary, never infrastructure.
