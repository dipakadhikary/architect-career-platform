# OpenAPI Architecture

## API organization

Aggregator: `openapi/ai-platform-v1.yaml` (OpenAPI **3.1.0**, `info.version: 1.0.0`).

Domain modules composed via `$ref`:

| Module | File |
| --- | --- |
| Knowledge | `openapi/knowledge/knowledge-api.yaml` |
| Learning | `openapi/learning/learning-api.yaml` |
| Career | `openapi/career/career-api.yaml` |
| Portfolio | `openapi/portfolio/portfolio-api.yaml` |
| Chat | `openapi/chat/chat-api.yaml` |
| Common | `openapi/common/{common,errors,health,pagination,security}.yaml` |

## Paths (v1 surface)

| Method | Path |
| --- | --- |
| GET | `/api/v1/ai/health` |
| POST | `/api/v1/ai/knowledge/index\|search\|summarize` |
| POST | `/api/v1/ai/learning/quiz/generate`, `/topics/recommend-next`, `/progress/evaluate` |
| POST | `/api/v1/ai/career/resume/generate`, `/interview/analyze`, `/cover-letter/generate` |
| POST | `/api/v1/ai/portfolio/review`, `/skill-gap/analyze` |
| POST | `/api/v1/ai/chat/completions` |

Servers documented: `http://localhost:8090`, `https://ai.acos.local`.

## Components

- **Security schemes:** `bearerJwt`, `serviceApiKey` (from `security.yaml`)
- **Global security:** bearer JWT default on aggregator
- **Schemas:** CorrelationId, ApiResponse, AIError, ProblemDetails, pagination, health, plus domain models in module files
- **Responses:** reusable problem+json responses in `errors.yaml`

## Operation IDs

Stable `operationId` values drive generated method names; duplicates fail `validate-openapi.mjs`.

| operationId | Path |
| --- | --- |
| `getAiPlatformHealth` | GET `/api/v1/ai/health` |
| `indexKnowledge` / `searchKnowledge` / `summarizeKnowledge` | Knowledge POSTs |
| `generateQuiz` / `recommendNextTopic` / `evaluateProgress` | Learning POSTs |
| `generateResume` / `analyzeInterview` / `generateCoverLetter` | Career POSTs |
| `reviewPortfolio` / `analyzeSkillGap` | Portfolio POSTs |
| `createChatCompletion` | POST `/api/v1/ai/chat/completions` |

Chat description notes that **streaming variants** may be added later under a compatible path; streaming is **not** in the current contract surface.

## Examples

Error responses include examples (validation failed, unauthorized, etc.) under `application/problem+json`.

## Validation

Lint/bundle via Node scripts invoked from Maven validate/generate-sources; unresolved `$ref` or invalid schemas fail the build.

## Interview Discussion

### Why Contract First?

Modular YAML keeps domain PRs reviewable while one aggregator defines the public AI surface.

### Alternative approaches

Monolithic single file — harder reviews; protobuf — different ecosystem.

### Trade-offs

`$ref` discipline required; bundling step mandatory before generate.

### Why not shared DTO libraries?

OpenAPI schemas generate into each language idiomatically.

### Why OpenAPI?

Feign/Axios/Pydantic all speak it.

### When would you choose gRPC?

Internal fan-out under heavy load — not current AI JSON APIs.

### Scaling considerations

Split aggregators per major version (`ai-platform-v2.yaml`) when breaking.

### Principal API Architect interview questions

**Q1. Where is health defined?**  
Knowledge module path ref + common HealthResponse schema.

**Q2. Envelope vs Problem Details?**  
`ApiResponse` for wrapped success paths; `ProblemDetails` for HTTP error content types in shared responses.
