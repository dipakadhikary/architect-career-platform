# Semantic Versioning

## Introduction

SemVer (MAJOR.MINOR.PATCH) communicates breaking vs additive changes to consumers.

## Problem Statement

Undeclared breaking OpenAPI changes strand Java and Python clients.

## Why ACOS Uses This

Contracts package `1.0.0` with documented compatibility table; URL `/api/v1`; Business/Web versions independent early numbers.

## Implementation Overview

Contracts package `1.0.0` with documented compatibility table; URL `/api/v1`; Business/Web versions independent early numbers.

## Best Practices

Bump MAJOR for breaking schema; changelog everything user-visible; deprecate first.

## Common Mistakes

- Silent required-field adds.
- Using versions as marketing vanity only.

## Alternative Approaches

CalVer; commit SHA only; date URLs.

## Trade-offs

Clear consumer signals vs discipline cost—mandatory for contracts repo.

## References to ACOS modules

- [07-ai-contracts/Versioning-Strategy.md](../../07-ai-contracts/Versioning-Strategy.md)

## Interview Questions

**Q:** Additive optional field?
**A:** MINOR.

**Q:** Rename JSON property?
**A:** MAJOR.

## Further Reading

- semver.org

