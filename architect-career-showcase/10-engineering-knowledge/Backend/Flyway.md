# Flyway

## Introduction

Flyway versions SQL migrations (`V1__...`) applied at startup for repeatable schema evolution.

## Problem Statement

Manual DBA scripts drift between environments and developers.

## Why ACOS Uses This

ACOS enables Flyway with migrations `V1`–`V12` covering auth refresh tokens and domain tables.

## Implementation Overview

Add forward-only migrations; never edit applied checksums casually; use test profile with Testcontainers.

## Best Practices

- Expand-contract for breaking changes.
- Keep migrations idempotent-safe.
- Review indexes with queries.

## Common Mistakes

- Editing old migrations already in prod.
- Huge data backfills in a blocking migration without plan.

## Alternative Approaches

Liquibase; Rails-style migrations; schema-by-ORM.

## Trade-offs

Simple SQL-first vs Liquibase XML richness. ACOS prefers readable SQL.

## References to ACOS modules

- `src/main/resources/db/migration`
- Persistence docs in chapter 04

## Interview Questions

**Q:** Can I change V3 after release?
**A:** Not safely—add V13.

**Q:** Test strategy?
**A:** Apply migrations on Testcontainers Postgres.

## Further Reading

- flywaydb.org

