# Progressive Web App (PWA)

## Introduction

PWAs add installability, service workers, and offline resilience to web apps.

## Problem Statement

Career tools used on the go suffer when transient offline kills the UI shell.

## Why ACOS Uses This

ACOS uses `vite-plugin-pwa`: prompt registration, Workbox CacheFirst fonts, StaleWhileRevalidate images, **NetworkOnly** for `/api/`, offline route/banner.

## Implementation Overview

ACOS uses `vite-plugin-pwa`: prompt registration, Workbox CacheFirst fonts, StaleWhileRevalidate images, **NetworkOnly** for `/api/`, offline route/banner.

## Best Practices

Never cache authenticated API responses in SW; keep update prompt UX clear.

## Common Mistakes

- Caching `/api` GET with tokens.
- Aggressive precache of everything bloating SW.

## Alternative Approaches

Native apps; no SW; App Capacitator wrappers.

## Trade-offs

Offline shell vs complexity/debug cost. API NetworkOnly is the critical safety choice.

## References to ACOS modules

- [05-web-platform](../../05-web-platform/) Performance / Build docs

## Interview Questions

**Q:** Are API calls served from cache?
**A:** No—NetworkOnly.

**Q:** Update model?
**A:** Prompt/`needRefresh` with delayed auto-update behavior in app code.

## Further Reading

- web.dev/progressive-web-apps

