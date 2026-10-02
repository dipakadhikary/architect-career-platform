# Coding Standards

## Enforced mechanically

| Repo | Enforcement |
| --- | --- |
| Business | Spotless google-java-format; Checkstyle; PMD; SpotBugs |
| Web | ESLint + Prettier (`semi`, singleQuote, printWidth 100) |
| AI | ruff + black (100 cols, py313); mypy strict (CI soft) |
| Contracts | Prettier; OpenAPI validation; generator compile |

## Definition of Ready (practical)

- Problem/user story clear  
- API/contract impact identified  
- Security/flags considered  
- Test approach named  

## Definition of Done (practical)

- Code meets linters/formatters  
- Tests added/updated  
- `mvn verify` / npm/AI gates green locally (and CI where present)  
- Docs/CHANGELOG updated when contracts change  
- No secrets committed  


## Interview Discussion

### Why this approach?

Mechanical gates beat style debates.

### Alternative approaches

Only human review. Inconsistent.

### Trade-offs

mypy/bandit soft fails reduce certainty.

### Enterprise adoption

Make soft gates hard once debt cleared.

### Scaling considerations

Shared style configs via org templates.

### Principal Architect interview questions

**Q1. Java format style?**  
Google Java Format via Spotless.

**Q2. AI line length?**  
100.
