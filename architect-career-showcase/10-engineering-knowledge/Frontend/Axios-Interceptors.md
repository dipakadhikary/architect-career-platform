# Axios Interceptors

## Introduction

Interceptors wrap requests/responses globally—auth headers, correlation IDs, and 401 refresh logic.

## Problem Statement

Per-call token attachment forgets edges and duplicates refresh races.

## Why ACOS Uses This

ACOS Axios instance attaches Bearer token + `X-Correlation-Id`; on 401 refreshes once (single-flight) then retries; failure emits session-expired.

## Implementation Overview

ACOS Axios instance attaches Bearer token + `X-Correlation-Id`; on 401 refreshes once (single-flight) then retries; failure emits session-expired.

## Best Practices

Exclude login/register/refresh from refresh loop; clear storage on hard failure.

## Common Mistakes

- Infinite 401 refresh loops.
- Multiple parallel refreshes without mutex.

## Alternative Approaches

fetch wrappers; ky; generated clients with middleware.

## Trade-offs

Central control vs hidden magic—document interceptor behavior for newcomers.

## References to ACOS modules

- [05-web-platform/API-Integration.md](../../05-web-platform/API-Integration.md)
- [05-web-platform/Authentication.md](../../05-web-platform/Authentication.md)

## Interview Questions

**Q:** Proactive refresh?
**A:** `isAccessTokenExpired` exists but isn't wired—reactive 401 refresh is the live path.

**Q:** Correlation ID source?
**A:** `crypto.randomUUID()` per request.

## Further Reading

- Axios docs — interceptors

