# Repository Guidelines

## Standard

Persistence access goes through Spring Data JPA repository interfaces. Controllers never talk to repositories or `EntityManager` directly.

## Conventions

1. Repository interfaces extend `JpaRepository<Entity, UUID>`.
2. User-owned resources expose ownership-aware methods such as:
   - `findByIdAndOwnerId(...)`
   - `findByOwnerId...(Pageable)` / ordered list variants
   - `existsByOwnerIdAnd...(...)`
3. Associations needed for mapping/API responses are loaded via `@EntityGraph` or carefully scoped queries (OSIV is disabled).
4. Repositories stay free of business rules; services own orchestration and authorization-by-ownership checks.

## Base persistence contract

All entities extend `BaseEntity`:

- UUID id
- `createdAt` / `updatedAt`
- `@Version` optimistic lock

Tables are mapped with `schema = "acos"`.

## Dynamic search

- Career job-application search uses `JpaSpecificationExecutor` + `JobApplicationSpecifications`.
- Other features use derived query methods or `@Query` (for example knowledge search).

Specifications are a **career** pattern today, not a mandatory platform pattern for every search endpoint.

## Rules reflected in code

1. No `EntityManager` usage in `src/main/java`.
2. Prefer repository methods that include `ownerId` for owned aggregates.
3. Keep cascade/`@EntityGraph` choices explicit and close to the query that needs them.
4. Soft archive filtering for applications is implemented in career repositories/services; do not assume soft-delete helpers exist elsewhere.

## Evidence

- `src/main/java/com/acos/common/persistence/BaseEntity.java`
- Feature `repository` packages
- `JobApplicationRepository` + `JobApplicationSpecifications` (career)
- `KnowledgeNoteRepository`, `LearningPlanRepository`, auth repositories
