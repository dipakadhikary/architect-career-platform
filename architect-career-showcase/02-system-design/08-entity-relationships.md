# Entity Relationships

All entities extend `BaseEntity` (`id`, `createdAt`, `updatedAt`, `version`) and map into PostgreSQL schema `acos`.

## Auth domain

```mermaid
erDiagram
    USER ||--o{ REFRESH_TOKEN : has
    USER }o--o{ ROLE : user_roles

    USER {
        uuid id PK
        string email UK
        string password_hash
        string first_name
        string last_name
        boolean enabled
        bigint version
    }

    ROLE {
        uuid id PK
        string name UK
        bigint version
    }

    REFRESH_TOKEN {
        uuid id PK
        uuid user_id FK
        string token_hash
        timestamptz expires_at
        timestamptz revoked_at
        bigint version
    }
```

## Knowledge domain

```mermaid
erDiagram
    KNOWLEDGE_NOTE }o--o| CATEGORY : categorized_by
    KNOWLEDGE_NOTE }o--o{ TAG : tagged_with

    KNOWLEDGE_NOTE {
        uuid id PK
        uuid owner_id
        string title
        string summary
        text content
        uuid category_id FK
        bigint version
    }

    CATEGORY {
        uuid id PK
        string name
        bigint version
    }

    TAG {
        uuid id PK
        string name
        bigint version
    }
```

## Learning domain

```mermaid
erDiagram
    LEARNING_PLAN ||--o{ LEARNING_MILESTONE : contains
    LEARNING_MILESTONE ||--o{ LEARNING_TOPIC : contains

    LEARNING_PLAN {
        uuid id PK
        uuid owner_id
        string title
        string status
        bigint version
    }

    LEARNING_MILESTONE {
        uuid id PK
        uuid plan_id FK
        string title
        int sort_order
        bigint version
    }

    LEARNING_TOPIC {
        uuid id PK
        uuid milestone_id FK
        string title
        string status
        bigint version
    }
```

## Portfolio domain

```mermaid
erDiagram
    PORTFOLIO_PROJECT }o--o{ TECHNOLOGY : uses

    PORTFOLIO_PROJECT {
        uuid id PK
        uuid owner_id
        string title
        string status
        bigint version
    }

    TECHNOLOGY {
        uuid id PK
        uuid owner_id
        string name
        bigint version
    }

    SKILL {
        uuid id PK
        uuid owner_id
        string name
        bigint version
    }

    ACHIEVEMENT {
        uuid id PK
        uuid owner_id
        string title
        bigint version
    }

    CERTIFICATION {
        uuid id PK
        uuid owner_id
        string name
        bigint version
    }
```

Portfolio skills, achievements, and certifications are owner-scoped entities; project↔technology is the primary many-to-many association used by project APIs.

## Career domain

```mermaid
erDiagram
    COMPANY ||--o{ RECRUITER : optional_company
    COMPANY ||--o{ JOB_APPLICATION : target
    RECRUITER ||--o{ JOB_APPLICATION : optional_recruiter
    JOB_APPLICATION ||--o{ INTERVIEW : schedules
    JOB_APPLICATION ||--o{ OFFER : receives
    JOB_APPLICATION ||--o{ STATUS_HISTORY : records
    USER ||--o{ CAREER_AUDIT_LOG : actor_owner

    COMPANY {
        uuid id PK
        uuid owner_id
        string name
        bigint version
    }

    RECRUITER {
        uuid id PK
        uuid owner_id
        uuid company_id FK
        string full_name
        string status
        date next_follow_up_date
        bigint version
    }

    JOB_APPLICATION {
        uuid id PK
        uuid owner_id
        uuid company_id FK
        uuid recruiter_id FK
        string title
        string status
        boolean archived
        timestamptz archived_at
        bigint version
    }

    INTERVIEW {
        uuid id PK
        uuid application_id FK
        string interview_round
        string status
        timestamptz interview_date
        bigint version
    }

    OFFER {
        uuid id PK
        uuid application_id FK
        numeric base_salary
        string offer_status
        date offer_expiry_date
        bigint version
    }

    STATUS_HISTORY {
        uuid id PK
        uuid application_id FK
        string old_status
        string new_status
        uuid changed_by
        timestamptz changed_at
        bigint version
    }

    CAREER_AUDIT_LOG {
        uuid id PK
        uuid owner_id
        uuid actor_id
        string action
        string entity_type
        uuid entity_id
        bigint version
    }
```

## Ownership pattern

Most user data entities store `owner_id` (UUID referencing `users.id`) and are queried with owner-scoped repository methods. Relationship enforcement is application-level (service + repository), not database RLS.

## Evidence

- Entity classes under `src/main/java/com/acos/*/entity`
- Flyway migrations `V2`–`V9` under `src/main/resources/db/migration`
- `src/main/java/com/acos/common/persistence/BaseEntity.java`
