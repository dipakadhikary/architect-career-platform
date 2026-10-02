# Guardrails

## Implementation

`HeuristicGuardrails` implements `GuardrailsPort`, used by `AiExecutionPipeline` for input and output validation.

Settings: `guardrails_enabled=true` (default), `guardrails_pii_redaction_enabled=true`, `guardrails_max_input_chars=20000`.

## Checks

| Rule | Behavior |
| --- | --- |
| Empty input | `BLOCK` |
| Max length | `BLOCK` when input exceeds max chars |
| Prompt injection / jailbreak | Regex patterns (ignore previous instructions, system prompt, DAN, etc.) → `BLOCK` |
| Content moderation | Heuristic disallowed phrases → `BLOCK` |
| PII | Detect email/phone/ssn → `REDACT` with `[REDACTED_*]` placeholders |

Verdicts: `ALLOW`, `REDACT`, `BLOCK` (`GuardrailVerdict`). Findings raise/propagate as `GuardrailViolationError` when blocked in the pipeline.

## Related security ports

Pipeline also uses `PromptSanitizerPort` and `DataMaskerPort` for additional sanitization/masking around execution.

## Future hallucination detection

Not implemented as a dedicated guardrail. Groundedness heuristics exist in evaluation — complementary, not a hard block today.

## Interview Discussion

### Why this architecture?

Deterministic heuristics give offline-safe, dependency-free guardrails suitable for CI and demos, with clear extension to model-based moderators later.

### Alternative approaches

OpenAI Moderations API only; LlamaGuard; human review only. Heuristics are the baseline layer.

### Trade-offs

Regex injection detection is bypassable; not a substitute for defense-in-depth (authz, least privilege tools, output filtering).

### Scaling considerations

Keep guardrails sync and fast; escalate to async model moderators for high-risk tenants.

### How would this evolve?

ML classifiers; policy packs per capability; multilingual PII; hallucination blockers tied to citations.

### Principal AI Architect interview questions

**Q1. Are guardrails optional?**  
Yes via `guardrails_enabled`, default on.

**Q2. Does redaction fail the request?**  
PII typically `REDACT` (allow with modified text); injection/moderation `BLOCK`.
