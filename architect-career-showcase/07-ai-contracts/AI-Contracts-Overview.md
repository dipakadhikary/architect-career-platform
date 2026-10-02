# AI Contracts Overview

## Purpose

`architect-career-ai-contracts` is the **single source of truth** for ACOS AI integration contracts. It defines synchronous AI HTTP APIs and asynchronous event shapes so Java, Python, and (future) TypeScript consumers stay aligned without sharing application code.

It does **not** contain Business/AI/Web business logic.

## Responsibilities

| Responsibility | Evidence |
| --- | --- |
| OpenAPI 3.1 REST contracts | `openapi/ai-platform-v1.yaml` + domain modules |
| AsyncAPI 3.0 event contracts | `asyncapi/ai-platform-events-v1.yaml` + domain modules |
| Shared schemas | `openapi/common/*`, AsyncAPI common events |
| Multi-language generation | OpenAPI Generator 7.12 via Maven |
| Validate → generate → package | `mvn clean verify` lifecycle |
| CI gate | `.github/workflows/validate-and-generate.yml` |
| Docs / guidelines | `docs/*`, `CHANGELOG.md`, `VERSION` |

## Technology Stack

| Area | Choice |
| --- | --- |
| Specs | OpenAPI **3.1.0**, AsyncAPI **3.0.0** |
| Build | Maven (`com.acos.ai:architect-career-ai-contracts:1.0.0`), Java 21 |
| Generator | `openapi-generator-maven-plugin` **7.12.0** |
| Node toolchain | Node 20 / npm 10 (frontend-maven-plugin) for lint/bundle helpers |
| Java SDK deps | OpenFeign 13.5, Jackson, Jakarta Validation |
| Python validation | Pydantic (CI pip install) |
| TypeScript | Generated Axios client package `@acos/ai-contracts` |

## Repository Structure

```text
openapi/                 # REST modules + aggregator
asyncapi/                # Event modules + aggregator
generator/{java,python,typescript}/  # generator configs
scripts/                 # mvn wrappers + node lint/bundle helpers
docs/                    # guidelines and how-tos
generated/               # placeholder README (outputs go to target/)
.github/workflows/       # validate-and-generate
pom.xml, package.json, VERSION, CHANGELOG.md
```

## Interaction with consumers

### Business Platform

Aligns Feign paths/DTOs with `/api/v1/ai/**` contracts. Consumption today is primarily **hand-maintained** Feign clients + DTOs under `com.acos.integration`, not mandatory dependency on the generated JAR.

### AI Platform

Vendors generated Python package under `third_party/acos_ai_contracts` (`acos-ai-contracts @ file:./third_party/...` in `pyproject.toml`) for FastAPI request/response models.

### Web Platform

Does **not** call AI directly and does **not** currently consume the generated TypeScript SDK; uses hand Axios clients against Business BFF paths.

### Generated SDKs

Produced under `target/generated/{java,python,typescript}` and packaged to `target/artifacts/career-ai-{java,python,typescript}-sdk.{jar|zip}`.

## Interview Discussion

### Why Contract First?

Prevents Java/Python/TS drift at the AI boundary — the highest-change, multi-language seam in ACOS.

### Alternative approaches

Shared JAR of DTOs only; code-first Springdoc export; Protobuf/gRPC. Contracts repo keeps language-neutral SoT.

### Trade-offs

Extra repo and build; consumers must sync artifacts. Worth it for multi-runtime AI.

### Why not shared DTO libraries?

A Java-only DTO JAR excludes Python; JSON Schema/OpenAPI is the lingua franca.

### Why OpenAPI?

Ubiquitous tooling, Feign/Axios/Pydantic generators, human-readable review in PRs.

### When would you choose gRPC?

High QPS binary internal meshes with streaming RPCs — not the current browser→Business→AI JSON path.

### Scaling considerations

Contract CI as merge gate; versioned artifacts; consumer compatibility jobs.

### Principal API Architect interview questions

**Q1. What is the API base path?**  
`/api/v1/ai/...` on the AI Platform (port 8090 in servers).

**Q2. Are events live?**  
Specified in AsyncAPI; no Kafka/Pulsar wiring in product repos yet.
