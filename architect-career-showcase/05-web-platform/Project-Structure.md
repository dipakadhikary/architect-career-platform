# Project Structure

## Top-level

```text
architect-career-web/
  src/
    app/           # bootstrap: config, providers, router
    features/      # domain features
    layouts/       # AppLayout, AuthLayout, chrome
    pages/         # route pages (auth, career, knowledge, …)
    shared/        # api, components, hooks, store, theme, utils
    test/          # vitest setup
  e2e/             # Playwright specs
  public/
  vite.config.ts
  vitest.config.ts
  playwright.config.ts
```

## Feature folders

Example `features/career`: components (dialogs, dashboards), api, hooks, schemas, types, shell.

Example `features/ai`: pages, components, api, hooks, schemas, services (`aiAvailability`, chat session), layouts.

## Shared folders

| Folder | Contents |
| --- | --- |
| `shared/api` | Axios instance, interceptors, token service, unwrap, types |
| `shared/components` | Cross-feature UI |
| `shared/hooks` | `useAuth`, network status, etc. |
| `shared/store` | theme + notification Zustand stores |
| `shared/theme` | MUI `createAppTheme`, palettes |
| `shared/utils` | correlation id, helpers |
| `shared/types` | shared TS types |

## Components / hooks / services / contexts

- Components: feature + shared (no large Context API tree for domain data).
- Hooks: feature query/mutation hooks; shared auth/network.
- Services: thin helpers (token, AI availability, chat session) — not a DDD domain layer.
- Contexts: React context is not the primary state vehicle; providers wrap theme/query/errors.

## Assets & configuration

- `public/` static assets (favicon)
- `app/config/env.ts` Zod-parsed Vite env
- `app/config/app.config.ts`, `module.config.ts`

## Why this structure was selected

1. Maps cleanly to Business domains.
2. Keeps Axios/auth/theme out of feature churn.
3. Supports lazy route imports by stable `@/` aliases.
4. Familiar to React enterprise teams (feature folders + shared kit).

## Interview Discussion

### Why this architecture?

Balances Feature-Sliced Design ideas with pragmatic Vite SPA conventions.

### Alternative approaches

Nx libs; package-per-feature; atomic design only under `components/`.

### Trade-offs

`pages/` vs `features/*/pages` split needs onboarding notes.

### Scaling considerations

Promote hot features to packages; keep `shared/api` as the integration kernel.

### How would this evolve?

Codemod pages into features; add `entities/` if FSD is mandated.

### Common Frontend Architect interview questions

**Q1. Where are Zod schemas?**  
Per-feature `schemas/` (and auth pages).

**Q2. Alias for imports?**  
`@` → `src` in Vite + TS config.
