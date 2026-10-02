# System Design — Index

Chapter 02 provides enterprise system design for ACOS based on implemented repositories.

| Document | Focus |
| --- | --- |
| [High-Level-Architecture.md](High-Level-Architecture.md) | Components, boundaries, lifecycle, deployment overview |
| [Low-Level-Architecture.md](Low-Level-Architecture.md) | Backend/frontend/AI layers, config, cache, errors, logs, tracing |
| [C4-Context.md](C4-Context.md) | Actors and systems |
| [C4-Container.md](C4-Container.md) | Deployable containers and ports |
| [C4-Component.md](C4-Component.md) | Major components inside containers |
| [Runtime-Architecture.md](Runtime-Architecture.md) | Runtime sequences for key journeys |
| [Deployment-Architecture.md](Deployment-Architecture.md) | Local Docker topology; production/K8s as future |
| [Data-Architecture.md](Data-Architecture.md) | PostgreSQL, Redis, vectors, ownership |
| [Integration-Architecture.md](Integration-Architecture.md) | REST, Feign, OpenAPI, AsyncAPI readiness |
| [Security-Architecture.md](Security-Architecture.md) | Authn/z, JWT, AI security, correlation |
| [AI-System-Architecture.md](AI-System-Architecture.md) | RAG, LangGraph, router, guardrails, MCP/A2A readiness |
| [Repository-Interaction.md](Repository-Interaction.md) | Cross-repo hops and isolation |
| [Request-Flows.md](Request-Flows.md) | End-to-end sequence diagrams |

Integrity rule: implemented facts are stated as current; incomplete seams are labeled **future enhancement**.

Return to portfolio home: [../README.md](../README.md)
