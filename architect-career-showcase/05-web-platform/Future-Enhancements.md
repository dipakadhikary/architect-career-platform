# Future Enhancements

Not implemented (or only partially present). Do not describe as current capabilities.

| Item | Current | Target |
| --- | --- | --- |
| Business AI BFF | Client `planned` POSTs + health; many `coming_soon` cards with no invoke | Ship BFF controllers; promote catalog lifecycles |
| AI default off | `VITE_AI_PLATFORM_ENABLED=false` in development env | Align flag with Business `ai.platform.enabled` per environment |
| Generated TS SDK | Hand-written Axios modules | Consume contracts/OpenAPI generators |
| Web Dockerfile / Compose | Absent | nginx/static container |
| HttpOnly cookie auth | localStorage Bearer tokens | Higher-assurance session mode |
| Full list virtualization | Pagination + light cues | react-virtual for huge tables |
| Streaming AI chat | Request/response UI | SSE via Business proxy |
| Automated a11y CI | Manual ARIA practices | axe/Playwright gates |
| Vitest coverage fail-under | Script available | Enforce threshold in CI |
| Admin role-gated UX | Roles on user; little route `roles` usage | Real ADMIN screens |
| Real-time notifications | Snackbar only | WebSocket/SSE events |
| i18n | English UI | Locale packs |

## Promotion rule

When Web + Business changes land together (especially AI BFF), update API/AI docs and remove rows here.
