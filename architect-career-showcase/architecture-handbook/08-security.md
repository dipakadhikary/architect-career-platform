# Security

## Authentication model

- Stateless Spring Security (`SessionCreationPolicy.STATELESS`)
- CSRF disabled for the API
- JWT access tokens validated by `JwtAuthenticationFilter`
- Refresh tokens stored as SHA-256 hashes and rotated on refresh
- Passwords hashed with `BCryptPasswordEncoder`
- Login compares against a dummy hash when the user is missing to reduce timing-based enumeration

## Filter chain permit list

Unauthenticated access is allowed for:

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- SpringDoc paths (`/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`)
- Actuator `health` and `info`

All other requests require authentication.

## Principal

Authenticated APIs receive:

```java
@AuthenticationPrincipal AcosUserDetails principal
```

and pass `principal.getId()` into services as the owner/actor id.

## Authorization model in practice

Primary authorization is **ownership isolation**:

- Entities store `owner_id`
- Repositories query by owner
- Services reject cross-owner access via not-found / business exceptions

`@EnableMethodSecurity` is enabled, but method-level `@PreAuthorize` is not the primary authorization pattern in current feature code.

## Token configuration

JWT settings are bound from `acos.jwt.*` (`JwtProperties`): secret, issuer, access/refresh TTLs.

## Rules reflected in code

1. Protect domain APIs by default; only explicitly permit public auth/docs/health endpoints.
2. Never trust client-supplied owner ids for authorization; use the authenticated principal.
3. Keep security error responses in the `ApiResponse` envelope.

## Evidence

- `src/main/java/com/acos/auth/config/SecurityConfiguration.java`
- `src/main/java/com/acos/auth/security/JwtAuthenticationFilter.java`
- `src/main/java/com/acos/auth/token/JwtTokenProvider.java`
- `src/main/java/com/acos/auth/token/TokenServiceImpl.java`
- `src/main/java/com/acos/auth/config/PasswordEncoderConfiguration.java`
- `src/main/java/com/acos/auth/service/AuthServiceImpl.java`
