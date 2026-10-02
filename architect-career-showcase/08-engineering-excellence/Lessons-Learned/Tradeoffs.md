# Trade-offs

| Decision | Benefit | Cost |
| --- | --- | --- |
| Modular monolith | Simpler txs & deploy | Coarse scale unit |
| Separate AI process | Isolation & Python ecosystem | Dual ops/CI |
| Contracts repo | Multi-language SoT | Extra PR choreography |
| localStorage JWT | Simple SPA auth | XSS residual |
| AI auth optional locally | Easy demos | Easy to misconfigure |
| Hand Feign alignment | Speed | Drift vs generated SDK |
| Spec-first AsyncAPI | Future-ready vocabulary | Spec/runtime lag |
| Checkstyle ban OTel on Business | Avoid half-wired tracing | No Java traces |


## Interview Discussion

### Why this approach?

Trade-off tables beat absolute claims.

### Alternative approaches

Best practice lists without costs. Naive.

### Trade-offs

Some interim choices need expiry dates.

### Enterprise adoption

Revisit quarterly with metrics.

### Scaling considerations

Revisit monolith split when team/load demands.

### Principal Architect interview questions

**Q1. Why accept hand Feign?**  
Unblock integration before Packages publish.

**Q2. Why ban OTel imports?**  
Prevent unused SDK sprawl until end-to-end plan exists.
