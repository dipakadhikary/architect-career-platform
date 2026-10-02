# Architecture Governance

## Non-negotiables (implemented decisions)

1. Modular monolith for business domains (not microservice mesh)  
2. Separate AI Platform process  
3. Contract-first AI HTTP/events in dedicated repo  
4. Browser → Business only  
5. AI behind feature flag + anti-corruption layer  

## Change control

- Cross-cutting changes → update ADRs in showcase Chapter 03  
- AI API changes → contracts PR first  
- New shared libraries → justify vs duplication  

## Architecture Decision Records

Canonical ADRs live in `03-architecture-decision-records/` (ADR-001…030 + Future Decisions).


## Interview Discussion

### Why this approach?

Written ADRs prevent rediscovering rejected options.

### Alternative approaches

Wiki folklore. Drifts.

### Trade-offs

ADR upkeep cost.

### Enterprise adoption

Architecture review board for MAJOR contract changes.

### Scaling considerations

Fitness functions in CI (dependency rules).

### Principal Architect interview questions

**Q1. Where are ADRs?**  
Showcase chapter 03.

**Q2. Can Web add a Qdrant client?**  
No — violates trust boundary.
