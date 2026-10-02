# Database Optimization

## PostgreSQL (Business)

- Flyway migrations `V1`–`V12`  
- JPA/Hibernate access  
- Hikari pool sizing for local  
- Testcontainers Postgres 17.5 for ITs  

## Vector database (AI)

- Qdrant optional via `QDRANT_ENABLED` / URL  
- Keyword/search behaviors documented in AI Platform chapter (scroll + local score patterns where applicable)  
- Redis used for cache, not primary relational store  

## Not implemented

- Explicit read replicas  
- Partitioning strategies  
- Automated `EXPLAIN` CI  
- Business query cache  

## Practices to keep

Index migrations with Flyway; avoid N+1 in new repositories; monitor pool exhaustion via Actuator.


## Interview Discussion

### Why this approach?

Flyway + pool limits beat premature sharding.

### Alternative approaches

NoSQL for everything. Poor fit for career transactional data.

### Trade-offs

Single Postgres for all domains in the modular monolith.

### Enterprise adoption

PgBouncer; slow-query log; index reviews.

### Scaling considerations

Split analytics later; keep OLTP lean.

### Principal Architect interview questions

**Q1. Migration tool?**  
Flyway.

**Q2. Qdrant required to boot AI?**  
No — can run degraded/disabled depending on settings/tests.
