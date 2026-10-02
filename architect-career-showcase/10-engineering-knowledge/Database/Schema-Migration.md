# Schema Migration

## Introduction

Automated migrations apply versioned DDL so every environment converges.

## Problem Statement

Schema drift causes “works on my machine” production outages.

## Why ACOS Uses This

Flyway on Business boot; ordered `V1`…`V12` scripts. AI/Qdrant schemas are separate concerns.

## Implementation Overview

Flyway on Business boot; ordered `V1`…`V12` scripts. AI/Qdrant schemas are separate concerns.

## Best Practices

Expand/contract; never rewrite history; include indexes with access paths.

## Common Mistakes

- Manual prod SQL undocumented.
- Destructive drops without backup.

## Alternative Approaches

Liquibase; migrate-on-deploy jobs only; ORM `ddl-auto=update` in prod.

## Trade-offs

SQL clarity vs some duplication with entities. ACOS chooses Flyway as SoT.

## References to ACOS modules

- Backend/Flyway.md
- db/migration folder

## Interview Questions

**Q:** Source of truth for columns?
**A:** Flyway SQL.

**Q:** AI vectors migrated by Flyway?
**A:** No—Qdrant collections managed by AI platform.

## Further Reading

- Flyway migrations concept docs

