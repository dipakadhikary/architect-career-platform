# AsyncAPI Architecture

## Status

**Spec-first and implemented as contracts + generated event schemas.** No Kafka/Pulsar/Rabbit consumers are wired in Business or AI Platform runtimes today. Channel addresses are transport-agnostic by design.

## Aggregator

`asyncapi/ai-platform-events-v1.yaml` — AsyncAPI **3.0.0**, `info.version: 1.0.0`, `defaultContentType: application/json`.

## Channels (examples)

| Channel key | Address |
| --- | --- |
| `aiProcessingStarted` | `acos.ai.processing.started.v1` |
| `aiProcessingCompleted` | `acos.ai.processing.completed.v1` |
| `aiProcessingFailed` | `acos.ai.processing.failed.v1` |
| Knowledge | created/updated/indexed/indexFailed |
| Learning | planCompleted, quizGenerated |
| Career | interviewCompleted/analyzed, resumeGenerated |
| Portfolio | updated/reviewed, skillGapDetected |

Domain details live under `asyncapi/{knowledge,learning,career,portfolio,common}/`.

## Messages & schemas

Common messages in `asyncapi/common/common-events.yaml` include a **CloudEvents-compatible** `CloudEventEnvelope` (naming aligned, transport-agnostic). Domain event payloads use `.v1` channel addresses and `schemaVersion` headers (see versioning docs).

## Aggregate operations vs channels

The aggregator **`$ref`s all domain channels** listed above, but its top-level `operations:` block currently declares only three send operations: `publishAIProcessingStarted`, `publishAIProcessingCompleted`, `publishAIProcessingFailed`. Domain modules define additional send/receive operations locally; broker wiring and full aggregate operation promotion remain future work.

## Generation

Custom Node (`scripts/maven/generate-events.mjs`) — **not** the official AsyncAPI Generator — exports payload/message JSON Schemas plus `catalog.json` under `target/generated/asyncapi`, then copies them into SDK trees as `event_schemas/` during Maven `process-resources`.

## Future event-driven architecture

Outbox + broker; Business listeners replacing in-process Spring events; AI publishing indexed/failed — until then AsyncAPI is the vocabulary, not the runtime bus.

## Interview Discussion

### Why Contract First?

Events need the same SoT discipline as REST or consumers invent incompatible payloads.

### Alternative approaches

Code-first Spring Cloud Stream bindings — language-biased.

### Trade-offs

Specs can outpace runtime (current state) — document honestly.

### Why not shared DTO libraries?

Events must be readable by non-Java services later.

### Why OpenAPI? / AsyncAPI?

OpenAPI for sync; AsyncAPI for async — paired standards.

### When would you choose gRPC?

Bidirectional streaming control planes — different problem than domain events.

### Scaling considerations

Freeze channel addresses; evolve payload minors carefully.

### Principal API Architect interview questions

**Q1. Is Kafka required to use this repo?**  
No — contracts validate without a broker.

**Q2. Who implements knowledgeIndexed today?**  
Business uses in-process indexing; AsyncAPI channel awaits broker adoption.
