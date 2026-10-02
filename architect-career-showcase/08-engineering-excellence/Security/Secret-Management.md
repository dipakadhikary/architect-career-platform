# Secret Management

## Pattern: environment variables with local defaults

| Secret | Source |
| --- | --- |
| JWT signing | `ACOS_JWT_SECRET` / `AUTH_JWT_SECRET` |
| DB password | `ACOS_DB_PASSWORD` |
| AI API key (Business→AI) | `AI_PLATFORM_API_KEY` |
| LLM providers | `OPENAI_API_KEY`, `AZURE_OPENAI_API_KEY`, etc. |
| Qdrant | `QDRANT_API_KEY` |
| LangFuse | `LANGFUSE_SECRET_KEY` |

AI uses pydantic `SecretStr` for sensitive fields. `.env` gitignored on AI; Business documents DB vars in `.env.example`.

## Not implemented

- HashiCorp Vault / AWS Secrets Manager / sealed secrets  
- Automatic rotation  
- AI `.env.example` file (referenced, missing)  

## Production guard (AI)

Rejects JWT secret `change-me`; forbids `APP_DEBUG`; requires auth configuration when `APP_ENV=production`.


## Interview Discussion

### Why this approach?

Env injection is the lowest-friction secret channel for local multi-repo demos.

### Alternative approaches

Vault agent sidecars. Correct for prod.

### Trade-offs

Defaults invite accidental insecure deploys if unchecked.

### Enterprise adoption

No secrets in images; rotate JWT; short-lived cloud keys.

### Scaling considerations

Per-service identity (IRSA/Workload Identity).

### Principal Architect interview questions

**Q1. Are secrets in Git?**  
Should not be — `.env` ignored; defaults exist in YAML for local only.

**Q2. Does AI store API keys in logs?**  
Regex data masker targets emails/SSN/`sk|rk-` style keys; still avoid logging secrets.
