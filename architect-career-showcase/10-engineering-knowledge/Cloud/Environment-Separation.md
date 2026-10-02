# Environment Separation

## Introduction

Separating local/test/stage/production prevents demo defaults from becoming production outages.

## Problem Statement

One shared “server” with debug on is not an environment strategy.

## Why ACOS Uses This

Business: `local`/`test` profiles (no prod YAML yet). AI: `APP_ENV` including production validation. Web: Vite modes.

## Implementation Overview

Business: `local`/`test` profiles (no prod YAML yet). AI: `APP_ENV` including production validation. Web: Vite modes.

## Best Practices

Fail closed in production; distinct credentials; never point prod AI keys at local compose casually.

## Common Mistakes

- Using production OpenAI keys on laptops without control.
- Identical JWT secrets across envs.

## Alternative Approaches

Single env; ephemeral PR envs only.

## Trade-offs

Safety vs more config matrices to maintain.

## References to ACOS modules

- Environment-Management chapter 08

## Interview Questions

**Q:** Business prod profile file?
**A:** Not present yet.

**Q:** AI production rule example?
**A:** Rejects weak JWT secret and CORS `*`.

## Further Reading

- Twelve-factor + environment promotion guides

