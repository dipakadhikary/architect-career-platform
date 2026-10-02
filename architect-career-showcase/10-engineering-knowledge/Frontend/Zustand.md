# Zustand

## Introduction

Zustand is a minimal global store for client state without Redux boilerplate.

## Problem Statement

Prop-drilling auth user/session across layouts is brittle.

## Why ACOS Uses This

ACOS auth store hydrates from localStorage tokens/user; works with ProtectedRoute/GuestRoute.

## Implementation Overview

ACOS auth store hydrates from localStorage tokens/user; works with ProtectedRoute/GuestRoute.

## Best Practices

Keep stores small; don't mirror entire server databases in Zustand.

## Common Mistakes

- Duplicating TanStack Query data into Zustand.
- Forgetting hydration race on first paint.

## Alternative Approaches

Redux Toolkit; Jotai; Context-only.

## Trade-offs

Simplicity vs time-travel/debug middleware ecosystem. Good fit for auth session.

## References to ACOS modules

- Web `auth.store` + RootLayout hydration

## Interview Questions

**Q:** Persist strategy?
**A:** Token service writes localStorage; store rehydrates.

**Q:** Roles in routes?
**A:** ProtectedRoute supports roles but routes don't pass them yet.

## Further Reading

- zustand docs

