# ADR-029: Testing Strategy

# Status

Accepted

# Date

2026-08-10

# Context

Multi-repo architecture requires executable verification at unit, integration, and contract levels.

# Problem Statement

Architecture claims without tests become portfolio fiction.

# Decision Drivers

- Maintainability
- Security
- Developer Experience

# Alternatives Considered

- **Manual testing only** — Rejected.
- **Only e2e across everything** — Too slow/flaky as sole strategy.
- **Identical tooling across Java/Python/TS** — Unrealistic; use ecosystem standards.

# Decision

Business: JUnit + Testcontainers + Maven quality gates. Web: Vitest + Playwright. AI: pytest (+ coverage gate in CI). Contracts: validate-and-generate CI. Prefer pyramid over e2e-only.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Real Postgres tests.
- Contract generation gated.
- AI pipeline unit coverage.

# Negative Consequences

- Business/Web lack in-repo GH Actions comparable to AI/Contracts.
- Coverage gates uneven.
- Full AI UX e2e blocked by BFF gaps.

# Trade-offs

- Higher CI cost vs confidence.

# Risks

- False confidence if integration flags keep AI disabled in most tests.

# Future Evolution

- Unified CI across repos; deeper e2e with AI enabled profiles.

# References

- `architect-career-operating-system/pom.xml`
- `architect-career-web/package.json`
- `architect-career-ai-platform/.github/workflows/ci.yml`
- `architect-career-ai-contracts/.github/workflows`

## Interview Discussion

### Why was this approach selected?

Test at the seam that fails: DB, contracts, pipeline, UI routes.

### When would you choose another approach?

Don't replace unit tests with Playwright.

### How would this decision change for 10x / 100x / 1000x users?

At scale, contract tests and canaries matter more than expanding brittle UI e2e.

### Common Principal Architect interview questions

**Q1. How do you test AI without OpenAI keys?**

hashing/memory adapters + mocks.

### Common follow-up questions

- What does contracts CI prove?

