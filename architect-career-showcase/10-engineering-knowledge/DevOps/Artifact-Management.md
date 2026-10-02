# Artifact Management

## Introduction

Build artifacts (jars, zips, images) are the transferable outputs of CI—not source trees of generated code.

## Problem Statement

Committing generated SDKs creates noisy diffs and merge hell.

## Why ACOS Uses This

Contracts produce `career-ai-*-sdk` artifacts uploaded to Actions; AI vendors Python into `third_party`; Packages deploy stubbed.

## Implementation Overview

Contracts produce `career-ai-*-sdk` artifacts uploaded to Actions; AI vendors Python into `third_party`; Packages deploy stubbed.

## Best Practices

Don't commit `target/generated`; pin versions when publishing starts; verify checksums later.

## Common Mistakes

- Hand-editing generated artifacts.
- Emailing zip files as the forever process.

## Alternative Approaches

Commit generated sources; monorepo workspace packages; Maven Central from day one.

## Trade-offs

Clean git vs manual consumer sync—until registries enable.

## References to ACOS modules

- Artifact Publishing chapter 07

## Interview Questions

**Q:** Are SDKs in git?
**A:** No—generated to `target/`.

**Q:** How AI consumes?
**A:** File-vendored package.

## Further Reading

- Maven/npm registry concepts

