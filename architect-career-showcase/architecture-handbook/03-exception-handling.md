# Exception Handling

## Standard

Business failures are modeled as `BusinessException` subtypes carrying an `ErrorCode`. Controllers do not catch domain exceptions; `GlobalExceptionHandler` translates them into `ApiResponse` failures.

## Core types

| Type | Role |
| --- | --- |
| `ErrorCode` | Machine-readable code + HTTP status |
| `BusinessException` | Base domain/business exception |
| `ValidationException` | Extends `BusinessException` with `VALIDATION_FAILED` |
| `ResourceNotFoundException` | Shared not-found helper |
| Feature `*Exception` | Feature-specific subtypes with fixed codes/messages |

## ErrorCode → HTTP mapping (implemented)

Examples from `ErrorCode`:

- `VALIDATION_FAILED` → 400
- `UNAUTHORIZED` / auth failures → 401 (as used by security handlers and auth codes)
- `RESOURCE_NOT_FOUND` → 404
- `BUSINESS_RULE_VIOLATION` → 422
- `INTERNAL_ERROR` → 500

## Feature exception pattern

Feature exceptions typically:

1. Extend `BusinessException`
2. Choose an `ErrorCode`
3. Build a stable message (often including the identifier)

Examples:

- `CompanyNotFoundException`
- `KnowledgeNoteNotFoundException`
- `EmailAlreadyExistsException`
- `InvalidApplicationStatusTransitionException`
- `ApplicationArchivedException`

## Global handler behavior

`GlobalExceptionHandler` (`@RestControllerAdvice`) handles:

- `BusinessException` → `ApiResponse.failure`, status from `ErrorCode`
- `MethodArgumentNotValidException` / `ConstraintViolationException` → 400 + field details
- Malformed JSON / type mismatch → 400
- Missing static resource → 404
- Unhandled `Exception` → 500

Logging:

- Business exceptions: `WARN`
- Unhandled exceptions: `ERROR` with stack trace

Security JSON handlers (`JsonAuthenticationEntryPoint`, `JsonAccessDeniedHandler`) also emit `ApiResponse` failures so auth errors match the same envelope.

## Rules reflected in code

1. Throw feature/business exceptions from services/validators; do not map HTTP status in controllers.
2. Prefer typed feature exceptions over generic messages at call sites.
3. Put field-level details in `ApiError.FieldErrorDetail` when validating.
4. Do not introduce per-feature `@ControllerAdvice` classes; keep translation centralized.

## Evidence

- `src/main/java/com/acos/common/exception/ErrorCode.java`
- `src/main/java/com/acos/common/exception/BusinessException.java`
- `src/main/java/com/acos/common/handler/GlobalExceptionHandler.java`
- Feature packages under `com.acos.*.exception`
