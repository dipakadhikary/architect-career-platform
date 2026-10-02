# Vite

## Introduction

Vite provides fast ESM dev server and Rollup-based production builds.

## Problem Statement

Older Webpack-centric CRA tooling is slower to start and heavier to configure for modern React.

## Why ACOS Uses This

ACOS uses Vite 6 on port 5173 with `/api` proxy to Business 8080; manualChunks for vendor splitting; PWA plugin.

## Implementation Overview

ACOS uses Vite 6 on port 5173 with `/api` proxy to Business 8080; manualChunks for vendor splitting; PWA plugin.

## Best Practices

Keep env vars prefixed `VITE_`; validate with Zod; don't put secrets in Vite env.

## Common Mistakes

- Proxying to AI Platform from Vite.
- Giant single chunk without manualSplits.

## Alternative Approaches

Webpack; Parcel; Rsbuild; Next bundler.

## Trade-offs

DX speed vs plugin ecosystem differences. ACOS is Vite-native.

## References to ACOS modules

- [05-web-platform/Build-and-Deployment.md](../../05-web-platform/Build-and-Deployment.md)

## Interview Questions

**Q:** Default proxy target?
**A:** `http://localhost:8080`.

**Q:** Dockerfile?
**A:** Not implemented for Web.

## Further Reading

- vitejs.dev

