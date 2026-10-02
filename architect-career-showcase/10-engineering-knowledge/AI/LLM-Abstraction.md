# LLM Abstraction

## Introduction

An LLM port/adapter layer isolates prompt execution from vendor SDKs (OpenAI, Azure OpenAI, Ollama).

## Problem Statement

Hard-coding one SDK locks cost, region, and offline demos.

## Why ACOS Uses This

ACOS model router / provider adapters select backends; enterprise pipeline invokes them behind policy and cost hooks.

## Implementation Overview

ACOS model router / provider adapters select backends; enterprise pipeline invokes them behind policy and cost hooks.

## Best Practices

Keep messages/prompts vendor-neutral at the port; translate in adapters; centralize timeouts.

## Common Mistakes

- Leaking Azure deployment names into domain services.
- No fallback when provider 429s.

## Alternative Approaches

Direct SDK in every handler; LangChain-only lock-in.

## Trade-offs

Flexibility vs more glue code. Essential for ACOS multi-provider story.

## References to ACOS modules

- [06-ai-platform/LLM-Abstraction.md](../../06-ai-platform/LLM-Abstraction.md)
- Model-Router docs

## Interview Questions

**Q:** Why Ollama?
**A:** Local/dev demos without cloud keys.

**Q:** Where do API keys live?
**A:** AI settings SecretStr—not the browser.

## Further Reading

- Hexagonal architecture applied to AI providers

