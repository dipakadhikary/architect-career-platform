# GitHub Actions

## Introduction

GitHub Actions runs workflows on push/PR to automate verify/build/scan.

## Problem Statement

Local-only quality does not protect main from broken merges across contributors.

## Why ACOS Uses This

Implemented for AI Platform (`ci.yml`) and AI Contracts (`validate-and-generate.yml`). Business/Web lack workflows—local Maven/npm instead.

## Implementation Overview

Implemented for AI Platform (`ci.yml`) and AI Contracts (`validate-and-generate.yml`). Business/Web lack workflows—local Maven/npm instead.

## Best Practices

Fail closed on critical gates; cache dependencies; upload artifacts explicitly.

## Common Mistakes

- Soft-failing all security jobs forever.
- Assuming Actions exist for every ACOS repo.

## Alternative Approaches

Jenkins; GitLab CI; Buildkite; local hooks only.

## Trade-offs

Native to GitHub vs uneven coverage today.

## References to ACOS modules

- [08-engineering-excellence/DevOps/CI-CD-Architecture.md](../../08-engineering-excellence/DevOps/CI-CD-Architecture.md)

## Interview Questions

**Q:** Which repos have Actions?
**A:** AI Platform and AI Contracts.

**Q:** Contracts publish step?
**A:** Stubbed/commented.

## Further Reading

- docs.github.com/actions

