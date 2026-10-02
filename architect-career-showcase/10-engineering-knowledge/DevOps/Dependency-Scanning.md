# Dependency & Image Scanning

## Introduction

Scanners detect known CVEs and insecure coding patterns in dependencies and images.

## Problem Statement

AI stacks pull many transitive packages; ignoring CVEs is reckless.

## Why ACOS Uses This

AI CI runs pip-audit, bandit, and Trivy image scan—often `continue-on-error: true` today.

## Implementation Overview

AI CI runs pip-audit, bandit, and Trivy image scan—often `continue-on-error: true` today.

## Best Practices

Track soft-to-hard fail transition; exclude tests thoughtfully; rebuild images on base CVE.

## Common Mistakes

- Ignoring soft fails for months.
- Scanning once a year.

## Alternative Approaches

Snyk; Dependabot only; no scanning.

## Trade-offs

Signal vs noise. Soft fail is interim honesty, not a destination.

## References to ACOS modules

- AI Platform ci.yml
- Engineering Excellence CI docs

## Interview Questions

**Q:** Trivy severity filter?
**A:** CRITICAL/HIGH in workflow.

**Q:** Hard gate?
**A:** Not yet—soft fail.

## Further Reading

- OWASP Dependency-Check concepts; Trivy docs

