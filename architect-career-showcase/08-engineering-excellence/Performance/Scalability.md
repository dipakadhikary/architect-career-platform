# Scalability

## Current scale model

**Vertical / single-instance demos.** No autoscaling configs.

| Tier | Scale unit today | Next step |
| --- | --- | --- |
| Web | Static assets + browser | CDN |
| Business | One JVM | Multiple instances + sticky-less JWT |
| Postgres | One container | Managed HA |
| AI | One uvicorn process | Multiple workers/replicas + shared Redis/Qdrant |

## Concurrency controls already present

- AI bulkhead / CB / retry  
- Business AI bulkhead 20  
- Web TanStack Query defaults (refetchOnWindowFocus false per prior docs)  

## Future Roadmap

K8s HPA; queue-based async AI; read replicas; Qdrant clustering.


## Interview Discussion

### Why this approach?

Correctness and architecture before horizontal scale.

### Alternative approaches

Serverless per endpoint. Different ops model.

### Trade-offs

Single-node rate limit and semantic cache limits.

### Enterprise adoption

Stateless app tiers; stateful stores managed.

### Scaling considerations

Separate scale axes for RAG vs chat vs CRUD.

### Principal Architect interview questions

**Q1. Stateless Business sessions?**  
Yes — Spring Session STATELESS + JWT.

**Q2. Shared rate limit across AI replicas?**  
Not today — in-memory only.
