# Security

## Token storage

Access + refresh tokens in **localStorage** (`token.service.ts`). Comment in code acknowledges XSS risk of this approach.

## XSS prevention

- React escapes text content by default
- AI/knowledge markdown uses `rehype-sanitize`
- Avoid `dangerouslySetInnerHTML` (not a pattern in the shared markdown path)

## CSRF considerations

Bearer token in Authorization header (not cookie session) — classic CSRF against cookie auth is less applicable. If moving to cookie sessions, CSRF tokens/SameSite become mandatory.

## Secure routing

ProtectedRoute for app shell; guest routes for auth; no AI/Business secrets in Vite env (only public flags and API base).

## Environment configuration

Zod-validated `import.meta.env` in `env.ts`. Only `VITE_*` keys exposed to the client — never put private API keys here. AI keys stay on Business/AI servers.

## Proxy

Dev proxy to Business reduces CORS friction; production should serve API same-site or explicit CORS on Business.

## Interview Discussion

### Why this architecture?

SPA Bearer auth matches current Business SecurityFilterChain; sanitize untrusted markdown.

### Alternative approaches

Memory-only access token + HttpOnly refresh cookie; CSP headers at CDN.

### Trade-offs

localStorage XSS impact radius is high — CSP + sanitization + dependency hygiene required.

### Scaling considerations

CSP nonces; Subresource Integrity for CDN; rotate tokens on privilege change.

### How would this evolve?

BFF cookie session; strict CSP; trusted types.

### Common Frontend Architect interview questions

**Q1. Where do OpenAI keys live?**  
Never in the Web app.

**Q2. Is localStorage acceptable?**  
Common for SPAs; document risk; prefer HttpOnly cookies for higher assurance.
