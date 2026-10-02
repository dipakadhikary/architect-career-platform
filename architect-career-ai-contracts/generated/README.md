# Generated artifacts

This directory is produced by `npm run generate`.

| Path | Contents |
| --- | --- |
| `java/` | OpenFeign clients + Jackson models (Java 21) |
| `python/` | Pydantic models / Python client |
| `typescript/` | Axios client + TypeScript models |
| `asyncapi/` | Event payload JSON Schemas + message catalog |

Do not edit generated sources. Regenerate after contract changes.

Note: OpenAPI Generator's Java Feign generator emits Jackson-annotated classes compatible with Java 21. Native `record` emission is limited in the upstream generator; consuming services may wrap generated DTOs in records if desired.
