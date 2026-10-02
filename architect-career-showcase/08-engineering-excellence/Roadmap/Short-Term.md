# Short-Term Roadmap (0–3 months)

## Engineering

- Add GitHub Actions to Business (`mvn verify`) and Web (`lint/test/build`)  
- Fix Business Prometheus registry dependency  
- Add AI `.env.example`; align DB defaults documentation  
- Enable contracts GitHub Packages publish (Java first)  
- Raise JaCoCo above 0 with measured baseline  
- Complete Business AI BFF paths expected by Web  

## Security / ops

- Explicit CORS allowlist; security headers / CSP at edge  
- Turn AI auth on in docker-compose production profile samples  
- Remove or provide otel-collector service reference  

## Testing

- Contract test Feign vs OpenAPI paths  
- Make one soft CI scan hard-fail  


## Interview Discussion

### Why this approach?

Short-term = close honesty gaps that block demos and trust.

### Alternative approaches

Jump to K8s. Premature.

### Trade-offs

Capacity contest with features.

### Enterprise adoption

Treat as platform reliability epic.

### Scaling considerations

Template workflows for new repos.

### Principal Architect interview questions

**Q1. First CI target?**  
Business and Web quality gates on PR.

**Q2. First publish target?**  
Java SDK to GitHub Packages.
