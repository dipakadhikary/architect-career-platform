# Future Enhancements

Items below are **not** claimed as implemented. They extend the current contracts repository and its consumers.

## Artifact publishing

- Enable GitHub Packages (`packages: write`, uncomment `mvn deploy`)
- Publish Java SDK to Maven-compatible registry
- Publish `@acos/ai-contracts` to npm (GitHub Packages or private registry)
- Publish `acos-ai-contracts` to internal PyPI instead of file vendoring

## Consumer adoption

- Business Platform: declare Maven dependency on `career-ai-java-sdk` and retire hand-duplicated AI DTOs where safe
- Add Business Feign coverage for Chat (and align health client usage with OpenAPI if desired)
- Web Platform: adopt generated TS types only if a typed client is appropriate (likely against Business BFF OpenAPI, or shared models)—**do not** call AI Platform from the browser
- Automate AI Platform `sync_contracts.py` in CI when contracts version bumps
- Wire pagination schemas into list-style AI operations when those endpoints exist
- Promote remaining domain AsyncAPI operations onto the aggregate (beyond the three `publishAIProcessing*` ops)
- Optional chat streaming path (called out in chat-api description; not specified yet)

## Event-driven architecture

- Implement broker-backed channels matching AsyncAPI (`ai-platform-events-v1`)
- Generate and consume event payload schemas in Business and AI runtimes
- Add publishers/subscribers, idempotency, and DLQ policies

## Validation & compatibility automation

- Spectral (or equivalent) style lint in Maven validate
- Breaking-change detection (`openapi-diff` / similar) between base and PR
- Buf-like policy for AsyncAPI evolution
- Cross-repo contract tests (Business Feign stub vs OpenAPI; AI routes vs OpenAPI)

## Generator improvements

- Evaluate better Java record support / custom templates
- Optional FastAPI server stub generation (explicitly deferred in generator notes)
- Stronger TypeScript patch pipeline documentation

## Versioning & release engineering

- Automated SemVer bumps from conventional commits
- Signed releases and SBOM for SDK artifacts
- Migration tooling for `/api/v2` dual-run periods

## Governance

- PR template checklist enforcing rest/event guidelines
- CODEOWNERS for `openapi/common/**`
- Architecture Decision updates when publish/broker decisions land

## Interview Discussion

### Why Contract First?

Future brokers and SDKs still hang off the same YAML SoT—investment compounds.

### Alternative approaches

Skip contracts and integrate ad hoc — multiplies future rewrite cost.

### Trade-offs

Publishing and brokers add platform ops load; correct next steps after SemVer discipline.

### Why not shared DTO libraries?

Future language consumers still need generated packs from OpenAPI/AsyncAPI.

### Why OpenAPI?

Keeps REST evolution independent of when events go live.

### When would you choose gRPC?

If latency/streaming requirements force binary APIs—contracts repo would grow a `proto/` tree.

### Scaling considerations

Treat contracts as a versioned platform product with SLAs for consumers.

### Principal API Architect interview questions

**Q1. Is Kafka implemented because AsyncAPI exists?**  
No — AsyncAPI is spec-first readiness only.

**Q2. Highest-value next step?**  
Enable artifact publish + Business Java SDK dependency, then AI PyPI package—reduce manual sync.
