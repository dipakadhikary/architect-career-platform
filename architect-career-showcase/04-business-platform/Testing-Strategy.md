# Testing Strategy

## Overview

Approximately **62** `*Test.java` classes under `src/test/java/com/acos`, organized by feature mirroring main packages, plus `testsupport`.

Dependencies: Spring Boot test, Security test, MockMvc, Mockito, Testcontainers PostgreSQL, spring-boot-testcontainers.

## Unit tests

- Service impls with mocked repositories/facades (e.g. Knowledge/Learning/Portfolio service tests)
- Integration support tests: `AiPlatformInvoker`, facades, metrics, health indicator, Feign error decoder/interceptor/properties
- State machine / pure logic tested where present

## Controller tests

- `@WebMvcTest` / MockMvc style tests for Auth, Knowledge, Learning, Portfolio, Career, Dashboard controllers
- Security context exercised for authenticated routes

## Repository / integration tests

- Testcontainers PostgreSQL used for persistence-backed tests
- Flyway migrations applied against real Postgres in those flows
- Prefer real SQL over in-memory H2 for fidelity (PostgreSQL-specific types/indexes)

## Mocking strategy

| Boundary | Approach |
| --- | --- |
| Repositories in unit tests | Mockito mocks |
| AI Platform | Mock facades/clients or disabled flag; never require live LLM |
| Security | `spring-security-test` helpers / principal stubs |
| Clock/time | Injected `Clock` in JWT provider where designed |

## Coverage goals

JaCoCo is wired (`prepare-agent`, `report`, `check`) but **minimum counters are set to `0.00`** in `pom.xml` — gates exist structurally; do not claim a hard enterprise coverage percentage today.

Quality also enforced by Spotless, Checkstyle, PMD, SpotBugs on verify.

## How to run

```bash
./mvnw test
./mvnw verify
```

Requires Docker for Testcontainers-based tests.

## Interview Discussion

### Why this architecture?

Test pyramid skewed to fast unit + focused controller tests, with Testcontainers for schema truth — appropriate for a modular monolith.

### Alternative approaches

Only `@SpringBootTest` everywhere (slow); Testcontainers for every unit (slow); H2 only (drift risk).

### Trade-offs

JaCoCo thresholds at zero mean coverage is measured but not yet enforced as a quality bar.

### Scaling considerations

Raise JaCoCo thresholds gradually; add contract tests against AI OpenAPI; add Testcontainers for Redis only if Business starts owning it (it does not today).

### Principal Architect interview questions

**Q1. How do you test AI failure soft paths?**  
Facade/invoker unit tests with mocked exceptions and disabled flag cases.

**Q2. Why PostgreSQL Testcontainers?**  
Schema, indexes, and dialects match production engine (17.x locally).
