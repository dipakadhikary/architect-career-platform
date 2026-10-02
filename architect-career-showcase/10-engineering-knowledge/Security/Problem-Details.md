# Problem Details (RFC 9457)

## Introduction

Problem Details standardizes HTTP error payloads (`type`, `title`, `status`, `detail`, extensions) often as `application/problem+json`.

## Problem Statement

Ad-hoc `{error: string}` shapes break clients across languages.

## Why ACOS Uses This

AI contracts define ProblemDetails and reusable error responses; Business uses JSON auth errors and ApiResponse patterns for product APIs.

## Implementation Overview

AI contracts define ProblemDetails and reusable error responses; Business uses JSON auth errors and ApiResponse patterns for product APIs.

## Best Practices

Use consistent content types; include correlation IDs; avoid leaking stack traces.

## Common Mistakes

- Different error schemas per endpoint.
- Returning 200 with error blobs.

## Alternative Approaches

gRPC status codes; GraphQL errors array; custom envelopes only.

## Trade-offs

Standard clients vs migration from legacy envelopes. ACOS AI edge standardizes on problem+json.

## References to ACOS modules

- [07-ai-contracts/Schema-Design.md](../../07-ai-contracts/Schema-Design.md)

## Interview Questions

**Q:** Media type?
**A:** `application/problem+json`.

**Q:** Replaces ApiResponse?
**A:** Complements—success envelopes vs error documents.

## Further Reading

- RFC 9457

