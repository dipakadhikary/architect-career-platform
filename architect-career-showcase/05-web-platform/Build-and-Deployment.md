# Build and Deployment

## Build process

```bash
npm run typecheck   # tsc -b --noEmit
npm run build       # tsc -b && vite build
npm run preview     # vite preview :4173
npm run dev         # vite :5173 with /api proxy
```

Output: static assets in `dist/` suitable for any static host.

## Environment variables

| Variable | Role |
| --- | --- |
| `VITE_APP_NAME` | App title |
| `VITE_APP_VERSION` | Version string |
| `VITE_API_BASE_URL` | Optional absolute API base |
| `VITE_API_PROXY_TARGET` | Dev proxy target (default `http://localhost:8080`) |
| `VITE_ENABLE_QUERY_DEVTOOLS` | React Query Devtools |
| `VITE_AI_PLATFORM_ENABLED` | Gate AI UX |

Validated by Zod in `env.ts`. See `.env.example` / `.env.development`.

## Configuration

`app.config.ts` + `module.config.ts` derive timeouts and AI paths from env.

## Docker

**No Dockerfile** in `architect-career-web` today. Deployment is static hosting or a future nginx container.

## Production deployment

1. Build `dist/`
2. Serve with SPA fallback to `index.html` (Workbox navigateFallback already assumes this)
3. Point API via same-origin reverse proxy `/api` → Business or set `VITE_API_BASE_URL`
4. Enable/disable AI flag to match Business `ai.platform.enabled`

## Future CDN strategy

Cache hashed assets immutable; short TTL on `index.html`; SW update prompt already implemented.

## Interview Discussion

### Why this architecture?

Static SPA + API proxy is simple and aligns with Business as the only backend.

### Alternative approaches

SSR host; containerized nginx in-repo. Docker is a packaging gap, not a runtime requirement.

### Trade-offs

Ops must configure reverse proxy carefully for deep links and `/api`.

### Scaling considerations

CDN + edge; separate cache policies for HTML vs assets.

### How would this evolve?

Dockerfile + compose with Business; GitHub Actions deploy; environment-specific AI flags.

### Common Frontend Architect interview questions

**Q1. Default dev ports?**  
Web 5173, Business 8080 via proxy.

**Q2. Is there a Web container?**  
Not in-repo currently.
