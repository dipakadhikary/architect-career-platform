# Health Probes (Liveness & Readiness)

## Introduction

Orchestrators use liveness (restart) vs readiness (accept traffic) probes to manage process lifecycle.

## Problem Statement

Killing a process for a downstream blip (AI down) can worsen outages if misclassified.

## Why ACOS Uses This

Business Actuator readiness includes `db` + `aiPlatform`; liveness separate. AI has `/system/liveness` and `/readiness` plus contract `/ai/health`.

## Implementation Overview

Business Actuator readiness includes `db` + `aiPlatform`; liveness separate. AI has `/system/liveness` and `/readiness` plus contract `/ai/health`.

## Best Practices

Don't block liveness on dependency health; put dependencies in readiness; keep endpoints cheap.

## Common Mistakes

- Readiness always 200.
- Liveness failing when Qdrant down (causes restart loops).

## Alternative Approaches

Single `/health` for everything; no probes.

## Trade-offs

Better orchestration vs more endpoints to design. ACOS is probe-ready even before K8s.

## References to ACOS modules

- Deployment/Docker chapters 08
- AI Dockerfile HEALTHCHECK

## Interview Questions

**Q:** AI container HEALTHCHECK?
**A:** curls liveness.

**Q:** Should DB failure fail liveness?
**A:** Prefer readiness—so orchestrator stops traffic without thrashing restarts.

## Further Reading

- Kubernetes probe semantics

