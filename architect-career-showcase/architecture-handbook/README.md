# ACOS Architecture Handbook

This handbook extracts **implementation standards** from the ACOS codebase and build configuration.

It documents how the platform is actually built today. It does **not** invent aspirational rules.

Companion ADRs live in [`../adr`](../adr).

## Chapters

| Chapter | Topic |
| --- | --- |
| [01 — Package Structure](./01-package-structure.md) | Feature packages and shared layers |
| [02 — Coding Standards](./02-coding-standards.md) | Formatting, Checkstyle, DI, Java baseline |
| [03 — Exception Handling](./03-exception-handling.md) | ErrorCode, BusinessException, global handler |
| [04 — DTO Guidelines](./04-dto-guidelines.md) | Records, validation, ApiResponse |
| [05 — Repository Guidelines](./05-repository-guidelines.md) | Spring Data, ownership, graphs |
| [06 — REST Standards](./06-rest-standards.md) | Controllers, versioning, HTTP conventions |
| [07 — Logging](./07-logging.md) | Correlation IDs, SLF4J patterns |
| [08 — Security](./08-security.md) | JWT, filter chain, ownership |
| [09 — Testing](./09-testing.md) | Unit, WebMvc, DataJpa, integration |
| [10 — Flyway](./10-flyway.md) | Schema ownership and migrations |
| [11 — Swagger](./11-swagger.md) | SpringDoc / OpenAPI |
| [12 — MapStruct](./12-mapstruct.md) | Mapper conventions |

## Scope discipline

- Platform-wide standards appear across multiple features.
- Career-only patterns (state machine, Specifications, after-commit events, soft archive, business audit log, DTO `version` fields) are called out explicitly and are **not** automatic requirements for other modules unless promoted later.
