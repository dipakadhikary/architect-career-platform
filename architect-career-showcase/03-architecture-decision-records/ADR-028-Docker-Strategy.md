# ADR-028: Docker Strategy

# Status

Accepted (partial)

# Date

2026-08-10

# Context

Developers need reproducible dependencies for Postgres/Redis/Qdrant and an AI image path.

# Problem Statement

Undocumented manual installs of databases block onboarding; full K8s is premature.

# Decision Drivers

- Developer Experience
- Operational Complexity
- Cost

# Alternatives Considered

- **No containers** — Rejected for DB/vector deps.
- **Kubernetes-first local** — Too heavy for current stage.
- **One mega Compose for all apps** — Not present; split by repo ownership today.

# Decision

Provide Docker Compose for Business data (Postgres/pgAdmin) and AI stack (app/Redis/Qdrant). AI Platform includes a Dockerfile. Business app and Web images are not in-repo yet.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Fast dependency bring-up.
- AI can run containerized.

# Negative Consequences

- Fragmented Compose files.
- Business/Web app container gaps.
- OTel collector referenced but missing.

# Trade-offs

- Repo-local Compose vs unified DX.

# Risks

- Credential mismatches between Compose and Spring local profile.

# Future Evolution

- Unified dev Compose; Business/Web images; K8s manifests.

# References

- `architect-career-operating-system/infrastructure/docker/docker-compose.yml`
- `architect-career-ai-platform/Dockerfile`
- `architect-career-ai-platform/docker-compose.yml`

## Interview Discussion

### Why was this approach selected?

Containerize dependencies first, apps as they stabilize.

### When would you choose another approach?

Go K8s-first only with platform team support.

### How would this decision change for 10x / 100x / 1000x users?

Prod moves to managed services + k8s deployments; Compose remains DX.

### Common Principal Architect interview questions

**Q1. Is Web dockerized?**

Not in-repo currently.

### Common follow-up questions

- Which ports does AI Compose publish?

