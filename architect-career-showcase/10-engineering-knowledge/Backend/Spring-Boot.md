# Spring Boot

## Introduction

Spring Boot is an opinionated runtime for building production-capable Spring applications with embedded servers, autoconfiguration, and actuator endpoints.

## Problem Statement

Hand-assembled Spring XML/MVC stacks slow delivery and fragment operational conventions.

## Why ACOS Uses This

ACOS Business Platform is a Spring Boot 3.5 application (Java 21) exposing REST on 8080 with Actuator health/metrics.

## Implementation Overview

Fat jar / `spring-boot:run`; profiles `local` and `test`; BuildProperties for OpenAPI versioning; modular packages under `com.acos.*`.

## Best Practices

- Prefer configuration properties types over scattered `@Value`.
- Keep Actuator exposure intentional.
- Fail fast on bad secrets in non-local profiles (pattern AI already uses).

## Common Mistakes

- Enabling every Actuator endpoint publicly.
- Business logic in `@SpringBootApplication` class.

## Alternative Approaches

Micronaut, Quarkus, plain Jakarta EE, .NET minimal APIs.

## Trade-offs

Huge ecosystem vs memory footprint and reflection magic. ACOS optimizes for enterprise familiarity.

## References to ACOS modules

- `architect-career-operating-system` POM + `application.yml`
- [04-business-platform](../../04-business-platform/)

## Interview Questions

**Q:** Why Boot 3.x?
**A:** Jakarta EE namespace, Java 21, modern Security/Observability starters.

**Q:** Embedded server?
**A:** Default Tomcat via Boot starter web.

## Further Reading

- spring.io/projects/spring-boot

