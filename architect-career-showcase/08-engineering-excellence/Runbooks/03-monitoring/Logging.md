# Logging

## Purpose

Locate and correlate operational logs for incident response and debugging.

## Scope

Process stdout logs for Business (MDC correlation) and AI (structlog). No centralized ELK/Loki (**Future enhancement**).

## Audience

- Developer
- SRE
- Platform Engineer

## Preconditions

- Access to terminal sessions or `docker logs`

## Step-by-Step Procedure

1. **Business** — Spring Boot console; search MDC field `correlationId` matching `X-Correlation-Id`.
2. **AI** — structlog console/JSON; fields include correlation/request/trace/user when present.
   ```bash
   docker logs -f <ai-platform-container>
   ```
3. **Web** — browser console; ErrorBoundary messages; Network panel headers.
4. **Compose dependencies**
   ```bash
   docker logs acos-postgres
   docker logs <redis|qdrant>
   ```
5. **Sanitize** — never paste JWT, API keys, or raw PII into tickets; AI masker is heuristic only.

## Validation

Able to retrieve a contiguous story for one correlation ID across Web→Business→AI.

## Rollback

N/A

## Troubleshooting

| Issue | Fix |
| --- | --- |
| No correlation in AI | Header not forwarded / AI middleware |
| Docker log spam OTEL | Disable `OTEL_ENABLED` or add collector (**Future**) |

## Escalation

Escalate if logs suggest credential leakage — start Secret Rotation.

## References

- [../../Observability/Logging.md](../../Observability/Logging.md)
- [../01-developer/Debugging.md](../01-developer/Debugging.md)
