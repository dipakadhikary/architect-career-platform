# Authentication

## Business Platform

| Mechanism | Detail |
| --- | --- |
| Access token | JWT (jjwt 0.12.6), HMAC, issuer/TTL via `ACOS_JWT_*` |
| Refresh token | Opaque; SHA-256 hashed in DB; rotation on refresh; logout revokes |
| Password | BCrypt; min 12 / max 72; complexity rules |
| Endpoints | `POST /api/v1/auth/register|login|refresh|logout`, `GET /me` |
| Filter | `JwtAuthenticationFilter` — stateless session |

PermitAll: register, login, refresh, OpenAPI UI, Actuator health/info.

## Web Platform

- Stores access/refresh tokens in **localStorage** (`acos.accessToken`, etc.)  
- Axios 401 → single-flight refresh → retry once → `acos:session-expired` on failure  
- `GuestRoute` / `ProtectedRoute` for navigation  

## AI Platform

`Authentication` service supports:

- JWT (`AUTH_JWT_*`, python-jose)  
- API keys (`AUTH_API_KEYS`, `X-API-Key`)  
- Internal service tokens (`X-Internal-Service`)  

If **all** auth modes disabled → principal `None` (open). Production `validate_for_runtime()` requires at least one auth mode and rejects weak JWT secret.


## Interview Discussion

### Why this approach?

Classic access JWT + rotating opaque refresh balances API simplicity and revocation.

### Alternative approaches

Session cookies only; OAuth2 Authorization Server. Heavier for portfolio.

### Trade-offs

localStorage vs httpOnly; AI auth optional by default.

### Enterprise adoption

OIDC provider; device auth; step-up for sensitive AI actions.

### Scaling considerations

Token revocation list / refresh family detection.

### Principal Architect interview questions

**Q1. Default access TTL?**  
`ACOS_JWT_ACCESS_TTL` default `15m`.

**Q2. Is AI JWT required in development?**  
No — flags default off unless production validation applies.
