# Containerization

## Introduction

Containers package process+deps for reproducible runs; Compose defines multi-service local topologies.

## Problem Statement

“Works on my laptop” JVM/Python version drift burns onboarding time.

## Why ACOS Uses This

ACOS containerizes Postgres/pgAdmin, AI app+Redis+Qdrant. Business/Web apps still typically run on host processes.

## Implementation Overview

ACOS containerizes Postgres/pgAdmin, AI app+Redis+Qdrant. Business/Web apps still typically run on host processes.

## Best Practices

Non-root users eventually; pin digests; healthchecks on AI image; don't commit secrets into images.

## Common Mistakes

- Giant images with build tools.
- Compose hostnames to services you never defined (otel-collector lesson).

## Alternative Approaches

VMs; nix; fully host-native all services.

## Trade-offs

Parity vs Dockerfile maintenance. ACOS partially containerized—by design at portfolio stage.

## References to ACOS modules

- Docker docs chapter 08

## Interview Questions

**Q:** Business Dockerfile?
**A:** Not present.

**Q:** AI base image?
**A:** `python:3.13-slim`.

## Further Reading

- Docker best practices

