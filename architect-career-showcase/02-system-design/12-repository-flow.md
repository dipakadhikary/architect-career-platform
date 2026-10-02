# Repository Flow

## Persistence access path

```mermaid
flowchart TB
    SVC[Service Impl] --> REPO[Spring Data Repository]
    REPO --> JPA[Spring Data JPA / Hibernate]
    JPA --> DB[(PostgreSQL schema acos)]
    FLY[Flyway at startup] --> DB
```

Controllers never participate in this path.

## Ownership query flow

```mermaid
flowchart LR
    S[Service] -->|ownerId + id| R[Repository]
    R -->|findByIdAndOwnerId| DB[(acos)]
    DB -->|row| R
    R -->|Optional entity| S
    S -->|empty| EX[*NotFoundException]
    S -->|present| USE[Continue business logic]
```

## Create/update unit of work

```mermaid
sequenceDiagram
    participant S as Service @Transactional
    participant R as Repository
    participant EM as JPA Persistence Context
    participant DB as PostgreSQL

    S->>R: save(entity)
    R->>EM: persist / merge
    Note over S,EM: Flush occurs before commit
    S-->>S: return mapped DTO
    S->>DB: commit transaction
```

Optimistic locking is enforced by `BaseEntity.version` (`@Version`). Concurrent updates that violate the version fail at flush/commit time through JPA optimistic-lock semantics.

## Career search via Specifications

```mermaid
flowchart TB
    CTRL[GET /applications/search] --> SVC[JobApplicationServiceImpl.search]
    SVC --> SPEC[JobApplicationSpecifications composition]
    SPEC --> OWN[ownedBy]
    SPEC --> ARCH[notArchived]
    SPEC --> FILT[company/recruiter/status/round/dates/salary/keyword]
    OWN --> AND[Specification.and ...]
    ARCH --> AND
    FILT --> AND
    AND --> REPO[JobApplicationRepository.findAll spec pageable]
    REPO --> DB[(PostgreSQL)]
    REPO --> PAGE[Page of JobApplication]
    PAGE --> MAP[CareerMapper.toPageResponse]
```

## EntityGraph read flow

When responses need associations (company, recruiter, tags, milestones), repositories use `@EntityGraph` or equivalent fetch strategies because OSIV is disabled.

```mermaid
flowchart LR
    S[Service get/list] --> R[Repository findWithDetails... / EntityGraph query]
    R --> DB[(Join/fetch associations)]
    DB --> E[Managed entity + initialized associations]
    E --> M[Mapper]
    M --> DTO[Response DTO]
```

## Soft-archive query flow (career applications)

```mermaid
flowchart TB
    LIST[list archived=false] --> Q1[findByOwnerIdAndArchived false]
    ARCH[list archived / archived endpoint] --> Q2[findByOwnerIdAndArchived true]
    SEARCH[search] --> SPEC[Specifications include notArchived]
    Q1 --> DB[(career_job_applications)]
    Q2 --> DB
    SPEC --> DB
```

Physical delete is not used for job applications; `archive()` sets `archived` and `archivedAt`.

## Repository interface style

```mermaid
classDiagram
    class JpaRepository
    class JpaSpecificationExecutor
    class JobApplicationRepository {
        <<interface>>
        findByIdAndOwnerId()
        findByOwnerIdAndArchived()
        findWithDetailsByIdAndOwnerId()
        countByOwnerIdAndArchivedFalse...
    }
    JpaRepository <|-- JobApplicationRepository
    JpaSpecificationExecutor <|-- JobApplicationRepository
```

Most feature repositories extend only `JpaRepository`. Career application search additionally extends `JpaSpecificationExecutor`.

## Evidence

- Feature `*Repository` interfaces under `com.acos.*.repository`
- `JobApplicationSpecifications`
- `BaseEntity` `@Version`
- `application.yml` (`open-in-view: false`, `ddl-auto: validate`)
- Flyway migrations defining FK/indexes for repository-backed tables
