# Evaluation

## Layers of evaluation

| Layer | Class | What it measures |
| --- | --- | --- |
| Knowledge RAG | `HeuristicRagEvaluator` | Lexical overlap, hit coverage, avg retrieval score → composite `score` |
| Agentic | `HeuristicAgenticEvaluator` | Workflow answer quality heuristics |
| Enterprise pipeline | `PersistentEnterpriseEvaluator` | Faithfulness, groundedness, context precision/recall, answer relevance, retriever quality, latency, tokens, cost, prompt version, model, provider |

## Enterprise metrics (implemented heuristics)

Computed from term-set ratios (not LLM-as-judge):

- **Faithfulness / groundedness** — answer terms ⊆ context terms ratio
- **Answer relevance** — query∩answer / query
- **Context precision / recall** — query terms vs context
- **Retriever quality** — mean of precision & recall
- **Latency / tokens / estimated cost** — from pipeline instrumentation
- **Prompt version / model / provider** — recorded on `EnterpriseEvaluationRecord`

Persisted in-process dict by `workflow_id`; optional LangFuse `trace(...)` when adapter present.

## LangFuse integration

- Settings: `langfuse_enabled` default **false**, host/public/secret keys
- `LangfuseAdapter` flushed on shutdown
- Evaluator best-effort traces; failures swallowed to avoid breaking requests

## What is not implemented

- DeepEval / Ragas pipelines (pytest even disables deepeval plugin via addopts)
- Human preference datasets
- Automatic regression gates in CI on quality scores

## Interview Discussion

### Why this architecture?

Always-on heuristic scores give observability without paying LLM judge costs or requiring API keys in CI.

### Alternative approaches

Ragas/DeepEval online; human raters; LLM-as-judge. Add later behind the same evaluation ports.

### Trade-offs

Lexical faithfulness ≠ semantic faithfulness — good for plumbing, weak for nuanced QA quality.

### Scaling considerations

Sample evals; async batch judges; store records in Postgres/LangFuse permanently.

### How would this evolve?

Golden-set CI; prompt A/B scorecards; retrieval-only eval harnesses.

### Principal AI Architect interview questions

**Q1. Is LangFuse required?**  
No — disabled by default.

**Q2. Where do scores attach?**  
Enterprise pipeline post-execution via `EnterpriseEvaluationPort`.
