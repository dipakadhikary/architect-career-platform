# TanStack Query

## Introduction

TanStack Query manages server state: caching, deduplication, retries, and invalidation for HTTP data.

## Problem Statement

Storing API lists only in Zustand duplicates cache invalidation bugs.

## Why ACOS Uses This

ACOS uses Query for domain fetches; `refetchOnWindowFocus: false` (per platform docs); offline banner invalidates on reconnect; Devtools behind env flag.

## Implementation Overview

ACOS uses Query for domain fetches; `refetchOnWindowFocus: false` (per platform docs); offline banner invalidates on reconnect; Devtools behind env flag.

## Best Practices

Keys per resource; invalidate on mutations; keep auth tokens out of Query cache payloads when possible.

## Common Mistakes

- Caching sensitive data forever.
- Using Query for pure UI toggles.

## Alternative Approaches

SWR; RTK Query; hand-written fetch + useEffect.

## Trade-offs

Excellent UX caching vs mental model cost. ACOS splits server (Query) vs client (Zustand).

## References to ACOS modules

- [05-web-platform/State-Management.md](../../05-web-platform/State-Management.md)

## Interview Questions

**Q:** Query vs Zustand?
**A:** Server/async vs client/session UI state.

**Q:** Why disable refetch on focus?
**A:** Avoid surprising refetch churn in this app's UX choice.

## Further Reading

- tanstack.com/query

