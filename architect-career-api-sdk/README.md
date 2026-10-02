# Architect Career API SDK

Production SDK repository for the **Architect Career Operating System (ACOS)** platform.

This repository is the **single source of truth** for API contracts consumed by:

- React Frontend
- Python AI Platform (future)
- Integration Tests
- CLI Tools (future)
- Mobile Applications (future)

All models, DTOs, enums, and API clients are **generated from OpenAPI**. Do not hand-write DTOs or clients.

## Repository layout

```text
architect-career-api-sdk/
├── openapi/
│   ├── acos-api.yaml          # Canonical OpenAPI snapshot
│   └── acos-api.json          # Normalized JSON twin
├── typescript/
│   ├── generated/             # OpenAPI Generator output (do not edit)
│   ├── custom/                # Wrappers: retry, logging, domain services
│   └── package.json           # @acos/api-sdk
├── java/
│   ├── generated/             # OpenAPI Generator RestClient output (do not edit)
│   ├── custom/                # Wrappers: Knowledge/Learning/Career/Portfolio
│   └── pom.xml                # Maven reactor
├── scripts/                   # Download / normalize / generate / version sync
├── docs/                      # Architecture and operator guides
└── .github/workflows/         # CI generation + validation
```

## Prerequisites

- Node.js 20+
- Java 21
- Maven 3.9+
- Running ACOS Java Business Platform (only required when refreshing OpenAPI)

## Quick start

```bash
npm install
npm run openapi:normalize   # ensure YAML/JSON are in sync
npm run generate            # TypeScript + Java SDKs
npm run build:typescript
npm run test:typescript
mvn -f java/pom.xml verify
```

Refresh from a live backend:

```bash
# Default: http://127.0.0.1:8080/v3/api-docs
npm run generate:all

# Or point at an explicit URL
ACOS_OPENAPI_URL=https://api.example.com/v3/api-docs npm run generate:all
```

## Versioning

SDK packaging version lives in `VERSION` and is synchronized to:

- root `package.json`
- `typescript/package.json`
- `openapitools.json` generator properties
- `java/pom.xml` reactor version

When OpenAPI `info.version` is a valid SemVer (for example `1.0.0`), `npm run version:sync` aligns the SDK to that value.

When the backend reports a non-SemVer label such as `development` (common before Spring Boot build-info is published), the repository keeps `VERSION` aligned with the Java Business Platform Maven version (`0.0.1`).

## TypeScript consumption

```ts
import { AcosApiClient } from '@acos/api-sdk';

const client = new AcosApiClient({
  basePath: 'http://localhost:8080',
  accessToken: () => localStorage.getItem('accessToken') ?? '',
});

const notes = await client.knowledge.listNotes({ page: 0, size: 20 });
```

Custom wrappers live under `typescript/custom`. Generated Axios clients live under `typescript/generated`.

## Java consumption

Default client library: **Spring RestClient**.

```java
AcosApiClient client = new AcosApiClient("http://localhost:8080")
    .withBearerToken(accessToken);

var notes = client.knowledge().listNotes(0, 20, List.of("updatedAt,desc"));
```

Optional Feign generation:

```bash
ACOS_JAVA_CLIENT_LIBRARY=feign npm run generate:java
```

## Documentation

- [Architecture overview](docs/architecture-overview.md)
- [How SDK generation works](docs/sdk-generation.md)
- [How to update OpenAPI](docs/update-openapi.md)
- [How to consume the SDK](docs/consume-sdk.md)
- [Folder structure](docs/folder-structure.md)
- [Versioning strategy](docs/versioning.md)
- [Troubleshooting](docs/troubleshooting.md)

## CI/CD

GitHub Actions workflow [`.github/workflows/generate-and-validate.yml`](.github/workflows/generate-and-validate.yml):

1. Install tooling
2. Optionally download OpenAPI
3. Generate SDKs
4. Format check
5. TypeScript build + tests
6. Java build + tests
7. Publish package (placeholder for future registries)

## Quality rules

- Never edit files under `typescript/generated` or `java/generated/src`
- Extend behavior only in `typescript/custom` or `java/custom`
- Prefer regenerating after backend contract changes instead of patching clients
- Keep OpenAPI snapshots committed so CI and consumers do not require a live backend for builds
