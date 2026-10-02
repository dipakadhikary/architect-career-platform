# ADR-025: JWT Security

# Status

Accepted

# Date

2026-08-10

# Context

Web and Business APIs need stateless authentication with revocable sessions.

# Problem Statement

Server-wide HTTP sessions scale poorly for SPA APIs; pure JWT without refresh strategy complicates revocation.

# Decision Drivers

- Security
- Scalability
- Developer Experience

# Alternatives Considered

- **Server sessions only** — Rejected as primary SPA approach.
- **JWT access tokens without refresh rotation** — Weaker revocation story.
- **External IdP/Keycloak mandatory** — Not implemented; Business issues tokens today.

# Decision

Issue short-lived JWT access tokens and opaque refresh tokens hashed in PostgreSQL. Web stores tokens in localStorage with Axios refresh rotation. AI auth remains optional service auth.

# Architecture Diagram

Not required for this decision beyond references in Context/Decision.

# Positive Consequences

- Stateless resource APIs.
- Refresh revocation via DB hash rows.
- Clear public auth routes.

# Negative Consequences

- localStorage XSS risk.
- httpOnly cookie migration still future.
- RBAC not broadly enforced on controllers.

# Trade-offs

- SPA convenience vs cookie security posture.

# Risks

- Default JWT secret if misdeployed; swagger permitAll surfaces.

# Future Evolution

- httpOnly cookies; broader method security; external IdP option.

# References

- `architect-career-operating-system/.../auth`
- `architect-career-web/src/shared/api/token.service.ts`

## Interview Discussion

### Why was this approach selected?

JWT access + opaque refresh balances SPA scale and revocation.

### When would you choose another approach?

Prefer cookie-based BFF sessions for higher XSS threat models.

### How would this decision change for 10x / 100x / 1000x users?

At scale, refresh store and token churn need careful indexing/TTL job design.

### Common Principal Architect interview questions

**Q1. Are refresh tokens JWTs?**

No—opaque, hashed at rest.

### Common follow-up questions

- What routes are permitAll?

