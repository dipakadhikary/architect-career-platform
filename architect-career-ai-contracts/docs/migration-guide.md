# Migration guide

## From hand-written Feign DTOs

1. Generate Java models from `dist/openapi/ai-platform-v1.bundled.yaml`.
2. Replace `com.acos.integration.dto.*` usages gradually with generated contracts.
3. Keep Feign path mappings identical to `/api/v1/ai/...`.
4. Remove duplicate DTOs only after behavioral parity is proven in tests.

## From no events to AsyncAPI

1. Adopt header envelope from `asyncapi/common/common-events.yaml`.
2. Start with high-value channels (`KnowledgeCreated`, `KnowledgeIndexed`, `AIProcessingFailed`).
3. Keep payloads additive and version channel addresses with `.v1`.

## To v2

Introduce `/api/v2` OpenAPI modules and `*.v2` event addresses. Do not mutate v1 required fields in place.
