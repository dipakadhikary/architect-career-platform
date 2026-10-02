# Refresh Token Rotation

## Introduction

Rotation issues a new refresh token on each use and invalidates the previous one, detecting theft when a reused token appears.

## Problem Statement

Long-lived reusable refresh tokens are high-value stealable credentials.

## Why ACOS Uses This

ACOS stores opaque refresh tokens hashed (SHA-256) in Postgres; refresh endpoint rotates; logout revokes.

## Implementation Overview

ACOS stores opaque refresh tokens hashed (SHA-256) in Postgres; refresh endpoint rotates; logout revokes.

## Best Practices

Hash at rest; rotate always; family detection as future hardening.

## Common Mistakes

- Storing refresh tokens in plaintext.
- Returning refresh tokens to logs.

## Alternative Approaches

Sliding server sessions; rotating refresh without reuse detection.

## Trade-offs

Better theft signal vs more DB writes. Worth it for ACOS auth.

## References to ACOS modules

- Auth module migrations V3/V4 era
- Engineering Excellence Authentication doc

## Interview Questions

**Q:** Why opaque refresh vs JWT refresh?
**A:** Server-side revoke/rotate without large blocklists.

**Q:** Where stored on client?
**A:** localStorage today—see SPA token storage topic.

## Further Reading

- OAuth 2.1 refresh rotation guidance

