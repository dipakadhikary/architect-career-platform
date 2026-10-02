# What We Would Do Differently

1. **CI for Business and Web from day one** — even lint+test only  
2. **One local compose** for Postgres+Redis+Qdrant+optional apps  
3. **Publish contracts artifacts** before multiple consumers diverge  
4. **httpOnly cookie session** plan earlier for XSS posture  
5. **Enforce JaCoCo >0** once baseline measured  
6. **Ship `.env.example` with AI** alongside Settings  
7. **BFF and Web AI surfaces** versioned together  
8. **Wire OTel fully or not at all** — no dangling collector hostnames  

Key learning: **architecture clarity compounds; incomplete ops honesty matters as much as diagrams.**


## Interview Discussion

### Why this approach?

Retrospectives create the next roadmap.

### Alternative approaches

Blame tools. Less useful.

### Trade-offs

Hindsight ignores time constraints — acknowledge portfolio pace.

### Enterprise adoption

Turn into OKRs for platform team.

### Scaling considerations

Invest in paved roads (templates).

### Principal Architect interview questions

**Q1. Highest leverage fix?**  
Remote CI for Business/Web + contracts publish.

**Q2. Highest security fix?**  
Mandatory AI auth in non-dev + cookie/CSP hardening.
