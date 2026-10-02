# Twelve-Factor Config

## Introduction

The twelve-factor methodology stores config in the environment so the same build promotes across stages.

## Problem Statement

Baking secrets into images or git branches creates leaks and snowflake deploys.

## Why ACOS Uses This

ACOS Business/AI/Web use env overrides (`ACOS_*`, `AUTH_*`, `VITE_*`). AI validates production invariants at boot.

## Implementation Overview

ACOS Business/AI/Web use env overrides (`ACOS_*`, `AUTH_*`, `VITE_*`). AI validates production invariants at boot.

## Best Practices

No secrets in images; distinct env per stage; document examples (AI `.env.example` still missing—known gap).

## Common Mistakes

- Default JWT secrets in prod.
- Vite secrets assuming privacy.

## Alternative Approaches

Spring Cloud Config server; sealed secrets only; config in DB.

## Trade-offs

Simple promotion vs secret sprawl in env vars—graduate to a vault.

## References to ACOS modules

- Environment/Configuration chapters 08

## Interview Questions

**Q:** Where do LLM keys live?
**A:** AI environment SecretStr fields.

**Q:** Web env privacy?
**A:** Anything `VITE_` is exposed to the browser—never put server secrets there.

## Further Reading

- 12factor.net

