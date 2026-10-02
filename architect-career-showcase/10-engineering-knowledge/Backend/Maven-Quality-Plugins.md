# Maven Quality Plugins

## Introduction

Maven quality plugins automate formatting, static analysis, and coverage checks in the build lifecycle.

## Problem Statement

Style and defect debates in PR review waste time without automation.

## Why ACOS Uses This

ACOS Business `mvn verify` runs Spotless, Checkstyle, PMD, SpotBugs, and JaCoCo as mechanical quality gates.

## Implementation Overview

Spotless google-java-format; Checkstyle 10.x (including OTel import ban); PMD+CPD; SpotBugs; JaCoCo with **0.00** minimum (tool present, bar not enforcing).

## Best Practices

Run verify before push; treat violations as build breaks; ratchet formatting carefully on legacy code.

## Common Mistakes

- Claiming coverage excellence while threshold is 0.
- Disabling SpotBugs excludes too broadly.

## Alternative Approaches

Gradle + Spotless; Checkstyle-only; SonarQube server.

## Trade-offs

Fast local gates vs no remote CI yet for Business. Uneven vs AI's GitHub Actions.

## References to ACOS modules

- [08-engineering-excellence/DevOps/Build-Pipeline.md](../../08-engineering-excellence/DevOps/Build-Pipeline.md)

## Interview Questions

**Q:** Does JaCoCo fail builds on low coverage?
**A:** Not meaningfully—minimum is 0.00.

**Q:** Why ban OpenTelemetry imports?
**A:** Avoid half-wired tracing until end-to-end plan exists.

## Further Reading

- Each plugin's official docs

