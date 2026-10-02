# Event contract guidelines

## Requirements

- AsyncAPI 3.x
- Every message defines payload + headers
- Headers include `eventId`, `correlationId`, `schemaVersion`, `producedAt`, `producer`
- Document producer and consumer intent in channel/operation descriptions
- Include examples
- Document retry and dead-letter recommendations in channel descriptions / common schemas

## Naming

- Channel address: `acos.ai.<domain>.<event>.v1`
- Message name: PascalCase domain event (`KnowledgeIndexed`)
- Event type (CloudEvents-compatible): `com.acos.ai.<domain>.<event>.v1`

## Semantics

- Events are facts that already happened
- Prefer immutable payloads
- Avoid embedding large binary content; reference resources by id

## Out of scope

Kafka topic creation, partition keys, and broker ACLs belong to infrastructure repositories.
