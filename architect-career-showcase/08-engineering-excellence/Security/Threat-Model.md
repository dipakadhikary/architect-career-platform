# Threat Model

## Assets

- User credentials & JWT/refresh material  
- Career/learning/portfolio PII-ish content  
- LLM API keys & AI prompts/outputs  
- Vector store contents  

## STRIDE-style view (selected)

| Threat | Example | Mitigation today | Residual |
| --- | --- | --- | --- |
| Spoofing | Stolen access token | Short TTL 15m; refresh rotation | XSS → localStorage theft |
| Tampering | Forged AI requests from browser | No direct AI access | Compromised Business host |
| Repudiation | Disputed AI action | AI audit hooks / correlation IDs | Incomplete centralized SIEM |
| Info disclosure | Prompt leakage | Guardrails + masker | Logs may still leak content |
| DoS | AI endpoint flood | Rate limit; Resilience4j bulkhead | Single-node limits |
| Elevation | Call admin APIs | Auth required | Weak role model |

## Trust assumptions

Local demo defaults are **not** production-hardened. Production AI validation raises the bar; Business still needs prod profile + secret hygiene.


## Interview Discussion

### Why this approach?

Explicit residual risk is more honest than claiming zero trust completion.

### Alternative approaches

Full formal TARA with threat library. Next maturity step.

### Trade-offs

Documented risks without automated control tests for all rows.

### Enterprise adoption

Map to ISO 27001 / SOC2 control catalog.

### Scaling considerations

Abuse case tests in CI; red-team prompts for guardrails.

### Principal Architect interview questions

**Q1. Top residual browser risk?**  
XSS stealing localStorage tokens.

**Q2. Is AI open by default locally?**  
Yes if all AI auth flags are false.
