# State Management

## Strategy

| Kind | Tool | Examples |
| --- | --- | --- |
| **Server state** | TanStack Query | Lists, details, dashboard, AI health/mutations |
| **Global client state** | Zustand | Auth session, theme mode, notifications |
| **Local UI state** | `useState` / RHF | Dialogs, tabs, form fields |

## Global state (Zustand)

- `features/auth/store/auth.store.ts` — user, isAuthenticated, hydrate/setSession/clearSession
- `shared/store/theme.store.ts` — light/dark
- `shared/store/notification.store.ts` — snackbar queue for `GlobalNotification`

## Server state (TanStack Query)

`QueryProvider` configures defaults from `appConfig.query` (**staleTime 60s**, **retry 1**, **`refetchOnWindowFocus: false`**). Feature hooks (`useDashboardOverview`, career/knowledge/learning hooks, `useAiHealth`, `useAiMutations`, etc.) encapsulate `useQuery` / `useMutation`.

`useDashboardOverview` aggregates real module queries and also consumes the dashboard endpoint (Business metrics may still be placeholder values — see Business Platform docs). `OfflineBanner` can invalidate queries on reconnect.

## Caching & synchronization

- Query cache is the source of truth for remote entities after fetch.
- Mutations invalidate/refetch related keys (feature-specific).
- Auth tokens live outside React Query (tokenService + auth store).
- AI chat session helper may keep ephemeral client conversation state in feature services.

## Why this approach was chosen

1. TanStack Query eliminates hand-rolled fetch caches and race handling.
2. Zustand is minimal for auth/theme without Redux boilerplate.
3. Avoids putting server lists in Zustand (duplication/staleness).

## Interview Discussion

### Why this architecture?

Separating server and client state is the modern React default for API-heavy SPAs.

### Alternative approaches

Redux Toolkit + RTK Query; MobX; all-in-Zustand. Query + Zustand is leaner for this codebase.

### Trade-offs

Developers must know which store owns what; misuse (putting server lists in Zustand) is a review smell.

### Scaling considerations

Query persistence (optional); normalized entity cache only if duplication hurts.

### How would this evolve?

Query key factory per feature; optimistic updates on high-churn career status transitions.

### Common Frontend Architect interview questions

**Q1. Where is the access token?**  
`localStorage` via tokenService; auth store mirrors user/session flags.

**Q2. Does Query refresh JWTs?**  
No — Axios interceptor refreshes; Query just retries failed requests after that if remounted/refetched.
