# Health Endpoints for Orchestration

## Introduction

Cloud schedulers need HTTP probes. Designing them early avoids rewrite later.

## Problem Statement

Apps that only log “started” cannot be auto-healed safely.

## Why ACOS Uses This

ACOS already separates liveness/readiness ideas on Business Actuator and AI system routes—usable by future K8s without redesigning product APIs.

## Implementation Overview

ACOS already separates liveness/readiness ideas on Business Actuator and AI system routes—usable by future K8s without redesigning product APIs.

## Best Practices

Keep probes auth-light where appropriate; don't make readiness call paid LLMs.

## Common Mistakes

- Expensive readiness checks.
- Securing liveness behind JWT breaking probes.

## Alternative Approaches

TCP socket probes only; exec probes into processes.

## Trade-offs

Slightly more code vs cloud portability. Good investment.

## References to ACOS modules

- Health probes topic in Observability/

## Interview Questions

**Q:** Which AI path for Docker HEALTHCHECK?
**A:** `/api/v1/system/liveness`.

**Q:** Business AI health REST vs Actuator?
**A:** Actuator indicator + JWT-protected integration health API.

## Further Reading

- Kubernetes configure liveness/readiness

