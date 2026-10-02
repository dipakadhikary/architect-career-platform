# Mistakes & Footguns

| Mistake / smell | Impact | Lesson |
| --- | --- | --- |
| Compose DB defaults ≠ Spring local defaults | Onboarding failures | Single source for local credentials |
| Prometheus endpoint without registry dep | False sense of metrics | Verify dependencies match actuator exposure |
| OTEL collector hostname without service | Noisy failures | Don't reference undeployed deps |
| Missing AI `.env.example` | Setup friction | Treat example env as product |
| JaCoCo 0% gate | Coverage theater | Either enforce or remove |
| Soft-fail security scans | Issues can rot | Time-box to hard fail |
| Pagination schemas unused | Dead contract surface | Don't export unused components without note |
| Web AI clients ahead of Business BFF | Broken demos | Contract consumer-driven depth |


## Interview Discussion

### Why this approach?

Cataloging mistakes is a Principal signal.

### Alternative approaches

Hide gaps. Discovered in interviews painfully.

### Trade-offs

Long list can overwhelm — prioritize top five live.

### Enterprise adoption

Convert each into a backlog epic.

### Scaling considerations

Automated detection (dep presence tests).

### Principal Architect interview questions

**Q1. Example of config drift?**  
Postgres `acos` vs `postgres` user defaults.

**Q2. Coverage theater meaning?**  
Tool present with 0.00 minimum.
