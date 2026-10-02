# Trunk-Based Development

## Introduction

Short-lived branches merging frequently to `main` reduce integration risk versus long GitFlow release branches.

## Problem Statement

Week-long feature branches diverge contracts and consumers painfully.

## Why ACOS Uses This

ACOS practical model: PR → main/master with CI where present; no formal GitFlow docs.

## Implementation Overview

ACOS practical model: PR → main/master with CI where present; no formal GitFlow docs.

## Best Practices

Keep PRs small; protect main; prefer feature flags for incomplete AI UX.

## Common Mistakes

- Days-long branches editing OpenAPI and all consumers simultaneously without contracts-first order.

## Alternative Approaches

GitFlow; environment branches; pair programming without VCS.

## Trade-offs

Speed vs needing solid CI—Business/Web still catching up.

## References to ACOS modules

- Branching strategy chapter 08

## Interview Questions

**Q:** Release branches?
**A:** Not required at current stage.

**Q:** Contracts order?
**A:** Spec PR before multi-repo consumer PRs.

## Further Reading

- trunkbaseddevelopment.com

