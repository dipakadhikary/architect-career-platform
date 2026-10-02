# Quality Gates

## Introduction

Quality gates are automated checks that must pass before merge/release.

## Problem Statement

Human-only review misses formatting, CVE, and contract breakage.

## Why ACOS Uses This

Business `mvn verify` plugins; Web lint/test/build scripts; AI ruff/black/pytest 55%; Contracts `mvn verify` + artifact upload.

## Implementation Overview

Business `mvn verify` plugins; Web lint/test/build scripts; AI ruff/black/pytest 55%; Contracts `mvn verify` + artifact upload.

## Best Practices

Make gates meaningful (coverage >0); keep fast on PR path; heavier scans nightly if needed.

## Common Mistakes

- Theater gates (0% coverage).
- 45-minute PR pipelines without caching.

## Alternative Approaches

Only SonarQube; only pair review; deploy-and-pray.

## Trade-offs

Consistency vs local friction. ACOS strength is intent; maturity varies by repo.

## References to ACOS modules

- Build-Pipeline chapter 08

## Interview Questions

**Q:** AI coverage gate?
**A:** 55%.

**Q:** Business coverage gate?
**A:** JaCoCo min 0.00.

## Further Reading

- Continuous Integration (Fowler)

