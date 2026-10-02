# Error Handling

## API errors

Axios interceptor converts failures to `ApiClientError` (code, status, details, correlationId). Feature hooks catch and notify.

## Unexpected errors

React class `ErrorBoundary` wraps the app in `AppProviders` with fallback title. Router `errorElement` uses `ServerErrorPage` for route-level failures.

## User notifications

`notification.store` + `GlobalNotification` (MUI Snackbar/Alert) for success/error toasts.

## Fallback UI

| Scenario | UI |
| --- | --- |
| Crash in tree | ErrorBoundary fallback |
| 404 | NotFoundPage |
| Offline | OfflineBanner + `/offline` page |
| Session expired | CustomEvent `acos:session-expired` → clear/redirect patterns |
| AI unavailable | `AiUnavailableBanner` / disabled actions |
| Forbidden/Unauthorized | Dedicated pages |

## Interview Discussion

### Why this architecture?

Layered handling (network → query → boundary → pages) prevents blank screens and preserves correlation ids for support.

### Alternative approaches

react-error-boundary only; silent console errors. Explicit UX is required for enterprise apps.

### Trade-offs

Must avoid double-toasting (hook + boundary). Correlation id should be shown in advanced error details where useful.

### Scaling considerations

Central `toUserMessage(error)` catalog keyed by Business `ErrorCode`.

### How would this evolve?

Sentry/Datadog RUM; user-facing “copy error id” from correlationId.

### Common Frontend Architect interview questions

**Q1. Does ErrorBoundary catch async Axios errors?**  
No — those are promise rejections handled in hooks; boundaries catch render errors.

**Q2. How is offline detected?**  
`useNetworkStatus` + OfflineBanner.
