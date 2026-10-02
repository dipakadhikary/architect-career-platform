# Package Structure

## Standard

ACOS uses **package-by-feature** under `com.acos`, with technical layers inside each feature.

## Root packages

| Package | Role |
| --- | --- |
| `com.acos.auth` | Authentication, JWT, users/roles |
| `com.acos.knowledge` | Knowledge notes |
| `com.acos.learning` | Learning plans / milestones / topics |
| `com.acos.portfolio` | Portfolio projects and related catalog entities |
| `com.acos.career` | Career tracker |
| `com.acos.dashboard` | Dashboard API |
| `com.acos.common` | Shared API, exceptions, logging, persistence base |
| `com.acos.config` | Cross-cutting Spring configuration (OpenAPI, JPA auditing) |
| `com.acos.analytics` | Package reserved; currently `package-info.java` only |

## Feature subpackages

Typical feature layout:

```
com.acos.<feature>
  config
  controller
  dto
  entity
  exception
  mapper
  repository
  service
  validator
```

Career also implements:

- `event`
- `specification`
- `state`

Auth also implements:

- `security`
- `token`
- `validation`

## Shared packages

- `com.acos.common.api` — `ApiResponse`, `ApiError`
- `com.acos.common.exception` — `BusinessException`, `ErrorCode`, shared exception types
- `com.acos.common.handler` — `GlobalExceptionHandler`
- `com.acos.common.logging` — `CorrelationIdFilter`
- `com.acos.common.persistence` — `BaseEntity`

## Rules reflected in code

1. New domain behavior belongs in a feature package, not a global `controllers` / `services` tree.
2. Cross-cutting utilities belong in `common` or `config`.
3. Every package has a `package-info.java` describing its purpose.
4. Feature configuration uses `*Configuration` + `@ConfigurationProperties` under `acos.<feature>`.

## Evidence

- `src/main/java/com/acos/`
- Feature roots such as `src/main/java/com/acos/career/`
- `src/main/java/com/acos/common/`
- `src/main/java/com/acos/config/`
