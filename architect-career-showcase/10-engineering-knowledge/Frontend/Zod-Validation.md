# Zod Validation

## Introduction

Zod defines schemas that parse/validate data at runtime and infer TypeScript types.

## Problem Statement

Compile-time types cannot validate untrusted JSON or env vars.

## Why ACOS Uses This

ACOS validates `import.meta.env` and form inputs (often with react-hook-form resolvers).

## Implementation Overview

ACOS validates `import.meta.env` and form inputs (often with react-hook-form resolvers).

## Best Practices

Parse at boundaries; fail fast on env; sanitize markdown separately (`rehype-sanitize`).

## Common Mistakes

- Using Zod only in UI and skipping Business validation.
- Over-permissive `passthrough` on sensitive objects.

## Alternative Approaches

Yup; AJV; io-ts.

## Trade-offs

DX + inference vs bundle size. Chosen for forms/env.

## References to ACOS modules

- [05-web-platform/Forms-and-Validation.md](../../05-web-platform/Forms-and-Validation.md)

## Interview Questions

**Q:** Env validation failure mode?
**A:** console.error + throw on boot parse failure.

**Q:** Replaces OpenAPI?
**A:** No—complements client-side checks.

## Further Reading

- zod.dev

