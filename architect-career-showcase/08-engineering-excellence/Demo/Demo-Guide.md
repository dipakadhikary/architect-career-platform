# Demo Guide

## Audience outcomes

Show a coherent career OS: auth → domain CRUD → optional AI assist → contracts/quality story.

## Suggested flow (20–25 min)

1. **Architecture slide** — four repos + trust boundary (2 min)  
2. **Live Web** — register/login, Knowledge note, Learning plan, Career application (8 min)  
3. **Actuator** — `/actuator/health` readiness with db (1 min)  
4. **Enable AI** (if prepared) — index/search or summarize via Business; show AI metrics (5 min)  
5. **Contracts** — open aggregator YAML + CI artifact story (3 min)  
6. **Honesty close** — what is stubbed (AsyncAPI broker, CD, BFF gaps) (2 min)  

## Prep checklist

- [ ] Postgres up; Business migrated  
- [ ] Web `.env.development` proxy correct  
- [ ] Demo user credentials ready  
- [ ] AI up only if you will enable it  
- [ ] One correlation ID captured for storytelling  

## Failure fallback

If AI fails: disable flag and continue CRUD demo — that *is* the resilience story.


## Interview Discussion

### Why this approach?

Demos should prove boundaries and recovery, not only happy path.

### Alternative approaches

Slideware only. Weaker.

### Trade-offs

Live demos can fail — prepare kill switch narrative.

### Enterprise adoption

Record backup video.

### Scaling considerations

Scripted seed data for repeatability.

### Principal Architect interview questions

**Q1. What if Feign times out on stage?**  
Show CB/metrics; disable AI; CRUD continues.

**Q2. Must Qdrant be up?**  
Only for vector RAG demos — not for core CRUD.
