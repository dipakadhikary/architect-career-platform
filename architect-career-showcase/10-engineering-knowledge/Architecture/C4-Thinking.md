# C4 Thinking

## Introduction

C4 (Context, Container, Component, Code) is a zoom-lens for architecture communication—from people/systems down to classes.

## Problem Statement

A single diagram either hides critical boundaries or overwhelms with class detail.

## Why ACOS Uses This

ACOS showcase Chapter 02 uses C4-style views: ecosystem context (Web/Business/AI/Contracts), containers (JVM, SPA, FastAPI, Postgres, Redis, Qdrant), and component breakdowns per platform.

## Implementation Overview

Interview and onboarding flows should start at Context (“browser never calls AI”), then Containers (ports 5173/8080/8090), then Components (Feign invoker, enterprise pipeline).

## Best Practices

- One message per diagram.
- Keep Code-level diagrams rare and local.

## Common Mistakes

- Mixing deployment nodes and domain entities on one slide.
- Drawing Kafka on Context when AsyncAPI is spec-only.

## Alternative Approaches

UML-only; Freeform whiteboard without zoom levels.

## Trade-offs

Shared vocabulary vs maintenance of diagrams. ACOS treats Mermaid in docs as living sketches tied to code truth.

## References to ACOS modules

- [02-system-design](../../02-system-design/)
- Showcase root README HLA mermaid

## Interview Questions

**Q:** What is a container in C4 for ACOS Web?
**A:** The React SPA (browser-hosted) talking to Business over HTTP—not each npm package.

## Further Reading

- c4model.com — Simon Brown
