# Testing Strategy

## Unit / component tests

- **Vitest** + jsdom + Testing Library
- Setup: `src/test/setup.ts`
- Examples: `shared/api` foundation/token tests, hooks tests, shared components tests, utils tests
- Scripts: `test`, `test:watch`, `test:coverage` (coverage via `@vitest/coverage-v8`)

No hard coverage threshold encoded in the small `vitest.config.ts` shown — coverage is available via script, not necessarily fail-under in config.

## Integration testing

Component tests with providers/theme; API tests mock Axios/storage rather than live Business by default.

## End-to-end

- **Playwright** (`e2e/*.spec.ts`) — auth-and-navigation, authenticated-navigation
- Authenticated flows may seed a **synthetic localStorage session** rather than full live CRUD against Business
- Scripts: `test:e2e`, `test:e2e:ui`

## Mocking

Vitest mocks for modules/stores; localStorage cleared in token tests; E2E may use real or stubbed backend depending on environment (see playwright config/README for local expectations).

## Interview Discussion

### Why this architecture?

Vitest aligns with Vite; Playwright covers critical auth navigation; Testing Library prefers accessible queries.

### Alternative approaches

Jest + CRA; Cypress. Current stack matches Vite 6.

### Trade-offs

E2E depends on Business availability unless fully mocked — keep smoke tests lean.

### Scaling considerations

Add MSW for API mocking; visual regression; a11y axe in Playwright.

### How would this evolve?

Contract tests against OpenAPI; increase coverage gates gradually.

### Common Frontend Architect interview questions

**Q1. What runs in CI for FE?**  
Lint/typecheck/vitest/playwright as configured in the repo’s CI (if present) — locally via package scripts.

**Q2. How do you test refresh interceptors?**  
Unit tests around token/interceptor behavior with mocked axios.
