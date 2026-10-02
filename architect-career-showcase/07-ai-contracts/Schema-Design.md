# Schema Design

## Shared schemas

Authored in `openapi/common/common.yaml` and re-exported by the aggregator:

| Schema | Role |
| --- | --- |
| `CorrelationId` / `RequestId` / `UserId` | UUID identity types |
| `SchemaVersion` | SemVer pattern string |
| `Metadata` | correlationId, requestId, schemaVersion, tags |
| `ApiResponse` | success envelope (`success`, `data`, `error`, `timestamp`, `metadata`) |
| `AIError` / `AIErrorCode` | AI-specific error object |
| `ProblemDetails` | RFC7807-style problem document |
| `ValidationError` | Field-level validation entry |

## Error models

`openapi/common/errors.yaml` defines reusable responses (`BadRequest`, `Unauthorized`, …) with `application/problem+json` and examples including `code` and nested `errors`.

## Pagination

`openapi/common/pagination.yaml` defines `PageRequest`, `PageResponse`, and `Pagination`, and the aggregator re-exports them. **No current REST operation `$ref`s these schemas** — they are reserved shared components for future list-style AI APIs, not active request/response shapes today.

## Metadata

Optional `Metadata` on envelopes for observability extension without breaking payloads.

## Problem Details

`ProblemDetails` used for HTTP error content types; complements `ApiResponse` success wrapping.

## References & reuse

Domain APIs `$ref` common schemas; aggregator re-exports for generator visibility. Prefer `$ref` over copy-paste.

## Interview Discussion

### Why Contract First?

Shared schemas force consistent correlation and error semantics across languages.

### Alternative approaches

Per-domain reinvented error JSON — rejected.

### Trade-offs

Envelope + Problem Details duality needs clear operation docs.

### Why not shared DTO libraries?

Schema YAML generates idiomatic models per language.

### Why OpenAPI?

`$ref` and components are first-class.

### When would you choose gRPC?

Protobuf well-known types for binary APIs.

### Scaling considerations

Keep common schemas strict (`additionalProperties: false` where used).

### Principal API Architect interview questions

**Q1. Is Business ApiResponse identical?**  
Conceptually aligned; AI contracts define AI Platform envelopes — Business has its own Java `ApiResponse` for product APIs.

**Q2. Where do field errors live?**  
`ValidationError` / ProblemDetails `errors` arrays in examples and schemas.
