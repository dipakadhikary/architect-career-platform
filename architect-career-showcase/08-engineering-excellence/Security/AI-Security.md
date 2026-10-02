# AI Security

## Controls implemented on AI Platform

| Control | Implementation |
| --- | --- |
| Guardrails | `HeuristicGuardrails` — empty/length, injection/jailbreak regex, moderation regex, optional PII redaction |
| Prompt sanitizer | `DefaultPromptSanitizer` |
| Data masker | `RegexDataMasker` |
| Rate limiting | In-memory middleware; default 120 req/min; exempts health/metrics/docs |
| Enterprise pipeline | Policy, cost, audit hooks around executions |
| Auth modes | JWT / API key / internal token (optional) |
| Browser isolation | Web never holds provider keys |

## Business anti-corruption

Feign calls only when enabled; failures degrade via invoker; correlation/API-key headers forwarded.

## Not production-grade yet

- Heuristic regex ≠ model-based moderation service  
- In-memory rate limit not distributed  
- MCP/A2A are stubs (no external tool exfiltration channel yet, but also no real tool governance)  


## Interview Discussion

### Why this approach?

Layer deterministic guardrails before/around model calls.

### Alternative approaches

Rely on provider moderation only. Less portable across Ollama/Azure.

### Trade-offs

Regex bypass risk; false positives.

### Enterprise adoption

Managed moderation API + allowlisted tools + human review queues.

### Scaling considerations

Distributed rate limits (Redis); per-tenant quotas.

### Principal Architect interview questions

**Q1. Default rate limit?**  
120 requests/minute (in-process).

**Q2. Are provider keys in the SPA?**  
No.
