# Testing Strategy

## Tooling

- `pytest` + `pytest-asyncio` (auto mode)
- `pytest-cov`, httpx for API tests
- Ruff / Black / Mypy strict in `pyproject.toml`
- Pre-commit hooks
- CI workflow under `.github/workflows/ci.yml`
- ~18 unit test modules under `tests/unit` (enterprise pipeline, guardrails, router, policy, cache, MCP/A2A, agentic capabilities, etc.)

## Unit testing

- Pipeline stages with fakes
- Guardrails pattern cases
- Router policy selection
- Resilience patterns
- Registry/MCP/A2A in-memory behavior

## Integration / API testing

HTTPX against FastAPI app with DI overrides where used; contract models from vendored package.

## Mocking strategy

| Dependency | Approach |
| --- | --- |
| LLM | Fake `LlmPort` / disabled providers / extractive fallback |
| Vector DB | `memory` store default |
| Embeddings | `hashing` provider default |
| Redis | adapter disabled or fake |
| LangFuse | disabled / no-op |
| MCP/A2A | in-memory stubs |

## Workflow testing

Agentic capability/workflow unit tests invoke workflows with stubbed ports rather than live LLMs.

## Load

`scripts/load/locustfile.py` exists for optional load experiments — not a substitute for unit gates.

## Interview Discussion

### Why this architecture?

Defaults (hashing/memory) make the test pyramid runnable without cloud credentials — essential for an AI platform CI.

### Alternative approaches

Only recorded HTTP VCR against OpenAI — brittle and secret-heavy.

### Trade-offs

Heuristic tests do not prove semantic quality; separate eval harness needed for that.

### Scaling considerations

Contract compatibility script (`scripts/check_contract_compatibility.py`); expand integration suite with Testcontainers for Redis/Qdrant.

### How would this evolve?

Golden RAG eval sets in CI; mutation testing on guardrails.

### Principal AI Architect interview questions

**Q1. How do you test without OpenAI?**  
Hashing embeddings + memory store + mocked LLM ports.

**Q2. Are MCP tests hitting real servers?**  
No — in-memory stubs only.
