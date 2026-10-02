# Review Checklist

## All PRs

- [ ] Scope matches ticket; no drive-by refactors  
- [ ] Tests cover happy path + key failures  
- [ ] No secrets / `.env` files  
- [ ] Correlation/logging adequate for new I/O  
- [ ] Feature flags default safe  

## Business

- [ ] `mvn verify` considered  
- [ ] Flyway migration backward compatible  
- [ ] Security filter implications for new routes  
- [ ] Feign/DTO aligned to AI contracts if AI touch  

## Web

- [ ] Lazy routes / a11y for new UI  
- [ ] Zod validation for forms  
- [ ] No direct AI Platform calls  

## AI

- [ ] Guardrails/pipeline path considered  
- [ ] Settings documented; prod validation still holds  
- [ ] Vendored contracts updated if schemas changed  

## Contracts

- [ ] SemVer impact correct  
- [ ] Examples + problem+json  
- [ ] Stable unique `operationId`  
- [ ] CHANGELOG updated  


## Interview Discussion

### Why this approach?

Checklists catch cross-repo footguns (especially AI + contracts).

### Alternative approaches

Unstructured review. Misses contract SemVer.

### Trade-offs

Checklist fatigue — keep short.

### Enterprise adoption

CODEOWNERS + required reviews.

### Scaling considerations

Automate checklist items as CI annotations.

### Principal Architect interview questions

**Q1. May Web call AI directly?**  
No — architecture rule.

**Q2. Contracts breaking change requires?**  
MAJOR SemVer + migration notes.
