# LangFuse Concepts

## Introduction

LangFuse is an LLM engineering platform for traces, prompt observability, and evaluation workflows.

## Problem Statement

Prometheus alone lacks prompt/token spans meaningful to AI developers.

## Why ACOS Uses This

ACOS integrates LangFuse behind `LANGFUSE_ENABLED` (default false) with required keys in production if enabled; used from adapters/evaluators.

## Implementation Overview

ACOS integrates LangFuse behind `LANGFUSE_ENABLED` (default false) with required keys in production if enabled; used from adapters/evaluators.

## Best Practices

Keep PII out of traces; sample; don't make LangFuse a prod hard dependency for CRUD.

## Common Mistakes

- Sending raw secrets in prompts to SaaS without review.
- Enabling without keys.

## Alternative Approaches

OpenTelemetry-only; custom SQL prompt logs; provider dashboards only.

## Trade-offs

AI-native insight vs extra vendor. Optional in ACOS.

## References to ACOS modules

- AI Observability chapter 06/08

## Interview Questions

**Q:** Default enabled?
**A:** False.

**Q:** Replaces Prometheus?
**A:** Complements—different audience/signal.

## Further Reading

- langfuse.com docs

