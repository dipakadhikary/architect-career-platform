# ACOS Frontend (`architect-career-web`)

Architect Career Operating System — production-ready React SPA.

## Stack

- React 19 + TypeScript + Vite 6
- Material UI 7 (light/dark)
- React Router 7 (lazy route code-splitting)
- Axios + TanStack Query
- Zustand, React Hook Form, Zod
- Recharts, react-markdown (+ sanitize + highlight)
- Vitest + React Testing Library + Playwright
- Progressive Web App (`vite-plugin-pwa`)

## Architecture overview

Feature-based layout with thin pages and shared UI:

```
src/
  app/           # env, app/module config, providers, router (lazy routes, PWA)
  features/      # auth, dashboard, knowledge, learning, portfolio, career, ai
  layouts/       # AuthLayout, AppLayout, Sidebar, TopNav, Footer
  pages/         # route entry points + system pages (404/403/500/offline)
  shared/        # api client, theme, hooks, reusable components, utils
```

Business logic lives in feature hooks/services. Pages compose shared components.

## Backend alignment

Targets `architect-career-operating-system` (Java) at `http://localhost:8080`.

| Concern    | Value                                        |
| ---------- | -------------------------------------------- |
| API prefix | `/api/v1`                                    |
| Auth       | `/api/v1/auth` (JWT access + opaque refresh) |
| Envelope   | `ApiResponse<T>`                             |
| AI health  | `GET /api/v1/integration/ai/health`          |
| Dev proxy  | Vite `/api` → `VITE_API_PROXY_TARGET`        |

Never call the Python AI Platform from the browser. AI UX uses the Java Integration Layer only.

## Environment variables

| Variable                     | Purpose                                                    | Default                                |
| ---------------------------- | ---------------------------------------------------------- | -------------------------------------- |
| `VITE_APP_NAME`              | Product name                                               | `ACOS`                                 |
| `VITE_APP_VERSION`           | Display version                                            | `0.1.0`                                |
| `VITE_API_BASE_URL`          | Axios base URL (empty = same origin / proxy)               | ``                                     |
| `VITE_API_PROXY_TARGET`      | Dev proxy target                                           | `http://localhost:8080`                |
| `VITE_ENABLE_QUERY_DEVTOOLS` | React Query Devtools                                       | `false` / `true` in `.env.development` |
| `VITE_AI_PLATFORM_ENABLED`   | Frontend AI feature toggle (mirrors `ai.platform.enabled`) | `false`                                |

Copy `.env.example` to `.env.local` for overrides.

## Run

```bash
cd architect-career-web
npm install
npm run dev
```

App: `http://localhost:5173`  
Requires Business Platform on `:8080` for authenticated API calls.

## Build

```bash
npm run build
npm run preview
```

Production output is written to `dist/` (includes service worker + web manifest).

## Testing guide

```bash
# Unit + component tests
npm run test

# Coverage
npm run test:coverage

# End-to-end (starts Vite via Playwright webServer)
npm run test:e2e

# Interactive e2e
npm run test:e2e:ui
```

Coverage focus:

- Shared utils, API unwrap, auth schemas, AI toggle helpers
- Shared components (dialogs, search, chips, skeletons)
- Auth store + debounced hooks
- Playwright: guest redirects, login/register shells, seeded authenticated navigation, logout, offline page, mobile viewport

CRUD e2e against a live API can be extended with real credentials when the Business Platform is running.

## Quality commands

```bash
npm run typecheck
npm run lint
npm run format:check
npm run build
npm run test
```

## PWA guide

- Manifest + service worker via `vite-plugin-pwa`
- Caches static assets, fonts, and images
- API traffic is **network-only** (no stale auth/data caches)
- Offline banner: “You are currently offline.” with retry
- `/offline` route for explicit offline messaging
- On reconnect, TanStack Query caches are invalidated and retried
- When a new build is available, users are notified and the SW updates

PWA registration is enabled for production builds (`preview` / deployed `dist`). Dev server keeps SW disabled by default.

## Accessibility

- Skip link to main content
- Landmark navigation (`aria-label="Primary"`)
- Dialogs: labelled titles, described confirmations, focus restore
- Tables: `aria-label`, column `scope`, keyboard-activatable rows
- Loading regions use `aria-busy` / live status where applicable

## Security notes

- Access + refresh tokens use **localStorage** (existing project approach). Do not log token values.
- Markdown is rendered through `react-markdown` + `rehype-sanitize` (no `rehype-raw`).
- External markdown links open with `rel="noopener noreferrer"`.
- Axios attaches Bearer tokens and correlation IDs; 401 refresh rotation avoids infinite loops on public auth paths.
- Prefer deploying behind HTTPS with a strict Content-Security-Policy.

## Folder structure (features)

Each domain feature typically includes:

```
features/<domain>/
  api/
  hooks/
  components/
  schemas/
  types/
  index.ts
```

AI additionally includes `services/`, `layouts/`, and `pages/`.
