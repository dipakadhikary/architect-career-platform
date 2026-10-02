# ACOS AI Contracts

Contract-first source of truth for **all ACOS AI Platform integration**.

This repository owns:

- REST contracts (OpenAPI 3.1)
- Event contracts (AsyncAPI 3.x)
- Shared schemas
- Code generation infrastructure
- Versioning and documentation

It does **not** contain business logic, Spring Boot implementations, Python service implementations, or React UI code.

## Consumers

- Spring Boot Business Platform
- Python AI Platform
- React Frontend (via Business Platform / future direct clients)
- Integration tests
- Future mobile apps, CLI tools, and partner integrations

## Quick start

Production generation is Maven-driven (Java 21). One command validates contracts, generates SDKs, compiles Java, validates Python/TypeScript, and packages publishable artifacts:

```bash
mvn clean verify
```

### Maven profiles

| Profile | Effect |
| ------- | ------ |
| `all` (default properties) | Java + Python + TypeScript |
| `java` | Java SDK only (`-Pjava`) |
| `python` | Python SDK only (`-Ppython`) |
| `typescript` | TypeScript SDK only (`-Ptypescript`) |

```bash
mvn clean verify -Pjava
mvn clean verify -Ppython
mvn clean verify -Ptypescript
mvn clean verify -Pall
```

### Build lifecycle

```text
validate          → Node toolchain, OpenAPI + AsyncAPI validation
generate-sources  → Bundle OpenAPI, generate Java/Python/TypeScript + event schemas
process-sources   → Register generated Java sources
process-resources → Copy event schemas into SDK trees
compile           → Compile generated Java (Feign clients, DTOs, enums)
prepare-package   → Validate Python models, build TypeScript
package           → Build career-ai-java-sdk.jar
verify            → Stage JAR/ZIPs under target/artifacts
```

### Generated artifact locations

| Output | Path |
| ------ | ---- |
| Java sources | `target/generated/java` |
| Python sources | `target/generated/python` |
| TypeScript sources | `target/generated/typescript` |
| Event schemas | `target/generated/asyncapi` |
| Java SDK JAR | `target/artifacts/career-ai-java-sdk.jar` |
| Python SDK ZIP | `target/artifacts/career-ai-python-sdk.zip` |
| TypeScript SDK ZIP | `target/artifacts/career-ai-typescript-sdk.zip` |

Generated code is never committed; it lives under `target/` only.

### Helper scripts

| Script | Action |
| ------ | ------ |
| `scripts/generate-all(.cmd\|.sh)` | `mvn clean verify -Pall` |
| `scripts/generate-java(.cmd\|.sh)` | `mvn clean verify -Pjava` |
| `scripts/generate-python(.cmd\|.sh)` | `mvn clean verify -Ppython` |
| `scripts/generate-typescript(.cmd\|.sh)` | `mvn clean verify -Ptypescript` |
| `scripts/validate-contracts(.cmd\|.sh)` | `mvn validate` |
| `scripts/clean-generated(.cmd\|.sh)` | `mvn clean` |

### Local development workflow

1. Edit contracts under `openapi/` or `asyncapi/` only.
2. Run `mvn clean verify` (or a language profile).
3. Consume artifacts from `target/artifacts/` or sources under `target/generated/`.
4. OpenAPI Generator skips regeneration when the bundled spec is unchanged (`skipIfSpecIsUnchanged`).

Legacy npm scripts (`npm run generate`, etc.) remain for ad-hoc use; CI and production builds use Maven.

### CI/CD workflow

GitHub Actions (`.github/workflows/validate-and-generate.yml`):

1. Checkout  
2. Setup Java 21 + Maven cache  
3. Setup Python (model validation)  
4. `mvn -B clean verify` (validate → generate → compile → package)  
5. Upload `target/artifacts/*`  

Publishing to GitHub Packages is stubbed in the workflow and `distributionManagement` in `pom.xml` for a later one-line enable.

### Troubleshooting

| Symptom | What to check |
| ------- | ------------- |
| OpenAPI validation fails | Unresolved `$ref`, invalid schema, or duplicate `operationId` — see Maven log `[openapi]` lines |
| AsyncAPI validation fails | Invalid AsyncAPI 3.x structure under `asyncapi/` |
| Java compile fails | Generator/Feign compatibility; ensure Java 21 |
| Python validation fails | Syntax errors in `target/generated/python` |
| TypeScript build fails | `npm run build` / `tsc` under `target/generated/typescript` |
| Missing artifacts | Ensure profile includes that language; inspect `target/artifacts` after `verify` |

Also see [docs/troubleshooting.md](docs/troubleshooting.md).

## Repository layout

```text
openapi/                 # REST contracts (modular + aggregated v1)
asyncapi/                # Event contracts (modular + aggregated v1)
generator/               # Language generator configuration (reference)
scripts/                 # Helper scripts + Maven Node validators
scripts/maven/           # Validation, bundle, package helpers for Maven
pom.xml                  # Parent Maven build (Java 21)
target/generated/        # Generated SDKs (local/CI, gitignored)
target/artifacts/        # Publishable JAR/ZIPs (gitignored)
docs/                    # Governance and operator guides
```

## REST surface (v1)

Aligned with existing Business Platform Feign paths under `/api/v1/ai/...`:

| Domain    | Examples                                                                  |
| --------- | ------------------------------------------------------------------------- |
| Knowledge | `/knowledge/index`, `/search`, `/summarize`                               |
| Learning  | `/learning/quiz/generate`, `/topics/recommend-next`, `/progress/evaluate` |
| Career    | `/career/resume/generate`, `/interview/analyze`, `/cover-letter/generate` |
| Portfolio | `/portfolio/review`, `/skill-gap/analyze`                                 |
| Chat      | `/chat/completions`                                                       |
| Health    | `/health`                                                                 |

## Event surface (v1)

Transport-agnostic AsyncAPI channels, including:

- `KnowledgeCreated`, `KnowledgeUpdated`, `KnowledgeIndexed`, `KnowledgeIndexFailed`
- `LearningPlanCompleted`, `QuizGenerated`
- `InterviewCompleted`, `InterviewAnalyzed`, `ResumeGenerated`
- `PortfolioUpdated`, `PortfolioReviewed`, `SkillGapDetected`
- `AIProcessingStarted`, `AIProcessingCompleted`, `AIProcessingFailed`

## Documentation

- [Architecture overview](docs/architecture-overview.md)
- [Contract-first development](docs/contract-first.md)
- [REST guidelines](docs/rest-guidelines.md)
- [Event guidelines](docs/event-guidelines.md)
- [Versioning strategy](docs/versioning.md)
- [Consumer guide](docs/consumer-guide.md)
- [Producer guide](docs/producer-guide.md)
- [Generate clients](docs/generate-clients.md)
- [Generate event models](docs/generate-event-models.md)
- [Add REST APIs](docs/add-rest-api.md)
- [Add events](docs/add-events.md)
- [Folder structure](docs/folder-structure.md)
- [Migration guide](docs/migration-guide.md)
- [Troubleshooting](docs/troubleshooting.md)

## Version

Current contract package version: see `VERSION` (`1.0.0`).
