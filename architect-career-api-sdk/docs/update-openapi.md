# How to update OpenAPI

## From a running backend

1. Start `architect-career-operating-system` (default `http://127.0.0.1:8080`).
2. Confirm docs are reachable:

```bash
curl -sS http://127.0.0.1:8080/v3/api-docs | head
```

3. Refresh the SDK snapshot:

```bash
npm run generate:all
```

This downloads, normalizes, syncs versions, and regenerates both SDKs.

## Environment variables

| Variable                   | Purpose                   | Default                 |
| -------------------------- | ------------------------- | ----------------------- |
| `ACOS_API_BASE_URL`        | Backend origin            | `http://127.0.0.1:8080` |
| `ACOS_OPENAPI_URL`         | Full OpenAPI URL override | unset                   |
| `ACOS_JAVA_CLIENT_LIBRARY` | `restclient` or `feign`   | `restclient`            |

## From a committed file only

If the backend is unavailable, regenerate from the committed snapshot:

```bash
npm run generate
```

## Review checklist

- Diff `openapi/acos-api.yaml` for breaking changes
- Regenerate clients
- Run TypeScript and Java builds/tests
- Update consumer applications if models or endpoints changed
- Bump SemVer according to [versioning.md](versioning.md)

## Notes on SpringDoc paths

- Preferred: `/v3/api-docs` (JSON) — permitted anonymously in ACOS security config
- `/v3/api-docs.yaml` may be unavailable depending on security/content negotiation; the download script prefers JSON and converts to YAML locally
