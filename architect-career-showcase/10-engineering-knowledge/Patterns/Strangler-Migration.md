# Strangler Fig / Incremental Adoption

## Introduction

The strangler pattern incrementally replaces an old path with a new one until the old can be removed.

## Problem Statement

Big-bang swaps (all Feign → generated SDK; all sync → Kafka) tend to fail.

## Why ACOS Uses This

ACOS stranglers in progress: hand Feign→generated Java SDK; AsyncAPI specs→future broker; correlation→future OTel; local gates→GitHub Actions for Business/Web.

## Implementation Overview

ACOS stranglers in progress: hand Feign→generated Java SDK; AsyncAPI specs→future broker; correlation→future OTel; local gates→GitHub Actions for Business/Web.

## Best Practices

Ship seam first; run dual paths; delete old path with metrics.

## Common Mistakes

- Eternal dual running without deletion plan.
- Strangling without contracts/tests.

## Alternative Approaches

Rewrite from scratch; freeze legacy forever.

## Trade-offs

Safer migration vs temporary complexity—explicit in ACOS roadmap.

## References to ACOS modules

- Lessons Learned + Roadmap chapter 08
- Consumer integration chapter 07

## Interview Questions

**Q:** Example seam?
**A:** AiPlatformInvoker remains stable while client implementation swaps.

**Q:** Event strangler?
**A:** Spec channels first, one broker-backed event later.

## Further Reading

- Martin Fowler — Strangler Fig Application

