# OpenFeign

## Introduction

OpenFeign turns annotated Java interfaces into HTTP clients—ideal for typed service-to-service calls.

## Problem Statement

Manual RestTemplate/WebClient calls duplicate URL and error handling across AI endpoints.

## Why ACOS Uses This

ACOS defines Feign clients under `com.acos.integration.client` for Knowledge/Learning/Career/Portfolio AI paths aligned to OpenAPI. Generated Feign SDK exists in contracts but is not yet the Business dependency.

## Implementation Overview

Feign CB autoconfig is off; resilience wraps invocations in `AiPlatformInvoker`. Headers: Authorization, API key, correlation/trace/user.

## Best Practices

- Match `operationId`/paths to contracts.
- Centralize error decoding.
- Timeouts via config + TimeLimiter.

## Common Mistakes

- Enabling Feign CB and custom CB simultaneously without clarity.
- Blocking forever without timeouts.

## Alternative Approaches

WebClient; Retrofit; generated SDK JAR as sole client.

## Trade-offs

Ergonomic interfaces vs annotation magic. Hand clients risk drift from contracts.

## References to ACOS modules

- Integration module in Business Platform
- [07-ai-contracts/Java-SDK.md](../../07-ai-contracts/Java-SDK.md)

## Interview Questions

**Q:** Why not only RestClient?
**A:** Feign maps cleanly to many AI operations as interface methods.

**Q:** Chat Feign?
**A:** Not implemented yet—contract exists on AI side.

## Further Reading

- Spring Cloud OpenFeign docs

