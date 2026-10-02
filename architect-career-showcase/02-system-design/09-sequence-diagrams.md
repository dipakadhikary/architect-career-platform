# Sequence Diagrams

## Login

```mermaid
sequenceDiagram
    actor Client
    participant AuthController
    participant AuthService
    participant PasswordEncoder
    participant UserRepository
    participant TokenService
    participant DB as PostgreSQL

    Client->>AuthController: POST /api/v1/auth/login
    AuthController->>AuthService: login(LoginRequest)
    AuthService->>UserRepository: findByEmail
    UserRepository->>DB: SELECT user
    AuthService->>PasswordEncoder: matches(raw, hash or dummyHash)
    alt invalid credentials / disabled
        AuthService-->>AuthController: BusinessException
        AuthController-->>Client: ApiResponse failure
    else success
        AuthService->>TokenService: issueTokens(user)
        TokenService->>DB: INSERT refresh_token hash
        TokenService-->>AuthService: access + refresh tokens
        AuthService-->>AuthController: LoginResponse
        AuthController-->>Client: 200 ApiResponse.success
    end
```

## Create job application

```mermaid
sequenceDiagram
    actor Client
    participant Filter as JwtAuthenticationFilter
    participant Controller as JobApplicationController
    participant Service as JobApplicationServiceImpl
    participant Validator as CareerValidator
    participant CompanyRepo as CompanyRepository
    participant AppRepo as JobApplicationRepository
    participant HistoryRepo as ApplicationStatusHistoryRepository
    participant Audit as CareerAuditService
    participant Mapper as CareerMapper
    participant DB as PostgreSQL

    Client->>Filter: POST /api/v1/career/applications + Bearer JWT
    Filter->>Controller: Authenticated principal
    Controller->>Service: create(ownerId, request)
    Service->>Validator: validate notes / limits
    Service->>CompanyRepo: findByIdAndOwnerId(companyId, ownerId)
    CompanyRepo->>DB: SELECT company
    Service->>AppRepo: save(JobApplication DRAFT)
    AppRepo->>DB: INSERT application
    Service->>HistoryRepo: save(history old=null new=DRAFT)
    HistoryRepo->>DB: INSERT status_history
    Service->>Audit: record(APPLICATION_CREATED)
    Audit->>DB: INSERT career_audit_logs
    Service->>Mapper: toJobApplicationResponse
    Service-->>Controller: JobApplicationResponse
    Controller-->>Client: 201 ApiResponse.success
```

## Status transition

```mermaid
sequenceDiagram
    actor Client
    participant Controller as JobApplicationController
    participant Service as JobApplicationServiceImpl
    participant State as ApplicationStateValidator
    participant AppRepo as JobApplicationRepository
    participant HistoryRepo as ApplicationStatusHistoryRepository
    participant Audit as CareerAuditService
    participant Events as CareerDomainEventPublisher
    participant Tx as Transaction commit
    participant Bus as ApplicationEventPublisher

    Client->>Controller: POST /applications/{id}/status
    Controller->>Service: transitionStatus(ownerId, id, request)
    Service->>AppRepo: find owned non-archived application
    Service->>State: validate(from, to)
    alt invalid transition
        State-->>Service: InvalidApplicationStatusTransitionException
        Service-->>Client: 422 ApiResponse failure
    else allowed
        Service->>AppRepo: update status
        Service->>HistoryRepo: save status history
        Service->>Audit: record(STATUS_CHANGED)
        Service->>Events: publish(ApplicationStatusChangedEvent[+specific])
        Events-->>Events: register afterCommit synchronization
        Service-->>Controller: JobApplicationResponse
        Controller-->>Client: 200 ApiResponse.success
        Controller->>Tx: commit
        Tx->>Events: afterCommit()
        Events->>Bus: publishEvent(domainEvent)
    end
```

## Protected read with ownership miss

```mermaid
sequenceDiagram
    actor Client
    participant Controller
    participant Service
    participant Repository
    participant Handler as GlobalExceptionHandler

    Client->>Controller: GET resource by id + JWT
    Controller->>Service: get(ownerId, resourceId)
    Service->>Repository: findByIdAndOwnerId
    Repository-->>Service: empty
    Service-->>Controller: *NotFoundException
    Controller-->>Handler: BusinessException
    Handler-->>Client: 404 ApiResponse.failure(RESOURCE_NOT_FOUND)
```

## Soft-archive application

```mermaid
sequenceDiagram
    actor Client
    participant Controller as JobApplicationController
    participant Service as JobApplicationServiceImpl
    participant AppRepo as JobApplicationRepository
    participant Audit as CareerAuditService

    Client->>Controller: DELETE /api/v1/career/applications/{id}
    Controller->>Service: archive(ownerId, id)
    Service->>AppRepo: load owned application
    Service->>Service: application.archive()
    Service->>AppRepo: save archived=true archivedAt=now
    Service->>Audit: record(APPLICATION_ARCHIVED)
    Service-->>Controller: void
    Controller-->>Client: 200 ApiResponse.success(null)
```

These sequences mirror the implemented auth and career flows. Other features follow the same controller → service → repository → mapper shape without career-specific state/event/audit steps.
