# TypeScript

## Introduction

TypeScript adds static types to JavaScript, catching API shape mistakes before runtime.

## Problem Statement

Untyped Axios responses silently break UI when Business DTOs change.

## Why ACOS Uses This

ACOS Web is TS-first (`tsc -b` in build). Contracts also generate a TS SDK (`@acos/ai-contracts`) not yet wired into Web.

## Implementation Overview

ACOS Web is TS-first (`tsc -b` in build). Contracts also generate a TS SDK (`@acos/ai-contracts`) not yet wired into Web.

## Best Practices

Prefer typed API modules; use Zod for runtime validation at boundaries; avoid `any` in shared code.

## Common Mistakes

- Believing compile-time types validate server payloads.
- Duplicating types by hand when OpenAPI exists for AI BFF future.

## Alternative Approaches

JSDoc JS; Flow; Dart.

## Trade-offs

Safety vs ceremony. ACOS accepts TS everywhere in Web.

## References to ACOS modules

- Web `tsconfig` + feature API modules
- [07-ai-contracts/TypeScript-SDK.md](../../07-ai-contracts/TypeScript-SDK.md)

## Interview Questions

**Q:** Types vs Zod?
**A:** Types erase at runtime; Zod checks actual JSON.

**Q:** Is generated AI SDK used?
**A:** Not in Web today.

## Further Reading

- typescriptlang.org

