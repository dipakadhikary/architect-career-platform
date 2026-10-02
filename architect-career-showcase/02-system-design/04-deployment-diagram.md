# Deployment Diagram

## Purpose

This documents the **local/runtime deployment topology** that exists in the repository today: one Spring Boot application process plus Docker Compose-managed PostgreSQL (and optional pgAdmin).

## Local deployment

```mermaid
flowchart TB
    subgraph DeveloperHost["Developer Host"]
        APP["ACOS JAR / Spring Boot process<br/>port 8080"]
        BROWSER["Browser / HTTP client"]
    end

    subgraph DockerCompose["Docker Compose: acos-local"]
        PG["postgres:17.5-alpine<br/>container acos-postgres<br/>host port 5432"]
        PGA["pgadmin4:9.4<br/>container acos-pgadmin<br/>host port 5050"]
        VOL1[(acos_postgres_data)]
        VOL2[(acos_pgadmin_data)]
        NET[[acos-network bridge]]
    end

    BROWSER -->|HTTP :8080| APP
    BROWSER -->|HTTP :5050| PGA
    APP -->|JDBC currentSchema=acos| PG
    PGA --> PG
    PG --> VOL1
    PGA --> VOL2
    PG --- NET
    PGA --- NET
```

## Process view

```mermaid
flowchart LR
    subgraph Runtime
        BOOT[Spring Boot 3.5]
        FLY[Flyway startup migrate/validate]
        JPA[Hibernate validate-only]
        SEC[Security filter chain]
        WEB[DispatcherServlet / Controllers]
        ACT[Actuator]
        DOC[SpringDoc]
    end

    DB[(PostgreSQL)]

    BOOT --> FLY
    BOOT --> JPA
    BOOT --> SEC
    BOOT --> WEB
    BOOT --> ACT
    BOOT --> DOC
    FLY --> DB
    JPA --> DB
    WEB --> DB
```

## Deployment notes (as implemented)

| Item | Implementation |
| --- | --- |
| Application packaging | Single Maven JAR (`architect-career-operating-system`) |
| Default app port | `8080` (`application.yml`) |
| Database | PostgreSQL 17.5 via Compose image `postgres:17.5-alpine` |
| DB schema | `acos` created/managed by Flyway |
| Hibernate DDL | `validate` only |
| Local admin UI | pgAdmin on port `5050` (Compose) |
| Profiles | Default `local`; tests use `test` |
| Cloud/K8s charts | Not present in this repository |

## Test runtime variant

Integration/repository tests use either:

1. Testcontainers PostgreSQL `postgres:17.5-alpine`, or
2. Local Postgres fallback via `ACOS_DB_*` environment variables

```mermaid
flowchart TB
    TEST[JUnit Integration / DataJpa Tests]
    TC{Docker available?}
    CONT[Testcontainers Postgres 17.5]
    LOCAL[Local Postgres via ACOS_DB_*]

    TEST --> TC
    TC -->|yes| CONT
    TC -->|no| LOCAL
    CONT --> SCHEMA[(schema acos + Flyway)]
    LOCAL --> SCHEMA
```

## Evidence

- `infrastructure/docker/docker-compose.yml`
- `src/main/resources/application.yml`
- `src/main/resources/application-local.yml`
- `src/test/java/com/acos/testsupport/PostgresTestSupport.java`
- `pom.xml` (single Spring Boot packaging)
