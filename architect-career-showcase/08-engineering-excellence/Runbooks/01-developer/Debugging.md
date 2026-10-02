# Debugging

## Purpose

Standardize cross-service debugging using correlation IDs, Actuator, and AI metrics.

## Scope

Local and shared-demo debugging of Web → Business → AI request paths.

## Audience

- Developer
- SRE
- Platform Engineer

## Preconditions

- Stack running or partially running
- HTTP client (curl/Insomnia) and browser DevTools
- Access to process stdout logs

## Step-by-Step Procedure

1. **Capture correlation ID**
   - Browser: Network request request headers → `X-Correlation-Id` (Web generates UUID).
   - Or set explicitly: `X-Correlation-Id: <uuid>` on Business/AI calls.

2. **Confirm hop health**
   - Business: `/actuator/health`, `/actuator/health/readiness`
   - AI: `/api/v1/system/liveness`, `/readiness`, `/api/v1/ai/health`

3. **Business AI path**
   - Check `AI_PLATFORM_ENABLED`
   - Call `GET /api/v1/integration/ai/health` with Bearer token
   - Inspect logs for `AiPlatformCallLogger` key=value lines and MDC `correlationId`

4. **Resilience signals**
   - Actuator metrics / custom meters: `acos.ai.platform.retries`, `.timeouts`, `.circuitbreaker.state`
   - If CB open: wait ~30s or fix AI, then retest

5. **AI platform**
   - structlog JSON/console fields for correlation/request ids
   - `GET /api/v1/system/metrics` for rate-limit / cache / HTTP series
   - Reproduce with provider flags off to validate offline fallbacks

6. **Web**
   - Axios 401 refresh loop: clear `localStorage` keys `acos.*` and re-login
   - ErrorBoundary → console `Unhandled UI error`

## Validation

Issue reproduced with a single correlation ID present in Business and AI logs; root cause classified (config, dependency, code).

## Rollback

N/A for read-only debug. If debug required temporary insecure settings, revert JWT secrets and auth flags immediately after.

## Troubleshooting

| Symptom | Checks |
| --- | --- |
| No correlation in Business logs | Filter/MDC not applied; wrong logger pattern |
| AI never called | Flag false; Feign not registered |
| 429 from AI | Rate limit 120/min — wait or raise `RATE_LIMIT_REQUESTS_PER_MINUTE` for local |

## Escalation

Escalate to Architect when defect implies contract drift (OpenAPI vs Feign/Pydantic mismatch).

## References

- [../03-monitoring/Logging.md](../03-monitoring/Logging.md)
- [../03-monitoring/Distributed-Tracing.md](../03-monitoring/Distributed-Tracing.md)
- ADR / Integration docs in chapters 04 & 08
