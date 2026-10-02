# Medium-Term Roadmap (3–9 months)

- Adopt generated Java SDK in Business; reduce hand DTOs  
- Wire TS types carefully (BFF OpenAPI or shared models — still no browser→AI)  
- Broker-backed AsyncAPI for key lifecycle events  
- Distributed rate limiting + shared semantic cache index  
- Structured JSON logging on Business; W3C Trace Context end-to-end  
- Postgres backup/restore automation; containerize Business & Web  
- Nightly AI eval job (LangFuse/DeepEval)  
- Real coverage thresholds on Web; expand Playwright beyond seeded sessions  


## Interview Discussion

### Why this approach?

Medium-term hardens platform productization.

### Alternative approaches

Feature-only roadmap. Accumulates debt.

### Trade-offs

Event bus adds ops cost.

### Enterprise adoption

Stage environment with prod-like auth.

### Scaling considerations

Horizontal AI replicas behind LB.

### Principal Architect interview questions

**Q1. AsyncAPI next step?**  
Pick one channel (e.g., knowledgeIndexed) and implement end-to-end.

**Q2. Tracing goal?**  
Trace Web correlation through Business into AI spans.
