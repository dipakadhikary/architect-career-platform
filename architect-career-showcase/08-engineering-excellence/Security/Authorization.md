# Authorization

## Business Platform

- Default rule: **any authenticated user** for non-permitAll endpoints  
- JSON 401/403 via `JsonAuthenticationEntryPoint` / `JsonAccessDeniedHandler`  
- No fine-grained method security matrix documented for domain resources beyond ownership patterns in services (user-scoped data access in domain layer)

## Web Platform

- `ProtectedRoute` accepts optional `roles?: ('USER'|'ADMIN')[]`  
- **Route tree does not pass `roles`** — auth presence only  
- Unauthorized page exists for role denials when used  

## AI Platform

- Policy component with `tenant_isolation_enabled` default **false** (allows with reason `tenant_isolation_disabled`)  
- Principal optional on domain AI routes via `get_optional_principal`  

## Future Roadmap

RBAC/ABAC, tenant isolation on by default, Chat Feign + BFF authorization parity.


## Interview Discussion

### Why this approach?

Authenticated monolith with user-scoped queries is enough for current product scope.

### Alternative approaches

OPA/Cedar policies. Overkill early.

### Trade-offs

Role prop unused in Web; weak multi-tenant story.

### Enterprise adoption

Turn on tenant isolation; resource-level ACL audits.

### Scaling considerations

Central policy decision point when services multiply.

### Principal Architect interview questions

**Q1. Are ADMIN routes enforced in Web?**  
Not wired — roles optional and unused in current routes.

**Q2. AI tenant isolation default?**  
Disabled (`tenant_isolation_enabled=false`).
