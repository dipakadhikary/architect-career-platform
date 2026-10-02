# Behavioral & Leadership Interview Guide

## Stories mapped to ACOS work

| Competency | Story angle |
| --- | --- |
| Technical leadership | Separating AI Platform despite dual-stack cost |
| Influence | Contract-first adoption vs shared DTO JAR temptation |
| Conflict | Speed (hand Feign) vs purity (generated SDK) — interim alignment |
| Failure | Env default mismatch Postgres credentials — improved runbooks |
| Prioritization | Document honesty over fake CD screenshots |
| Mentoring | Checklists for contracts SemVer + Web never calls AI |

## Sample behavioral questions

**Tell me about a trade-off you defended.**  
Modular monolith + AI sidecar over microservices — ship domain value, keep AI blast radius isolated.

**Describe a time you reduced risk.**  
AI feature flag default false; Resilience4j; kill switch in incidents.

**How do you handle incomplete platforms?**  
Label stubs (AsyncAPI, MCP, CI gaps); put them on roadmap; don't market as done.


## Interview Discussion

### Why this approach?

Behavioral answers need concrete artifacts (flags, ADRs, CI).

### Alternative approaches

Generic STAR without numbers. Forgettable.

### Trade-offs

Honesty about gaps can feel negative — frame as maturity plan.

### Enterprise adoption

Show how you'd run an architecture review.

### Scaling considerations

People scale: CODEOWNERS, platform team.

### Principal Architect interview questions

**Q1. How do you prevent scope creep?**  
DoD + contract SemVer + ADR for cross-cuts.

**Q2. Example of saying no?**  
No browser→AI; no Kafka until consumers exist.
