# Performance Optimization

## Lazy loading & code splitting

- Route-level `React.lazy` via `lazyNamed` / `withSuspense`
- Vite `manualChunks`: `mui`, `query`, `charts`, `markdown`, `router`, `vendor`
- Prefetch helper module for warmer navigations

## Memoization

Selective `useMemo`/`useCallback` on heavy list column defs and filtered rows (knowledge/learning/portfolio). Not applied blindly everywhere.

## Bundle optimization

Production `tsc -b && vite build`; chunk size warning limit 900kb; tree-shaking via ESM.

## Caching

- TanStack Query staleTime 60s
- PWA Workbox: fonts CacheFirst, images SWR, **`/api/*` NetworkOnly**
- PWA registerType `prompt` with `PwaUpdatePrompt`

## Virtualization

No `react-window`/`react-virtual` dependency. `DataTable` is pagination-oriented with optional lightweight cues — **not** full list virtualization.

## Future optimization opportunities

Route-based preload on sidebar hover everywhere; image CDN; stricter bundle budgets in CI; virtualize dense career tables if needed.

## Interview Discussion

### Why this architecture?

Split by route and vendor libraries first — highest ROI for MUI/markdown/charts weight.

### Alternative approaches

SSR/streaming; Module Federation. SPA split is enough for authenticated ACOS.

### Trade-offs

First load still pays for MUI; mitigated by chunking/cache.

### Scaling considerations

Measure LCP/INP; reduce markdown highlight cost on AI pages when unused.

### How would this evolve?

Partial hydration only if migrating to meta-framework; keep SW API NetworkOnly.

### Common Frontend Architect interview questions

**Q1. Why NetworkOnly for API in the SW?**  
Avoid stale career data and auth anomalies offline.

**Q2. Are all pages lazy?**  
Yes — routes use lazy imports with Suspense.
