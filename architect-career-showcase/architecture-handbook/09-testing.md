# Testing

## Test layers used

| Layer | Typical annotations / support | Examples |
| --- | --- | --- |
| Unit | JUnit 5 + Mockito (`@ExtendWith(MockitoExtension.class)`) | `*ServiceImplTest`, validator tests, state machine tests |
| Web slice | `@WebMvcTest` | `*ControllerTest` |
| Persistence slice | `@DataJpaTest` + feature `*RepositoryTestSupport` | repository tests |
| Integration | `@SpringBootTest` + `@AutoConfigureMockMvc` + `@ActiveProfiles("test")` | `*IntegrationTest` |

## Shared test support

- `PostgresTestSupport`
  - Starts Testcontainers `postgres:17.5-alpine` when Docker is available
  - Otherwise falls back to local Postgres via `ACOS_DB_*` env defaults
  - Registers datasource, Flyway schema `acos`, and optional JWT secret
- `AuthApiTestSupport`
  - Unique email helper
  - Register / login helpers for integration tests

## Maven split

- Surefire runs `*Test` and excludes IT-style names
- Failsafe runs `*IT`, `*ITCase`, and `*IntegrationTest`

## Conventions reflected in tests

1. Integration tests exercise real security + Flyway + HTTP where relevant.
2. Repository tests use the shared Postgres wiring rather than H2.
3. Auth-protected flows obtain a bearer token through `AuthApiTestSupport`.
4. Keep unit tests free of Spring context unless the slice genuinely needs it.

## Evidence

- `src/test/java/com/acos/testsupport/PostgresTestSupport.java`
- `src/test/java/com/acos/testsupport/AuthApiTestSupport.java`
- Feature tests under `src/test/java/com/acos/**`
- `pom.xml` Surefire / Failsafe configuration
- `src/main/resources/application-test.yml`
