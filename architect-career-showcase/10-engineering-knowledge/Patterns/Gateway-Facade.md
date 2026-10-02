# Gateway / Facade

## Introduction

Facades/gateways present a simplified API over a messy subsystem—here, AI HTTP + resilience + mapping.

## Problem Statement

Domain services shouldn't know Feign annotations, header propagation, or CB details.

## Why ACOS Uses This

Business `*AiGateway` classes orchestrate clients/DTOs; controllers/application services call facades.

## Implementation Overview

Business `*AiGateway` classes orchestrate clients/DTOs; controllers/application services call facades.

## Best Practices

Keep facades stable; hide DTO churn; centralize metrics.

## Common Mistakes

- Fat facades owning domain rules.
- One mega-God gateway for all domains without structure.

## Alternative Approaches

Direct Feign in controllers; messaging instead of sync facade.

## Trade-offs

Indirection vs clarity at ACL. Strong ACOS teaching pattern.

## References to ACOS modules

- Integration gateways in Business Platform

## Interview Questions

**Q:** Gateway vs ACL?
**A:** Gateway/facade is a common ACL implementation style.

**Q:** Example?
**A:** `KnowledgeAiGateway`, `CareerAiGateway`, etc.

## Further Reading

- GoF Facade; microservices API Gateway (related but edge-network)

