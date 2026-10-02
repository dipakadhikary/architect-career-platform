# Consumer guide

## Spring Boot Business Platform

1. Generate Java Feign clients from the bundled OpenAPI.
2. Depend on generated models instead of hand-written AI DTOs over time.
3. Subscribe to AsyncAPI events using generated payload schemas.

## Python AI Platform

1. Generate Pydantic models / API stubs from OpenAPI.
2. Implement FastAPI (or equivalent) handlers matching `operationId`s and schemas.
3. Publish domain events conforming to AsyncAPI message payloads.

## React / TypeScript

1. Generate Axios clients for direct or BFF-facing AI contracts as needed.
2. Prefer Business Platform integration endpoints when browser security requires it.
3. Use generated event types for any client-side event tooling.

## Compatibility expectations

Consumers must tolerate additive optional fields and ignore unknown fields where practical.
