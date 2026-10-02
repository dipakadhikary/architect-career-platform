# ACOS Low Level Design

Low-level design for the implemented ACOS modular monolith. Diagrams use Mermaid and reflect current package, persistence, and request-flow patterns.

## Documents

| Document | Contents |
| --- | --- |
| [07 — Package Diagram](./07-package-diagram.md) | Feature package structure |
| [08 — Entity Relationships](./08-entity-relationships.md) | JPA/table relationships by domain |
| [09 — Sequence Diagrams](./09-sequence-diagrams.md) | Auth login, application create, status transition |
| [10 — Service Interaction](./10-service-interaction.md) | Service collaborators and orchestration |
| [11 — Controller Flow](./11-controller-flow.md) | Thin controller request path |
| [12 — Repository Flow](./12-repository-flow.md) | Persistence access patterns |

## Design constraints reflected in code

- Controllers → services → repositories (no repository access from controllers)
- DTOs at the HTTP boundary; entities stay in persistence/application layers
- Owner id comes from `AcosUserDetails`, not from client-supplied ownership claims
- Transactions are declared on services
- Schema `acos` is Flyway-owned; Hibernate validates only
