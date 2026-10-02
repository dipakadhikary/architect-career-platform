# Security

## Authentication options (`AuthenticationService`)

| Mechanism | Settings | Default |
| --- | --- | --- |
| JWT validation | `auth_jwt_enabled`, secret/alg/aud/iss | **false** |
| API keys | `auth_api_key_enabled`, `auth_api_keys` | **false** |
| Internal service header | `auth_internal_service_header`, token set | optional |

Local/dev can run open unless enabled — **production must turn auth on**.

## Pipeline security controls

- Guardrails (injection, moderation, PII)
- Prompt sanitizer / data masker ports
- Policy engine: max cost, max tokens, optional latency, allow/deny providers/models/capabilities/tools/prompts
- Tenant isolation flag (`tenant_isolation_enabled`, default false) for future enforcement
- Audit log port records pipeline events
- Rate limiting middleware (`rate_limit_requests_per_minute` default 120) with exempt paths for health/docs/metrics

## Network posture

- Intended caller: Business Platform (private network / mesh)
- CORS configurable (`cors_allow_origins` default `*`) — tighten in production
- No browser-direct design

## Secrets

API keys for OpenAI/Azure/Cohere/LangFuse via env/`SecretStr`; never log secrets.

## MCP / A2A

In-memory stubs only — no remote tool execution surface yet.

## Interview Discussion

### Why this architecture?

Layered controls (edge auth + pipeline guardrails + policy + audit) match enterprise AI threat models better than prompt-only safety.

### Alternative approaches

Mutual TLS only; API gateway policy only. Defense in depth preferred.

### Trade-offs

Auth disabled by default for DX — dangerous if copied to prod unchanged.

### Scaling considerations

Centralize API keys in a secret manager; per-tenant keys; abuse detection on rate limits.

### How would this evolve?

Mandatory auth in non-dev envs; mTLS to Business; signed capability tokens.

### Principal AI Architect interview questions

**Q1. Who authenticates end users?**  
Business Platform JWT sessions. AI Platform authenticates **service callers** when enabled.

**Q2. What blocks prompt injection?**  
Heuristic guardrails in the pipeline (plus future stronger classifiers).
