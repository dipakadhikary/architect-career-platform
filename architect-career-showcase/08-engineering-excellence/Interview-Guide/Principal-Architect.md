# Principal Architect Interview Guide

## Narrative to own

ACOS is a **portfolio-grade multi-repo system**: modular monolith (Business) + dedicated AI runtime + contract SoT + React client. Decisions prioritize clear boundaries over premature microservices or cloud theater.

## Likely questions & suggested answers

**Q: Why not microservices for Knowledge/Learning/Career?**  
A: Shared transactional user journeys and one team — modular packages + single Postgres keep consistency; extract only with proven scale pain.

**Q: Why a separate AI Platform?**  
A: Different language ecosystem (Python AI), independent scaling/failure, guardrails/RAG/agent graphs shouldn't pollute domain modules.

**Q: Why contracts repo instead of Spring REST docs alone?**  
A: Multi-consumer (Java Feign, Python Pydantic, future TS); OpenAPI/AsyncAPI as SoT prevents DTO drift.

**Q: Biggest honesty gap?**  
A: Uneven CI (Business/Web local-only); AI BFF incomplete vs Web; AsyncAPI without broker; tracing partial; JaCoCo at 0%.

**Q: How do you kill AI blast radius?**  
A: `ai.platform.enabled=false`; Resilience4j isolation; browser never holds provider keys.

## Trade-off drill

Be ready to discuss localStorage JWT, Feign hand-alignment vs generated SDK, and OTel banned on Business via Checkstyle.


## Interview Discussion

### Why this approach?

Principal interviews reward boundary clarity + intellectual honesty.

### Alternative approaches

Overclaim production-ready K8s. Backfires under probe.

### Trade-offs

Must memorize real gaps.

### Enterprise adoption

Map each gap to a 30/60/90 plan.

### Scaling considerations

Explain scale axes per tier.

### Principal Architect interview questions

**Q1. What is the anti-corruption layer?**  
Business integration Facades/Gateways/Feign + DTOs isolating AI.

**Q2. Where do ADRs live?**  
Showcase chapter 03.
