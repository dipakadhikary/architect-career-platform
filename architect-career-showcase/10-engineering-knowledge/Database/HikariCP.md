# HikariCP

## Introduction

HikariCP is the default JDBC pool in Spring Boot—low overhead, clear knobs for size and timeouts.

## Problem Statement

Unbounded connections exhaust Postgres; creating connections per request is slow.

## Why ACOS Uses This

ACOS local profile sets pool name `acos-local-pool`, max 10, min idle 2, connection/idle/max lifetime timeouts.

## Implementation Overview

ACOS local profile sets pool name `acos-local-pool`, max 10, min idle 2, connection/idle/max lifetime timeouts.

## Best Practices

Align pool max with Postgres `max_connections` and instance count; monitor wait times.

## Common Mistakes

- Max pool 100 on a tiny Postgres.
- Leaking connections via unclosed sessions.

## Alternative Approaches

Tomcat pool; c3p0; pgBouncer in front.

## Trade-offs

App-level pooling vs external pooler for many instances—future pgBouncer.

## References to ACOS modules

- `application-local.yml`
- Performance/DB optimization chapter 08

## Interview Questions

**Q:** Local max pool?
**A:** 10.

**Q:** Why min idle 2?
**A:** Warm connections for interactive demos without huge idle waste.

## Further Reading

- HikariCP GitHub wiki

