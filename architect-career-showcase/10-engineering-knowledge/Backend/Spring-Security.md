# Spring Security

## Introduction

Spring Security provides authentication, authorization, and HTTP security filter chains for servlet apps.

## Problem Statement

Homegrown servlet filters miss edge cases (session fixation, header defaults, method security).

## Why ACOS Uses This

ACOS uses a stateless JWT filter chain: permitAll for register/login/refresh and health/docs; authenticated everything else; CSRF disabled for token APIs; JSON 401/403 handlers.

## Implementation Overview

`JwtAuthenticationFilter` + `JwtTokenProvider` (jjwt); BCrypt passwords; opaque refresh tokens hashed in DB.

## Best Practices

- Keep security matcher lists explicit.
- Never log tokens.
- Prefer 401/403 problem JSON consistent with API style.

## Common Mistakes

- CSRF disabled without understanding SPA token model.
- Overusing `permitAll` for new controllers.

## Alternative Approaches

Apache Shiro; servlet-only JWT; OAuth2 Resource Server with external IdP.

## Trade-offs

Powerful defaults vs complexity. ACOS uses Resource-server-like JWT validation without full OAuth2 AS.

## References to ACOS modules

- [04-business-platform](../../04-business-platform/) Security docs
- [08-engineering-excellence/Security/Authentication.md](../../08-engineering-excellence/Security/Authentication.md)

## Interview Questions

**Q:** Why stateless?
**A:** Horizontal-friendly JWT access; refresh revocation via DB.

**Q:** Is CORS fully customized?
**A:** Defaults today—explicit allowlist is a known hardening item.

## Further Reading

- Spring Security reference

