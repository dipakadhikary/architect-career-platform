# Exception Handling

## Goals

Translate all failures into the uniform `ApiResponse` envelope with stable `ErrorCode` values and correct HTTP statuses — without leaking stack traces to clients.

## Exception taxonomy

| Type | Role |
| --- | --- |
| `BusinessException` | Base typed business failure carrying `ErrorCode` + optional field details |
| `ValidationException` | Domain validation failure |
| `ResourceNotFoundException` | Missing owned resource |
| Feature exceptions under `*.exception` | Domain-specific subclasses |
| Integration AI exceptions | Mapped to `AI_*` error codes (auth, validation, timeout, rate limit, unavailable) |

## ErrorCode catalog (implemented)

`VALIDATION_FAILED`, `WEAK_PASSWORD`, `UNAUTHORIZED`, `INVALID_CREDENTIALS`, `INVALID_TOKEN`, `ACCOUNT_DISABLED`, `ACCESS_DENIED`, `EMAIL_ALREADY_EXISTS`, `RESOURCE_NOT_FOUND`, `ROLE_NOT_FOUND`, `BUSINESS_RULE_VIOLATION`, `AI_PLATFORM_ERROR`, `AI_PLATFORM_UNAVAILABLE`, `AI_TIMEOUT`, `AI_AUTHENTICATION_FAILED`, `AI_VALIDATION_FAILED`, `AI_RATE_LIMITED`, `INTERNAL_ERROR`.

Each maps to an HTTP status in the enum.

## GlobalExceptionHandler

`@RestControllerAdvice` handlers include:

- `BusinessException` → status from error code
- `MethodArgumentNotValidException` / `ConstraintViolationException` → 400 with field details
- `HttpMessageNotReadableException` → malformed JSON
- `MethodArgumentTypeMismatchException` → bad path/query types
- `NoResourceFoundException` → 404
- Generic `Exception` → 500 `INTERNAL_ERROR` (logged)

## Security exceptions

Unauthenticated/unauthorized HTML defaults are replaced by JSON handlers in the security filter chain so SPA clients receive envelopes consistently.

## Feature exceptions (examples)

- Knowledge: note not found / validation
- Tutorials: `TutorialTopicNotFoundException`, `TutorialQuestionNotFoundException`, `TutorialCircularHierarchyException`, `TutorialDuplicatePathException`
- Career: state-machine / resource not found variants

## Logging policy

Business exceptions logged at WARN with code; unexpected errors at ERROR. Correlation id appears in the logging pattern via MDC.

## Interview Discussion

### Why this architecture?

Stable machine-readable codes let Web map UX and let support correlate failures without parsing free-text messages.

### Alternative approaches

RFC 7807 Problem Details exclusively; gRPC status codes. Envelope + ErrorCode was chosen for SPA uniformity.

### Trade-offs

Must keep `ErrorCode` enum disciplined as the platform grows; avoid one-off string codes in controllers.

### Scaling considerations

Add error catalogs per bounded context only if the shared enum becomes a contention point — prefer shared codes with domain messages first.

### Principal Architect interview questions

**Q1. Should controllers catch exceptions?**  
No — let advice handle unless a rare response-shaping need exists.

**Q2. How are AI errors different?**  
Dedicated codes and gateway statuses (502/503/504/429) so clients can distinguish provider failure from domain validation.
