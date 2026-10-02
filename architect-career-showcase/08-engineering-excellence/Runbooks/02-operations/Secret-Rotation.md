# Secret Rotation

## Purpose

Rotate credentials used by ACOS without leaving stale secrets in running processes.

## Scope

JWT HMAC secrets, DB passwords, AI API keys, LLM provider keys, LangFuse keys. No Vault automation (**Future enhancement**).

## Audience

- Platform Engineer
- DevOps Engineer
- SRE

## Preconditions

- Ability to restart Business/AI/Web
- Maintenance window if JWT secret rotation forces re-login
- DB access for password changes

## Step-by-Step Procedure

1. **Inventory** secrets in use (`ACOS_JWT_SECRET`, `ACOS_DB_PASSWORD`, `AI_PLATFORM_API_KEY`, `AUTH_*`, `OPENAI_API_KEY`, `QDRANT_API_KEY`, `LANGFUSE_SECRET_KEY`).

2. **JWT (`ACOS_JWT_SECRET`)**
   - Generate new secret (≥32 chars)
   - Restart Business with new env
   - **Effect:** existing access tokens invalid; refresh may fail → users re-authenticate
   - Optionally purge refresh tokens table if compromise suspected

3. **Database password**
   - `ALTER USER ... PASSWORD ...` in Postgres
   - Update compose/env `ACOS_DB_PASSWORD` / `POSTGRES_PASSWORD`
   - Rolling restart Business
   - Update `.env` / secret store copies

4. **AI service / provider keys**
   - Rotate provider key in vendor console
   - Update AI env; restart AI
   - Update Business `AI_PLATFORM_API_KEY` if used; restart Business

5. **Verify** auth login, one Feign call (if enabled), no secret values in logs

6. **Future enhancement:** sealed secrets, automatic rotation, dual-key JWT acceptance window

## Validation

Login works with new secret; old secret rejected; AI calls succeed with new provider key; logs show no plaintext secrets.

## Rollback

Revert env to previous secret and restart if rotation breaks shared demos—then schedule a proper window.

## Troubleshooting

| Issue | Fix |
| --- | --- |
| Users locked out after JWT rotate | Expected — communicate re-login |
| AI 401 after key rotate | Business API key not updated |
| DB auth fail | Business still on old password |

## Escalation

Escalate immediately on suspected secret leak to public git history — rotate, purge, and audit.

## References

- [../../Security/Secret-Management.md](../../Security/Secret-Management.md)
- [../06-checklists/Security-Checklist.md](../06-checklists/Security-Checklist.md)
