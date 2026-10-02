# Routing Architecture

## Organization

Routes are declared in `src/app/router/routes.tsx` as React Router v7 `RouteObject[]`.

```mermaid
flowchart TB
  Root[RootLayout + errorElement ServerError]
  Guest[GuestRoute + AuthLayout]
  Prot[ProtectedRoute + AppLayout]
  Root --> Guest
  Root --> Prot
  Root --> Offline[/offline]
  Root --> NotFound[*]
  Guest --> Login[/login]
  Guest --> Register[/register]
  Prot --> Dash[/]
  Prot --> Learning[/learning]
  Prot --> Career[/career/*]
  Prot --> Portfolio[/portfolio]
  Prot --> Knowledge[/knowledge]
  Prot --> AI[/ai/*]
```

## Public (guest) routes

| Path | Page |
| --- | --- |
| `/login` | LoginPage |
| `/register` | RegisterPage |

`GuestRoute` keeps authenticated users out of auth pages (typical redirect-to-home pattern).

## Protected routes

Wrapped by `ProtectedRoute` (requires hydrated authenticated session). Optional `roles` prop supported (`USER` | `ADMIN`) — when provided, unauthorized users go to `/unauthorized`. Current route table does not pass role arrays on most product routes (auth-only gate).

| Area | Paths |
| --- | --- |
| Dashboard | `/` |
| Learning | `/learning`, `/learning/:planId` |
| Career | `/career`, `applications`, `applications/:id`, `companies`, `recruiters` |
| Portfolio | `/portfolio`, `projects/:projectId` |
| Knowledge | `/knowledge`, `:noteId` |
| AI | `/ai`, `chat`, `knowledge`, `learning`, `career`, `portfolio` |
| Utility | `/unauthorized`, `/forbidden`, `/error` |

Also: `/offline`, `/home` → `/`, `*` → NotFound.

## Nested routing

Career and AI use shell layouts (`CareerShell`, `AiShell`) with child routes. Learning/knowledge/portfolio nest detail routes under list paths.

## Lazy loading

`lazyNamed` + `withSuspense` in `lazyRoute.tsx` — route elements are React.lazy imports. Sidebar hover/focus uses `prefetch.ts` to warm lazy chunks for faster navigation.

System error/unauthorized pages may use thin `PagePlaceholder`-style shells — intentional UX, not domain stubs.

## Future route evolution

Role-gated admin sections; AI routes behind successful health + BFF readiness; path-based locales.

## Interview Discussion

### Why this architecture?

Central route config with lazy boundaries keeps the shell small and mirrors product IA in the URL.

### Alternative approaches

File-based routing (Remix/Next); runtime CMS routes. Explicit config is clearer for an authenticated app.

### Trade-offs

Large `routes.tsx` — acceptable until split by domain route modules.

### Scaling considerations

Per-feature route modules; parallel lazy chunks already via manualChunks + lazy pages.

### How would this evolve?

Typed route helpers; breadcrumb metadata on route handles.

### Common Frontend Architect interview questions

**Q1. How is auth enforced?**  
`ProtectedRoute` checks Zustand auth hydration + tokens; redirect to login with `from` state.

**Q2. Are AI routes public?**  
No — under ProtectedRoute; additionally gated by `VITE_AI_PLATFORM_ENABLED` in UX.
