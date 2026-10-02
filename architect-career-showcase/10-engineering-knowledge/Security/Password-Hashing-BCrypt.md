# Password Hashing (BCrypt)

## Introduction

BCrypt is an adaptive password hash with salt designed to be slow for attackers.

## Problem Statement

MD5/SHA password storage is broken under offline attacks.

## Why ACOS Uses This

ACOS hashes passwords with BCrypt and enforces complexity (length 12–72, character classes).

## Implementation Overview

ACOS hashes passwords with BCrypt and enforces complexity (length 12–72, character classes).

## Best Practices

Never log passwords; keep complexity server-side; prefer BCrypt/Argon2 over custom.

## Common Mistakes

- Truncating passwords silently.
- Building homemade crypto.

## Alternative Approaches

Argon2id; scrypt; PBKDF2.

## Trade-offs

BCrypt widely supported vs Argon2 memory hardness. Acceptable enterprise default.

## References to ACOS modules

- PasswordValidator + Security configuration

## Interview Questions

**Q:** Max length 72?
**A:** BCrypt truncation concerns—cap explicitly.

**Q:** Salt?
**A:** Handled by BCrypt implementation.

## Further Reading

- OWASP Password Storage Cheat Sheet

