# Integration Testing

## Business

- `@SpringBootTest` + MockMvc examples (`AuthSecurityIntegrationTest`, domain ITs)  
- Testcontainers Postgres `17.5-alpine` via `PostgresTestSupport` with local Postgres fallback  
- Profile `application-test.yml`  

## AI

- Integration tests boot app with redis/qdrant/LLM/langfuse/otel/auth disabled in `conftest.py`  
- Cover OpenAPI surface, metrics, knowledge/agentic APIs  

## Web

- Playwright starts `npm run dev`; auth flows; authenticated nav with localStorage-seeded session  
- Not full live CRUD against Business in all specs  

## Contracts

- Maven verify is the integration of specs → multi-language artifacts  


## Interview Discussion

### Why this approach?

Testcontainers gives real SQL dialect confidence.

### Alternative approaches

H2 only. Diverges from Postgres 17.

### Trade-offs

Docker required for ideal Business IT runs.

### Enterprise adoption

Shared compose for IT; CI services: postgres.

### Scaling considerations

Reusable test fixtures across modules.

### Principal Architect interview questions

**Q1. Postgres image for tests?**  
`postgres:17.5-alpine`.

**Q2. AI tests hit real OpenAI by default?**  
No — conftest disables LLM and uses hashing embeddings / memory stores.
