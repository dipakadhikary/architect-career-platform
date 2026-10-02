# AI Guardrails

## Introduction

Guardrails are deterministic or model-based checks on inputs/outputs: injection, jailbreak, PII, length, moderation.

## Problem Statement

Unfiltered prompts enable prompt injection, data leakage, and abuse.

## Why ACOS Uses This

ACOS `HeuristicGuardrails` plus sanitizer and regex masker run in the enterprise path; toggles for PII redaction and max input chars.

## Implementation Overview

ACOS `HeuristicGuardrails` plus sanitizer and regex masker run in the enterprise path; toggles for PII redaction and max input chars.

## Best Practices

Fail closed on clear injection; don't rely on regex alone for production moderation forever; log verdicts with correlation IDs.

## Common Mistakes

- Only client-side filtering.
- Stripping logs of all signal so abuse can't be studied.

## Alternative Approaches

Provider moderation APIs; Llama Guard; human review queues.

## Trade-offs

Cheap heuristics vs bypassability. ACOS documents residual risk in threat model.

## References to ACOS modules

- [06-ai-platform/Guardrails.md](../../06-ai-platform/Guardrails.md)
- [08-engineering-excellence/Security/AI-Security.md](../../08-engineering-excellence/Security/AI-Security.md)

## Interview Questions

**Q:** Are guardrails ML classifiers?
**A:** Heuristic/regex-first today.

**Q:** Max input default?
**A:** Settings default around 20k chars (`GUARDRAILS_MAX_INPUT_CHARS`).

## Further Reading

- OWASP LLM Top 10

