# How SDK generation works

## Tooling

| Concern              | Tool                                                                     |
| -------------------- | ------------------------------------------------------------------------ |
| Spec download        | `scripts/download-openapi.mjs`                                           |
| YAML/JSON normalize  | `scripts/normalize-openapi.mjs` + `js-yaml`                              |
| Version sync         | `scripts/sync-version.mjs`                                               |
| Code generation      | OpenAPI Generator CLI `7.12.0` via `@openapitools/openapi-generator-cli` |
| TypeScript generator | `typescript-axios`                                                       |
| Java generator       | `java` library `restclient` (default) or `feign`                         |

## Pipeline

```text
download → normalize → version:sync → generate:typescript → generate:java → build/test
```

### 1. Download

Prefers `GET {base}/v3/api-docs` (JSON). Falls back to YAML endpoints and `/api-docs`.

### 2. Normalize

Writes both:

- `openapi/acos-api.yaml` (canonical human-readable snapshot)
- `openapi/acos-api.json` (machine-friendly twin)

OpenAPI metadata from the backend is preserved as-is.

### 3. Version sync

If `info.version` is SemVer, packaging versions are updated. Otherwise `VERSION` remains authoritative.

### 4. Generate TypeScript

Output: `typescript/generated`

Includes:

- models / DTOs / enums
- Axios API services
- `Configuration` with bearer auth support
- base client utilities

### 5. Generate Java

Output: `java/generated` (RestClient) or `java/generated-feign` (Feign)

Post-processing (`scripts/postprocess-java.mjs`):

- sets compiler source/target to Java 21
- removes unused Gradle/Travis scaffolding
- writes `GENERATED.md`

## Custom wrappers

Wrappers compose generated APIs and add:

- retry with exponential backoff
- structured logging without secrets
- friendlier domain method names (backend operationIds are often auto-derived)

TypeScript examples: `KnowledgeApiService`, `LearningApiService`, `CareerApiService`, `PortfolioApiService`.

Java examples: same domain services under `com.acos.sdk`.
