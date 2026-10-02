# PostgreSQL

## Introduction

PostgreSQL is ACOS's transactional system of record for users, career, learning, knowledge notes metadata, and refresh tokens.

## Problem Statement

Career workflows need ACID transactions, relational integrity, and mature tooling.

## Why ACOS Uses This

Business uses Postgres 17.5 (compose alpine + Testcontainers). AI does not own product CRUD data.

## Implementation Overview

Business uses Postgres 17.5 (compose alpine + Testcontainers). AI does not own product CRUD data.

## Best Practices

Migrate via Flyway; size pools; back up volumes before claiming DR.

## Common Mistakes

- Using Qdrant as SoR for applications.
- Ignoring credential mismatch between compose and Spring defaults.

## Alternative Approaches

MySQL; CockroachDB; DynamoDB for all entities.

## Trade-offs

Strong consistency vs operational management. Correct default for ACOS domains.

## References to ACOS modules

- Business docker-compose
- Persistence chapter 04

## Interview Questions

**Q:** Schema name/app db?
**A:** Configured via `ACOS_DB_*` (commonly `acos`).

**Q:** Version in tests?
**A:** `postgres:17.5-alpine`.

## Further Reading

- postgresql.org docs

