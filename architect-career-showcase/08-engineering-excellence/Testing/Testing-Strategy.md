# Testing Strategy

## Philosophy

Prefer **fast unit tests** near domain logic, **integration tests** at HTTP/persistence boundaries, **contract validation** for AI APIs, and **selective E2E** for navigation/auth UX. Do not claim coverage that gates do not enforce.

| Repo | Primary tools | Remote gate |
| --- | --- | --- |
| Business | JUnit + SpringBootTest + Testcontainers + MockMvc | Local `mvn verify` only |
| Web | Vitest + Testing Library + Playwright | Local npm scripts only |
| AI | pytest (+asyncio, cov) | CI fail under 55% |
| Contracts | Spec validate + generate compile | CI `mvn verify` |

## Coverage strategy (honest)

- Business JaCoCo **minimum 0.00**  
- AI CI **55%** lines on `app`  
- Web `test:coverage` available; **no thresholds** in vitest config  


## Interview Discussion

### Why this approach?

Match test style to stack; put hard gates where AI risk is highest.

### Alternative approaches

One E2E-only strategy. Slow and brittle.

### Trade-offs

Uneven coverage enforcement across repos.

### Enterprise adoption

Raise JaCoCo; add Business/Web CI; mutation testing later.

### Scaling considerations

Shard CI; quarantine flaky tests.

### Principal Architect interview questions

**Q1. Business IT naming?**  
Failsafe: `*IT`, `*ITCase`, `*IntegrationTest`.

**Q2. Web E2E browser?**  
Chromium only in Playwright config.
