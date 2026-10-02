# Component Diagram

## Purpose

This diagram shows the major runtime components inside the single ACOS Spring Boot process and their relationships to PostgreSQL.

## Component overview

```mermaid
flowchart TB
    subgraph Clients
        U[HTTP Client / User]
        S[Swagger UI Consumer]
        O[Ops / Actuator Consumer]
    end

    subgraph ACOS["ACOS Spring Boot Application"]
        CF[CorrelationIdFilter]
        SEC[Spring Security Filter Chain]
        JWT[JwtAuthenticationFilter]
        CTRL[Feature Controllers]
        AUTH_C[Auth Controller]
        SVC[Application Services]
        MAP[MapStruct Mappers]
        REPO[Spring Data Repositories]
        GEH[GlobalExceptionHandler]
        EVT[Career Domain Event Publisher]
        FLY[Flyway Migrator]
        ACT[Actuator Endpoints]
        DOC[SpringDoc OpenAPI]
    end

    DB[(PostgreSQL schema acos)]

    U --> CF
    S --> DOC
    O --> ACT
    CF --> SEC
    SEC --> JWT
    JWT --> AUTH_C
    JWT --> CTRL
    AUTH_C --> SVC
    CTRL --> SVC
    SVC --> MAP
    SVC --> REPO
    SVC --> EVT
    REPO --> DB
    FLY --> DB
    CTRL -.-> GEH
    AUTH_C -.-> GEH
    SVC -.-> GEH
```

## Component responsibilities

| Component | Responsibility |
| --- | --- |
| CorrelationIdFilter | Propagates `X-Correlation-Id` into MDC |
| Spring Security + JWT filter | Authenticates requests; permits public auth/docs/health paths |
| Controllers | Thin HTTP adapters under `/api/v1`; return `ApiResponse` |
| Application Services | Ownership checks, transactions, domain orchestration |
| MapStruct Mappers | Entity ↔ DTO conversion |
| Repositories | JPA persistence boundary |
| GlobalExceptionHandler | Maps exceptions to `ApiResponse` failures |
| CareerDomainEventPublisher | Publishes career domain events after commit |
| Flyway | Applies SQL migrations at startup |
| Actuator / SpringDoc | Ops and API documentation endpoints |

## Layering inside a feature

```mermaid
flowchart LR
    C[controller] --> S[service]
    S --> V[validator]
    S --> M[mapper]
    S --> R[repository]
    R --> E[entity]
    S --> X[exception]
    C --> D[dto]
    M --> D
    M --> E
```

Career additionally includes `state`, `specification`, and `event` components used by career services.

## Cross-cutting components

```mermaid
flowchart TB
    subgraph Common["com.acos.common"]
        API[ApiResponse / ApiError]
        EX[BusinessException / ErrorCode]
        H[GlobalExceptionHandler]
        L[CorrelationIdFilter]
        P[BaseEntity]
    end

    subgraph Config["com.acos.config"]
        OA[OpenApiConfiguration]
        JA[JpaAuditingConfiguration]
    end

    CTRL[Feature Controllers] --> API
    CTRL --> H
    SVC[Services] --> EX
    ENT[Entities] --> P
    APP[Application] --> OA
    APP --> JA
    APP --> L
```
