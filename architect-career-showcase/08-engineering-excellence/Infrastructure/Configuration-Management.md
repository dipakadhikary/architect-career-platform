# Configuration Management

## Patterns

| Layer | Pattern |
| --- | --- |
| Business | `application.yml` hierarchy + `@ConfigurationProperties` (`AiPlatformProperties` prefix `ai.platform`) |
| AI | pydantic-settings `Settings` loading optional `.env` |
| Web | Zod parse of `import.meta.env` in `src/app/config/env.ts` |
| Contracts | Generator configs under `generator/*` + Maven properties |

## AI integration config (Business)

- `ai.platform.enabled` default **false** (safe local)  
- `base-url` default `http://localhost:8090`  
- Resilience instance name `ai-platform` (Retry/CB/Bulkhead/TimeLimiter via `AiPlatformInvoker`)  
- Feign circuit breaker auto-config **disabled**; manual Resilience4j wrapping instead

## Feature flags

- Business: AI enabled flag  
- Web: `VITE_AI_PLATFORM_ENABLED` (UI gating)  
- AI: many enterprise toggles (guardrails, semantic cache, LangFuse, OTEL, auth modes)

## Not used

Spring Cloud Config, Consul, Kubernetes ConfigMaps as runtime sources.


## Interview Discussion

### Why this approach?

Typed config objects reduce stringly-typed mistakes.

### Alternative approaches

Only OS env without schema. Faster but opaque.

### Trade-offs

Flag sprawl on AI settings needs documentation discipline.

### Enterprise adoption

Central feature-flag service later; keep boot-time validation.

### Scaling considerations

Dynamic refresh carefully — prefer restart for security flags.

### Principal Architect interview questions

**Q1. Why is Feign CB disabled?**  
Resilience is applied in `AiPlatformInvoker` (Retry→CB→Bulkhead→TimeLimiter).

**Q2. Where is Web env validated?**  
Zod schema in `env.ts` — fails fast on bad config.
