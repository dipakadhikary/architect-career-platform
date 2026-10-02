# Testcontainers

## Introduction

Testcontainers boots real Docker dependencies (e.g., Postgres) for integration tests.

## Problem Statement

H2 lies about Postgres features, JSON, and constraints.

## Why ACOS Uses This

Business `PostgresTestSupport` uses `postgres:17.5-alpine` with fallback to local Postgres if Docker unavailable.

## Implementation Overview

Business `PostgresTestSupport` uses `postgres:17.5-alpine` with fallback to local Postgres if Docker unavailable.

## Best Practices

Reuse containers where possible; keep tests hermetic; don't require cloud DBs for unit tests.

## Common Mistakes

- Only H2 for all persistence tests.
- Depending on a shared dirty local DB without cleanup.

## Alternative Approaches

Embedded Postgres; docker-compose from Surefire; mocked repositories only.

## Trade-offs

Higher fidelity vs slower CI and Docker dependency.

## References to ACOS modules

- Business integration tests
- Testing chapter 08

## Interview Questions

**Q:** Failsafe vs Surefire?
**A:** ITs via Failsafe naming patterns.

**Q:** Fallback?
**A:** Env-configured local Postgres when Docker missing.

## Further Reading

- testcontainers.com

