# Feature Flags

## Introduction

Feature flags gate functionality without redeploying binaries—critical for risky dependencies like AI.

## Problem Statement

Hard-wiring AI on makes local demos and incidents brittle.

## Why ACOS Uses This

Business `ai.platform.enabled` defaults false; Web `VITE_AI_PLATFORM_ENABLED`; AI has many enterprise toggles (guardrails, cache, LangFuse, auth modes).

## Implementation Overview

Business `ai.platform.enabled` defaults false; Web `VITE_AI_PLATFORM_ENABLED`; AI has many enterprise toggles (guardrails, cache, LangFuse, auth modes).

## Best Practices

Default safe; keep flag checks in ACL/UI; document flag matrix in runbooks.

## Common Mistakes

- Long-lived flags without cleanup.
- Splitting flag state across repos inconsistently during demos.

## Alternative Approaches

LaunchDarkly; ConfigMaps only; branch deploys.

## Trade-offs

Operational safety vs flag sprawl. ACOS AI surface has many knobs—governance needed.

## References to ACOS modules

- Configuration management in Engineering Excellence
- Business AiPlatformProperties

## Interview Questions

**Q:** Fastest AI incident mitigation?
**A:** Turn `ai.platform.enabled` off.

**Q:** Flag in browser sufficient?
**A:** No—server flag is authoritative.

## Further Reading

- Feature Toggles (Fowler)

