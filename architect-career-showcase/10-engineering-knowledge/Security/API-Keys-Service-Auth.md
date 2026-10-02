# API Keys & Service Authentication

## Introduction

Service-to-service calls often use API keys or internal tokens complementary to user JWTs.

## Problem Statement

User JWT alone may be wrong for machine callers; AI may need a service credential from Business.

## Why ACOS Uses This

Business can send `X-API-Key` / Authorization to AI; AI supports API key lists and `X-Internal-Service` tokens; production validation requires some auth mode.

## Implementation Overview

Business can send `X-API-Key` / Authorization to AI; AI supports API key lists and `X-Internal-Service` tokens; production validation requires some auth mode.

## Best Practices

Rotate keys; don't embed in mobile/SPA; prefer workload identity long-term.

## Common Mistakes

- Shipping demo keys in git.
- AI open mode in prod compose.

## Alternative Approaches

mTLS; OAuth2 client credentials; AWS IAM SigV4.

## Trade-offs

Easy demos vs key distribution pain. Interim for ACOS.

## References to ACOS modules

- AI authentication module
- Business Feign header config

## Interview Questions

**Q:** Can AI run open?
**A:** Yes if all auth flags false—unsafe for prod.

**Q:** Production guard?
**A:** `validate_for_runtime()` requires auth configuration.

## Further Reading

- API key management best practices

