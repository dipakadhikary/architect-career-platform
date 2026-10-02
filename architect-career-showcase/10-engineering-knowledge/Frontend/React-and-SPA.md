# React & Single-Page Applications

## Introduction

React builds component UIs; as a SPA, ACOS Web loads once and navigates client-side while calling Business APIs.

## Problem Statement

Multi-page server renders slow career workflows that feel app-like; mixing AI keys into the browser is unsafe.

## Why ACOS Uses This

React 19 + React Router 7 feature routes; lazy-loaded pages; ErrorBoundary; Web talks only to Business (`/api` proxy in dev).

## Implementation Overview

React 19 + React Router 7 feature routes; lazy-loaded pages; ErrorBoundary; Web talks only to Business (`/api` proxy in dev).

## Best Practices

Keep features sliced; suspense lazy routes; never import AI Platform URLs for data plane.

## Common Mistakes

- Fetching in components without a server-state library.
- Storing provider API keys in the SPA.

## Alternative Approaches

Next.js SSR/RSC; Remix; plain MPA.

## Trade-offs

Snappy UX vs SEO/TTFB trade-offs. ACOS is an authenticated app—SPA fit is good.

## References to ACOS modules

- [05-web-platform](../../05-web-platform/)

## Interview Questions

**Q:** Why not call AI from React?
**A:** Trust boundary—keys and guardrails stay server-side.

**Q:** Lazy loading purpose?
**A:** Smaller initial bundle; route-based code splitting.

## Further Reading

- react.dev

