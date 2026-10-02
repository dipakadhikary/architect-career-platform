# JSON Web Tokens (JWT)

## Introduction

JWTs are compact, signed tokens carrying claims (subject, issuer, expiry) for stateless authentication.

## Problem Statement

Server sessions alone complicate horizontal scaling for access checks on every API.

## Why ACOS Uses This

ACOS Business issues HMAC JWTs (jjwt) as access tokens (default TTL 15m). AI can optionally validate JWTs via python-jose when enabled.

## Implementation Overview

ACOS Business issues HMAC JWTs (jjwt) as access tokens (default TTL 15m). AI can optionally validate JWTs via python-jose when enabled.

## Best Practices

Short TTL; strong secret; validate iss/exp; don't put sensitive PII in claims.

## Common Mistakes

- `alg=none` acceptance.
- Long-lived access tokens without refresh design.
- Secret `change-me` in production.

## Alternative Approaches

Opaque access tokens only; PASETO; mTLS instead of bearer.

## Trade-offs

Stateless validation vs revocation difficulty—hence refresh opacity in DB.

## References to ACOS modules

- Business JwtTokenProvider
- AI AUTH_JWT_* settings

## Interview Questions

**Q:** Algorithm?
**A:** HMAC (HS256-class) with shared secret.

**Q:** Default access TTL?
**A:** 15 minutes.

## Further Reading

- RFC 7519
- jwt.io introduction (conceptual)

