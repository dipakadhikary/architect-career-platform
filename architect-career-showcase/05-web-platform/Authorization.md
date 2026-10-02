# Authorization

## Model

Authorization is primarily **backend-enforced** (owner-scoped APIs). The Web reflects roles on the user object and can gate routes.

## Route-level authorization

`ProtectedRoute` accepts optional `roles?: Array<'USER' | 'ADMIN'>`.

- If roles specified and user lacks all of them → navigate to `/unauthorized`
- Product routes today typically omit `roles`, requiring authentication only

Pages `/unauthorized` and `/forbidden` exist for UX.

## Role management

Users carry `roles` from Business (`USER`, `ADMIN`). Registration defaults to USER on the server. Fine-grained admin screens are not a major implemented surface in the Web app.

## Resource authorization

Hidden/disabled actions still rely on API 403/404. Do not treat UI hiding as security.

## AI capability gating

`VITE_AI_PLATFORM_ENABLED` plus availability/health helpers disable AI actions when the platform is off or unhealthy — product enablement, not RBAC.

## Interview Discussion

### Why this architecture?

Keep authorization truth on the server; use the client for UX gates only.

### Alternative approaches

CASL/Abilities; policy-as-code in the SPA. Unnecessary until admin surfaces expand.

### Trade-offs

Role prop on routes is underused — easy to forget when adding ADMIN features.

### Scaling considerations

Central `can(user, action, resource)` helper when admin UX grows.

### How would this evolve?

Permission claims in JWT; route `handle.meta.roles` conventions.

### Common Frontend Architect interview questions

**Q1. Can a user edit another user’s notes by crafting a URL?**  
API must reject — Web cannot be trusted.

**Q2. Is ADMIN required for `/ai`?**  
No — authenticated user + feature flag.
