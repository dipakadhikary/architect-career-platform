# Performance and Scalability

## Implemented practices

| Concern | Practice |
| --- | --- |
| Pagination | All major list/search endpoints use `Pageable` + max page size caps |
| Lazy loading | `open-in-view: false`; services use explicit fetch methods (e.g. learning plan with details) |
| Connection pooling | HikariCP tuned in local profile (max 10) |
| Indexes | Owner and lookup indexes in Flyway |
| AI off request path | Knowledge indexing `@Async` after commit |
| AI bulkhead | Resilience4j bulkhead max concurrent AI calls (20) |
| Compression | Feign request/response compression configurable |
| Timeouts | Feign connect 3s / read 30s; TimeLimiter 30s |

## Caching

No Spring Cache (`@Cacheable`) layer is implemented on domain reads today. Do not document Redis caching for Business Platform — Redis belongs to the AI Platform runtime.

## Database optimization

- UUID PKs with owner indexes
- Search endpoints avoid unbounded scans via paging
- Career Specifications compose predicates for selective filters
- Soft-archive reduces noise on active career lists without physical deletes across career aggregates

## Batch processing

No Spring Batch jobs in this repository. Async executor handles AI indexing fan-out only.

## Future scaling (guidance, not implemented)

| Scale | Likely moves |
| --- | --- |
| 10× users | Raise Hikari sizing carefully; add read replica; CDN for Web only |
| 100× | Outbox + queue for AI indexing; cache hot read DTOs; split reporting reads |
| 1000× | Shard or partition by owner; extract AI workers; possibly extract career search to dedicated read model |

Horizontal scale of stateless Business nodes is feasible because JWT is stateless; refresh token table and DB become the shared bottleneck.

## Interview Discussion

### Why this architecture?

Optimize the common case (paged CRUD) and isolate slow AI without introducing distributed cache complexity early.

### Alternative approaches

Cache-aside Redis on Business; CQRS from day one; Elasticsearch for knowledge search (AI vector search covers semantic case separately).

### Trade-offs

Placeholder dashboard avoids expensive aggregations today but cannot show live KPIs. Missing Business-side cache means repeated reads hit Postgres.

### Scaling considerations

Measure Feign CB open rate and DB p99 before splitting services.

### Principal Architect interview questions

**Q1. Is the monolith a scalability dead end?**  
No — scale vertically/horizontally first; split on measured seams (often AI workers before domain microservices).

**Q2. What is the first bottleneck you expect?**  
Postgres connections under chatty list endpoints, or AI indexing backlog if enabled without a broker.
