# Persistence Architecture

## PostgreSQL

- Engine used locally: **PostgreSQL 17.5** (Docker image)
- Logical schema: **`acos`**
- JDBC URL pattern (local profile): `jdbc:postgresql://host:port/db?currentSchema=acos`
- Hibernate `default_schema: acos`
- `open-in-view: false`
- `ddl-auto: validate` — schema owned by Flyway

## Flyway

| Setting | Value |
| --- | --- |
| Enabled | true |
| Locations | `classpath:db/migration` |
| Validate on migrate | true |
| Local schemas | `acos`, history table `flyway_schema_history` |

### Migrations (implemented)

| Version | Purpose |
| --- | --- |
| V1 | Initial schema bootstrap |
| V2 | Users and roles |
| V3–V4 | Refresh tokens (+ hash column alter) |
| V5 | Knowledge tables |
| V6 | Learning tables |
| V7 | Portfolio tables |
| V8–V9, V11–V12 | Career tables / enhancements / hardening |
| V10 | *(not present in tree — numbering jumps)* |
| V13 | Tutorial topics / concepts / questions + PostgreSQL FTS (expression GIN indexes) |

Tables include owner FKs to `users`, unique constraints per owner where needed, and indexes on `owner_id` and common lookup columns.

## JPA

- Entities extend `BaseEntity`: UUID id, `created_at`, `updated_at`, `@Version`
- `@EnableJpaAuditing` with custom date-time provider
- MapStruct for DTO mapping
- Relationships: M:N join tables for tags, project technologies, user roles

## Specifications

Career application search uses JPA **Specifications** under `career.specification` for optional filters without query-method explosion.

## Optimistic locking

`BaseEntity.version` (`@Version`) on essentially all aggregates — concurrent updates fail with optimistic lock exceptions (handled as platform errors via generic/internal mapping paths as applicable).

## Soft delete / archive

- **Not** a global `@SQLDelete` soft-delete framework on `BaseEntity`
- **Career domain** implements soft-archive via `archived` / `archived_at` and `archive()` on: `Company`, `Recruiter`, `JobApplication`, `Interview`, `Offer`, and `ApplicationStatusHistory` (Flyway V11–V12, including soft-delete-aware uniqueness/indexes)
- Application list APIs expose active vs archived views; company/recruiter/interview/offer deletes archive rather than hard-delete
- Knowledge/learning/portfolio deletes remain **hard deletes** as implemented in services
- Soft-delete policy is therefore **career-scoped**, not platform-wide

## Transactions

- Service-layer `@Transactional` boundaries (Spring defaults on service impls)
- Domain events published **after commit** via `AfterCommitEventPublisher` so AI indexing cannot roll back CRUD

## Indexes

Flyway creates indexes such as:

- `owner_id` on user-owned tables
- Title/summary composites for knowledge search aids
- Career hardening indexes in later migrations

## Auditing

| Mechanism | Scope |
| --- | --- |
| `created_at` / `updated_at` | All `BaseEntity` subtypes |
| `CareerAuditLog` entity/table | Career-specific audit trail |
| Application status history | Explicit history rows on transitions |

No Spring Data Envers entity revisioning module detected.

## Repository relationships (conceptual)

```mermaid
erDiagram
  users ||--o{ knowledge_notes : owns
  users ||--o{ learning_plans : owns
  users ||--o{ portfolio_projects : owns
  users ||--o{ career_job_applications : owns
  learning_plans ||--o{ learning_milestones : contains
  learning_milestones ||--o{ learning_topics : contains
  career_job_applications ||--o{ career_interviews : has
  career_job_applications ||--o{ career_offers : has
  career_job_applications }o--|| career_companies : targets
```

## Interview Discussion

### Why this architecture?

PostgreSQL + Flyway + JPA is the enterprise default for transactional career data with evolvable schema and strong constraints.

### Alternative approaches

NoSQL for notes; separate DB per module; jOOQ instead of JPA. Rejected for consistency and team familiarity at this stage.

### Trade-offs

JPA lazy loading requires careful fetch plans (`open-in-view: false`). Career schema evolved across multiple migrations — expect some historical duplication in V8/V11 naming.

### Scaling considerations

Read replicas, partitioning by owner, and outbox tables before multi-DB split.

### Principal Architect interview questions

**Q1. Why UUID primary keys?**  
Stable public identifiers without sequence leakage; well-suited to distributed clients.

**Q2. Is the schema the API?**  
No — DTOs are the API; schema can evolve behind Flyway with compatibility discipline.
