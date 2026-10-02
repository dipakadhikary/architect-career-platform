# Service Interaction

## Standard service collaboration

```mermaid
flowchart LR
    CTRL[Controller] --> SVC[Service Interface]
    SVC --> IMPL[Service Impl @Transactional]
    IMPL --> VAL[Validator]
    IMPL --> MAP[MapStruct Mapper]
    IMPL --> REPO[Repositories]
    IMPL --> EX[Feature Exceptions]
```

Services are the orchestration boundary. They:

1. Accept `ownerId` + DTO/command inputs
2. Validate business/limit rules
3. Load owned aggregates through repositories
4. Mutate entities
5. Persist through repositories
6. Map entities to response DTOs

## Auth service collaborators

```mermaid
flowchart TB
    AuthServiceImpl --> UserRepository
    AuthServiceImpl --> RoleRepository
    AuthServiceImpl --> PasswordEncoder
    AuthServiceImpl --> PasswordValidator
    AuthServiceImpl --> UserMapper
    AuthServiceImpl --> TokenService
    TokenService --> JwtTokenProvider
    TokenService --> RefreshTokenRepository
```

## Career job-application service collaborators

```mermaid
flowchart TB
    JobApplicationServiceImpl --> JobApplicationRepository
    JobApplicationServiceImpl --> CompanyRepository
    JobApplicationServiceImpl --> RecruiterRepository
    JobApplicationServiceImpl --> ApplicationStatusHistoryRepository
    JobApplicationServiceImpl --> CareerMapper
    JobApplicationServiceImpl --> CareerValidator
    JobApplicationServiceImpl --> ApplicationStateValidator
    JobApplicationServiceImpl --> CareerAuditService
    JobApplicationServiceImpl --> CareerDomainEventPublisher

    ApplicationStateValidator --> ApplicationStateMachine
    CareerAuditService --> CareerAuditLogRepository
    CareerDomainEventPublisher --> ApplicationEventPublisher
    JobApplicationServiceImpl --> JobApplicationSpecifications
```

## Cross-service interaction inside career

```mermaid
flowchart LR
    AppSvc[JobApplicationService]
    IntSvc[InterviewService]
    OfferSvc[OfferService]
    CoSvc[CompanyService]
    RecSvc[RecruiterService]
    DashSvc[CareerDashboardService]

    IntSvc -->|require owned application| AppRepo[(JobApplicationRepository)]
    OfferSvc -->|require owned application| AppRepo
    AppSvc --> CoRepo[(CompanyRepository)]
    AppSvc --> RecRepo[(RecruiterRepository)]
    DashSvc --> AppRepo
    DashSvc --> CoRepo
    DashSvc --> RecRepo
    DashSvc --> IntRepo[(InterviewRepository)]
    DashSvc --> OfferRepo[(OfferRepository)]
```

Interview and offer services do not call `JobApplicationService` as a remote API; they resolve the parent application through repositories and ownership checks, matching the implemented code.

## Transaction and event timing

```mermaid
flowchart TB
    Start[Service method @Transactional] --> Work[Mutations + repository saves]
    Work --> PublishReg[CareerDomainEventPublisher.register afterCommit]
    PublishReg --> Return[Return DTO to controller]
    Return --> Commit{Transaction commits?}
    Commit -->|yes| After[afterCommit publishEvent]
    Commit -->|rollback| Drop[Event not published]
```

## Knowledge / learning / portfolio pattern

```mermaid
flowchart LR
    Controller --> ServiceImpl
    ServiceImpl --> FeatureValidator
    ServiceImpl --> FeatureMapper
    ServiceImpl --> FeatureRepository
    ServiceImpl --> OwnedLookups
```

These features use the same interaction style without career state machine, audit log, or after-commit event publisher.

## Evidence

- `com.acos.auth.service.AuthServiceImpl`
- `com.acos.career.service.JobApplicationServiceImpl`
- `com.acos.career.service.InterviewServiceImpl`
- `com.acos.career.service.OfferServiceImpl`
- `com.acos.career.service.CareerDashboardServiceImpl`
- Knowledge/learning/portfolio `*ServiceImpl` classes
