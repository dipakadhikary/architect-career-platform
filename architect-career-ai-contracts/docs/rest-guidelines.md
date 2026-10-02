# REST contract guidelines

## Requirements

- OpenAPI 3.1
- Stable `operationId` values
- Tags per capability domain
- Request and response examples
- RFC 9457 Problem Details for errors (`application/problem+json`)
- Reuse schemas via `$ref`
- Explicit security schemes

## Path conventions

- Version prefix: `/api/v1/ai/...`
- Capability segments: `knowledge`, `learning`, `career`, `portfolio`, `chat`
- Prefer verbs only when the resource noun is unnatural (`/quiz/generate`)

## Headers

- `X-Correlation-Id`
- `X-Request-Id`
- `X-Schema-Version` (optional negotiation)

## Compatibility

- Additive fields are minor changes
- Renames/removals/type changes are major changes
- Deprecate before removal
