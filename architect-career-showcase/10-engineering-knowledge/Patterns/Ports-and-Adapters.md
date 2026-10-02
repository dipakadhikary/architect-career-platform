# Ports & Adapters (Hexagonal)

## Introduction

Ports define capabilities; adapters implement technology details—keeping application core stable.

## Problem Statement

Enterprise AI concerns (audit, cache, guardrails) must be swappable and testable.

## Why ACOS Uses This

ACOS AI declares ports for guardrails, cache, router, pipeline, etc., with Redis/heuristic/in-memory adapters; tests disable heavy adapters.

## Implementation Overview

ACOS AI declares ports for guardrails, cache, router, pipeline, etc., with Redis/heuristic/in-memory adapters; tests disable heavy adapters.

## Best Practices

Name ports by intent; one adapter per concern; don't leak Redis types into ports.

## Common Mistakes

- Ports that return Redis-specific exceptions.
- “Hexagonal” folders with no real boundaries.

## Alternative Approaches

Classic layering only; plugin architectures.

## Trade-offs

Extra types vs test seams—worth it on AI platform.

## References to ACOS modules

- [06-ai-platform/Layered-Architecture.md](../../06-ai-platform/Layered-Architecture.md)

## Interview Questions

**Q:** Example port?
**A:** GuardrailsPort / AiExecutionPipelinePort style interfaces.

**Q:** Stub adapters?
**A:** MCP/A2A stubs illustrate ports without networked adapters.

## Further Reading

- Alistair Cockburn — Hexagonal Architecture

