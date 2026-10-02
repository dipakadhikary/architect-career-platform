# Incident: OpenAI / LLM Provider Outage

## Purpose

Keep ACOS usable when cloud LLM providers fail or rate-limit.

## Scope

Applies when `OPENAI_ENABLED` / Azure OpenAI paths are used. Ollama local may still work if configured.

## Audience

- SRE
- DevOps Engineer
- Platform Engineer
- Developer
- Architect

## Preconditions

- Access to process logs and health endpoints
- Ability to change env flags and restart services
- Docker CLI if dependencies are containerized

## Symptoms

- AI 5xx/timeouts on generative routes
- Provider 429/5xx in logs
- Business Feign timeouts; CB may open

## Detection

- AI logs showing provider HTTP errors
- Metrics latency spikes
- Vendor status pages (external)

## Diagnosis

1. Confirm failure is provider — not Redis/Qdrant/local code
2. Check which provider flags are enabled (OpenAI/Azure/Ollama)
3. Observe whether offline extractive fallbacks engage (e.g., summarizer fallback when LLM disabled)
4. Check rate limit middleware (local 429) vs provider 429

## Step-by-Step Procedure (Recovery)

1. **Stabilize Business:** `AI_PLATFORM_ENABLED=false` if user-facing errors cascade
2. Switch to Ollama/local model if available, or set provider `*_ENABLED=false` to force offline fallbacks
3. Reduce load; respect rate limits
4. When provider recovers: re-enable, watch CB closed, re-enable Business AI flag
5. **Future enhancement:** multi-provider automatic failover policies beyond current router

## Validation

- Generative call succeeds or documented fallback returns
- CB closed
- No auth/key errors after rotation if keys were involved

## Rollback

If recovery introduces worse failure, revert to last known-good env/git SHA and keep Business in CRUD-only mode (`AI_PLATFORM_ENABLED=false`) when AI is involved.

## Prevention

- Prefer feature flag kill switch drills
- Cache hits reduce provider dependency
- Don't block Business readiness solely on LLM vendor (design readiness carefully)

## Troubleshooting

| Issue | Fix |
| --- | --- |
| Invalid API key | Secret rotation |
| Only chat fails | Narrow route; keep RAG if separate |
| Extractive fallback quality low | Communicate degraded mode |

## Escalation

Escalate to Architect for SLA/customer comms if demos/production depend on generative AI.

## References

- [../../AI/LLM-Abstraction.md](../../../10-engineering-knowledge/AI/LLM-Abstraction.md) (knowledge base)
- Chapter 06 Model Router
- AI Settings provider flags
