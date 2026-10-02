# SPA Token Storage

## Introduction

Browser SPAs must store credentials somehow—localStorage, sessionStorage, or httpOnly cookies—each with XSS/CSRF trade-offs.

## Problem Statement

There is no perfect browser secret storage; architecture must pick a threat preference.

## Why ACOS Uses This

ACOS Web stores access/refresh tokens in localStorage for implementation simplicity; markdown is sanitized; no CSP headers in-app yet.

## Implementation Overview

ACOS Web stores access/refresh tokens in localStorage for implementation simplicity; markdown is sanitized; no CSP headers in-app yet.

## Best Practices

Invest in XSS prevention; plan httpOnly cookie migration for higher assurance; never store LLM provider keys in SPA.

## Common Mistakes

- Ignoring XSS because “we use JWT”.
- Putting refresh tokens in world-readable logs.

## Alternative Approaches

BFF cookie session; Trusted Types; token in memory only (lost on refresh).

## Trade-offs

Simple DX vs XSS residual risk—documented in threat model.

## References to ACOS modules

- [05-web-platform/Authentication.md](../../05-web-platform/Authentication.md)
- [08-engineering-excellence/Security/Threat-Model.md](../../08-engineering-excellence/Security/Threat-Model.md)

## Interview Questions

**Q:** Top residual risk?
**A:** XSS stealing localStorage tokens.

**Q:** Mitigations today?
**A:** sanitize markdown, short access TTL, refresh rotation.

## Further Reading

- OWASP HTML5 Security — local storage

