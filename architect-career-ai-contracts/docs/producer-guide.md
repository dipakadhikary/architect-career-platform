# Producer guide

## REST producers (AI Platform)

- Implement every operation in the aggregated OpenAPI v1 surface you claim to support
- Return Problem Details on failures
- Propagate and echo `X-Correlation-Id`
- Honor idempotency guidance for indexing and generation endpoints where applicable

## Event producers

- Populate required headers (`eventId`, `correlationId`, `schemaVersion`, `producedAt`, `producer`)
- Use channel addresses defined in AsyncAPI
- Emit lifecycle events (`AIProcessing*`) around long-running work
- Follow retry/DLQ recommendations when acting as a processing worker

## Do not

- Invent parallel payload shapes
- Publish undocumented event types to production
- Break required fields without a major version bump
