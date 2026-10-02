# Governance

## API standards

Codified in `docs/rest-guidelines.md` and `docs/event-guidelines.md`:

| Standard | Rule |
| --- | --- |
| Spec versions | OpenAPI 3.1, AsyncAPI 3.0 |
| Paths | `/api/v1/ai/{capability}/...` |
| Errors | RFC 9457 Problem Details (`application/problem+json`) |
| Security | Explicit schemes (`bearerJwt`, `serviceApiKey`) |
| Headers | `X-Correlation-Id`, `X-Request-Id`, optional `X-Schema-Version` |
| Examples | Required on operations |
| Reuse | `$ref` common components — no anonymous schema clones |
| operationId | Stable, unique across aggregate |

## Naming conventions

- Tags by capability: knowledge, learning, career, portfolio, chat, health
- Event channel addresses include `.v1` major segment
- Generator package names: `com.acos.ai.contracts.*`, `acos_ai_contracts`, `@acos/ai-contracts`

## Review process

1. Author edits domain YAML under `openapi/` or `asyncapi/` (prefer modules over aggregators)
2. Update `CHANGELOG.md`; bump `VERSION` per SemVer table
3. Run `mvn clean verify` locally
4. PR review focuses on compatibility, examples, security, and `$ref` reuse
5. CI must pass before merge

## Version control

- Specs and docs are committed
- Generated sources are **not** committed (`target/`)
- `VERSION` + Maven/npm versions stay aligned at **1.0.0** currently

## Ownership

| Area | Owner archetype |
| --- | --- |
| `openapi/common/*`, security, errors | Platform / API architects |
| Domain `*-api.yaml` / `*-events.yaml` | Capability owners + API review |
| Generators / CI / packaging | Platform engineers |
| Consumer sync (AI vendoring, Feign) | Consuming repo owners |

## Change management

- Additive = MINOR; docs = PATCH; breaking = MAJOR + `docs/migration-guide.md`
- Deprecate in-spec before removal
- Coordinate Business/AI releases on MAJOR bumps

## Interview Discussion

### Why Contract First?

Governance attaches to YAML PRs instead of three language PR reviews for the same change.

### Alternative approaches

Architecture review boards without mechanical CI — weak enforcement.

### Trade-offs

Slower breaking changes; higher bar for common schema edits.

### Why not shared DTO libraries?

Governance would fragment across Java/Python/TS PRs.

### Why OpenAPI?

Industry-standard review artifact for REST.

### When would you choose gRPC?

Same governance model over `.proto` + buf breaking checks.

### Scaling considerations

API design checklist in PR template; automated spectral + diff bots.

### Principal API Architect interview questions

**Q1. Who edits `common/errors.yaml`?**  
Platform owners with elevated review — not casual domain PRs.

**Q2. Are generated clients reviewed line-by-line?**  
No — review the contract; verify CI generation.
