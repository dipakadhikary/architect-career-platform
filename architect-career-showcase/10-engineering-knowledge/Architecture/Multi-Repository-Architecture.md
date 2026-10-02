# Multi-Repository Architecture

## Introduction

ACOS splits Web, Business, AI, and Contracts into distinct Git repositories so each can use native toolchains and evolve on different cadences.

## Problem Statement

A monorepo can simplify atomic changes but forces one CI/tooling story across Java, Python, and TypeScript and couples release noise.

## Why ACOS Uses This

ACOS chose four primary repos to match stacks: Maven/Java, npm/React, Python/FastAPI, and YAML contracts with generators.

## Implementation Overview

Changes that cross AI HTTP shapes require a contracts PR then consumer sync (especially AI Python vendoring). Business and Web remain independently buildable for CRUD demos without AI.

## Best Practices

- Define ownership of the boundary (contracts for AI).
- Document sync steps (Engineering Excellence runbooks).
- Prefer contract changes before dual consumer edits.

## Common Mistakes

- Editing Feign DTOs and Pydantic models independently without contracts.
- Assuming one CI green means the ecosystem is green.

## Alternative Approaches

Monorepo (Nx/Bazel); polyrepo with shared packages published to registries.

## Trade-offs

Clear stack boundaries vs multi-PR choreography. ACOS still vendors Python instead of publishing to PyPI.

## References to ACOS modules

- [01-platform-overview/Repository-Overview.md](../../01-platform-overview/Repository-Overview.md)
- [07-ai-contracts/Consumer-Integration.md](../../07-ai-contracts/Consumer-Integration.md)

## Interview Questions

**Q:** Why not put OpenAPI inside the AI repo only?
**A:** Business and future clients need a neutral SoT; generators target multiple languages.

## Further Reading

- ThoughtWorks polyrepo vs monorepo debates
- Semantic versioning across repos
