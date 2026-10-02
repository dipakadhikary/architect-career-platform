# AI Testing

## What is tested

| Area | Approach |
| --- | --- |
| Guardrails / policy / cache / router / resilience | Unit tests with heuristics |
| Pipeline / enterprise middleware | Unit tests |
| HTTP AI APIs | Integration tests with mocks |
| Contracts compatibility | Import check script in CI |
| Quality format | ruff, black, mypy (soft in CI) |

## What is not a hard gate

- DeepEval disabled via pytest `addopts = "-p no:deepeval"`  
- Golden-output LLM evals not enforced in CI  
- Locust/k6 not in CI  

## Fixtures

`tests/fixtures/providers.py` defines Mock LLM/embedding/vector/reranker helpers (available for tests).


## Interview Discussion

### Why this approach?

Deterministic unit tests beat flaky live-LLM CI for PRs.

### Alternative approaches

Always call real models in CI. Costly/flaky.

### Trade-offs

Behavioral regressions in prompts may slip.

### Enterprise adoption

Separate nightly eval job with LangFuse/DeepEval.

### Scaling considerations

Snapshot embeddings; seed Qdrant fixtures.

### Principal Architect interview questions

**Q1. Coverage fail-under?**  
55% on `app` in CI.

**Q2. Is DeepEval active?**  
Plugin disabled in addopts.
