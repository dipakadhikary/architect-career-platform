# AsyncAPI & Eventing Concepts

## Introduction

AsyncAPI describes asynchronous channels/messages analogously to OpenAPI for HTTP—useful before a broker exists.

## Problem Statement

Teams invent incompatible JSON events when Kafka finally arrives.

## Why ACOS Uses This

ACOS contracts define channels like `acos.ai.knowledge.indexed.v1` with CloudEvents-compatible envelopes; generation exports JSON Schemas; **no broker consumers in Business/AI yet**.

## Implementation Overview

ACOS contracts define channels like `acos.ai.knowledge.indexed.v1` with CloudEvents-compatible envelopes; generation exports JSON Schemas; **no broker consumers in Business/AI yet**.

## Best Practices

Freeze channel addresses; evolve payloads with SemVer; implement one vertical slice before expanding.

## Common Mistakes

- Claiming events are “done” because YAML exists.
- Renaming addresses casually.

## Alternative Approaches

Code-first Spring Cloud Stream without spec; CloudEvents SDK only.

## Trade-offs

Early vocabulary vs spec/runtime lag—documented honestly in ACOS.

## References to ACOS modules

- [07-ai-contracts/AsyncAPI-Architecture.md](../../07-ai-contracts/AsyncAPI-Architecture.md)

## Interview Questions

**Q:** Is Kafka required to use contracts?
**A:** No.

**Q:** Envelope style?
**A:** CloudEvents-compatible naming, transport-agnostic.

## Further Reading

- asyncapi.com
- cloudevents.io

