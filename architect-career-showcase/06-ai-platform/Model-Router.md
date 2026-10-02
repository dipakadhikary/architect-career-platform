# Model Router

## Implementation

`ConfigurablePolicyModelRouter` (`app.infrastructure.enterprise.router`) implements `ModelRouterPort`.

Default policy setting: `routing_policy=lowest_cost`.  
Fallback setting: `routing_fallback_provider=extractive`.

## Routing strategies (`RoutingPolicy`)

| Policy | Selection heuristic |
| --- | --- |
| `lowest_cost` (default) | Min `estimated_cost_per_1k` among candidates |
| `preferred_provider` | Match `routing_preferred_provider` |
| `capability_specific` | Map from `routing_capability_providers` |
| `context_length` | Prefer models with `max_context >= estimate` |
| `lowest_latency` | Prefer ollama then cost |
| `highest_quality` | Prefer openai → azure → ollama → extractive |
| `availability` | First candidate |
| `fallback` | Last candidate |

Candidates are derived from enabled provider settings; if none, returns configured fallback (`extractive`).

## Cost / latency optimization

- Cost estimates use settings like `cost_openai_per_1k_tokens`, Azure, Ollama (0).
- Latency policy biases local Ollama.
- Pipeline cost tracker records estimated USD after execution.

## Fallback

Router + pipeline resilience cooperate: provider selection first; execution retries/timeouts/circuit; soft fallback where allowed.

## Configuration

All via `AppSettings` / env — no code change required to switch policy.

## Interview Discussion

### Why this architecture?

Central routing prevents every workflow from hard-coding models and enables cost governance.

### Alternative approaches

Single global model; per-request model field only. Policy router supports enterprise FinOps.

### Trade-offs

Heuristics are static estimates — not live latency probes (except preference rules).

### Scaling considerations

Feed real latency/cost telemetry into routing scores; pin critical capabilities to preferred providers.

### How would this evolve?

Bandit routing; shadow traffic; per-tenant budgets enforcing provider choice.

### Principal AI Architect interview questions

**Q1. Default routing?**  
`lowest_cost`.

**Q2. What is extractive fallback?**  
Degraded non-LLM (or minimal) response path when generative providers unavailable — used as router fallback target.
