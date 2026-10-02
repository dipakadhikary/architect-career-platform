# Contract-First Design

## Introduction

Contract-first means the API/event schema is designed and validated before (or as the gate for) multi-language client/server code generation.

## Problem Statement

Code-first APIs drift across Java Feign, Python FastAPI, and TypeScript clients until production breaks.

## Why ACOS Uses This

ACOS keeps AI REST/events in `architect-career-ai-contracts` (OpenAPI 3.1, AsyncAPI 3.0), generates SDKs, and vendors Python into the AI Platform.

## Implementation Overview

`mvn clean verify` validates, bundles, generates Java Feign / Python / TypeScript, and packages artifacts. Business product APIs remain Spring-first with springdoc; AI edge is contract-first.

## Best Practices

- Stable `operationId`s; Problem Details for errors; SemVer discipline.
- Review YAML PRs for compatibility before consumer code.

## Common Mistakes

- Hand-editing generated sources.
- Breaking required fields in a PATCH.

## Alternative Approaches

Code-first + exported schema; gRPC/protobuf; GraphQL schema-first.

## Trade-offs

Strong multi-consumer alignment vs slower iteration and generator limits (e.g., Java records).

## References to ACOS modules

- [07-ai-contracts](../../07-ai-contracts/)
- Chapter 07 Contract-First and Code-Generation docs

## Interview Questions

**Q:** Why OpenAPI over shared Java DTOs?
**A:** Python and TS cannot consume Java classes idiomatically.

## Further Reading

- OpenAPI Specification
- AsyncAPI Specification
