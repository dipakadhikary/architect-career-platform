# Logging

## Correlation IDs

`CorrelationIdFilter`:

- Runs at highest precedence
- Reads/generates `X-Correlation-Id`
- Stores the value in MDC under key `correlationId`

`ApiResponse` includes the MDC correlation id on every success/failure envelope.

Console logging pattern includes `%X{correlationId:-}` so log lines and API responses can be correlated.

## SLF4J usage

- Obtain loggers with `LoggerFactory.getLogger(Class)`
- Prefer parameterized messages, not string concatenation
- `GlobalExceptionHandler`:
  - business exceptions → `WARN`
  - unhandled exceptions → `ERROR` with throwable
- Auth filter uses `DEBUG` for non-critical skip paths

## Levels

- Default: `root` and `com.acos` at `INFO` (`application.yml`)
- Local profile: `com.acos` at `DEBUG` (`application-local.yml`)

## Rules reflected in code

1. Do not invent a second correlation mechanism; use the existing filter/MDC/header.
2. Keep sensitive data (passwords, raw tokens) out of logs.
3. Prefer warn for expected business failures and error for unexpected failures.

## Evidence

- `src/main/java/com/acos/common/logging/CorrelationIdFilter.java`
- `src/main/java/com/acos/common/api/ApiResponse.java`
- `src/main/java/com/acos/common/handler/GlobalExceptionHandler.java`
- `src/main/resources/application.yml`
- `src/main/resources/application-local.yml`
