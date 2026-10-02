# API Architecture

## Design principles

1. **Resource-oriented REST** under `/api/v1/...`
2. **Uniform envelope** — every controller returns `ApiResponse<T>`
3. **DTO at the edge** — entities do not cross HTTP
4. **Fail with stable codes** — `ErrorCode` + `ApiError`
5. **Documented via springdoc** — annotations on controllers; UI at `/swagger-ui.html`
6. **Owner-centric** — authenticated principal drives scoping

## REST standards

| Convention | Practice |
| --- | --- |
| Prefix | `/api/v1` |
| Resources | Nouns: `notes`, `plans`, `applications` |
| Nested resources | Learning topics under milestones; interviews/offers under applications |
| Methods | GET/POST/PUT/PATCH/DELETE as implemented per controller |
| Content types | `application/json` |
| Success create | Often HTTP 201 with envelope |
| Idempotent deletes | Delete owned resource; career applications soft-archive semantics where documented |

## Versioning

URL versioning via **`/api/v1`**. No header-based version negotiation implemented.

## Request validation

- Jakarta Bean Validation on request bodies (`@Valid`)
- Feature validators for richer rules (password policy, career state, content length)
- Config caps: `acos.knowledge.max-content-length`, `max-page-size` per domain, etc.
- Constraint violations and `MethodArgumentNotValidException` → HTTP 400 `VALIDATION_FAILED`

## Response structure

```json
{
  "success": true,
  "data": { },
  "error": null,
  "correlationId": "...",
  "timestamp": "2026-08-10T00:00:00Z"
}
```

Failure sets `success=false`, populates `error` (`code`, `message`, `details`), keeps correlation id.

## Error handling

Centralized in `GlobalExceptionHandler` — see [Exception-Handling.md](Exception-Handling.md). Security entry points return JSON as well (`JsonAuthenticationEntryPoint`, `JsonAccessDeniedHandler`).

## OpenAPI

- springdoc scans `com.acos`
- `/v3/api-docs`, Swagger UI `/swagger-ui.html`
- Public for docs paths (permitAll)
- Operation-level `@Operation` / `@ApiResponses` widely used on controllers
- Bearer JWT security scheme configured for protected ops

## Authentication

- Public: register, login, refresh, swagger, actuator health/info
- Protected: all other `/api/v1/**` including `/auth/me`, `/auth/logout`
- Header: `Authorization: Bearer <access-jwt>`

## Authorization

- Filter chain: authenticated user required
- Resource authorization: service-layer `ownerId` / `findByIdAndOwnerId`
- Roles `USER` and `ADMIN` exist; `@EnableMethodSecurity` is on, but domain endpoints primarily use ownership rather than widespread `@PreAuthorize`

## Pagination

Spring Data `Pageable` on list/search endpoints (page/size/sort query params). Domain `max-page-size` properties cap abuse (typically 100).

## Filtering

- Knowledge: `/search` with query params
- Portfolio projects: `/search`
- Career applications: `/search` with optional filters via Specifications
- Career list supports `archived` flag

## Sorting

Via Spring `Pageable` sort parameters where repositories accept `Pageable` (standard Spring Data behavior).

## API inventory (implemented controllers)

| Area | Base path |
| --- | --- |
| Auth | `/api/v1/auth` |
| Dashboard | `/api/v1/dashboard` |
| Knowledge | `/api/v1/knowledge/notes` |
| Learning | `/api/v1/learning/plans...` |
| Portfolio | `/api/v1/portfolio/{projects\|skills\|technologies\|certifications\|achievements}` |
| Career | `/api/v1/career/{companies\|recruiters\|applications\|dashboard}` |
| AI health | `/api/v1/integration/ai/health` |

## Interview Discussion

### Why this architecture?

A versioned, enveloped REST API gives the Web client one predictable contract and simplifies cross-cutting correlation and error UX.

### Alternative approaches

GraphQL for nested learning/career graphs; gRPC for Web (rejected — browser + tooling favor REST); HATEOAS (not implemented).

### Trade-offs

Envelope adds wrapping verbosity. Nested URLs are clear but deep. Incomplete AI BFF means OpenAPI does not yet describe Web’s expected AI routes.

### Scaling considerations

Introduce ETags/conditional requests and denser read models before splitting APIs by service.

### Principal Architect interview questions

**Q1. Why ApiResponse instead of raw bodies?**  
Consistent client parsing, correlation id propagation, and typed error codes.

**Q2. How is breaking change managed?**  
New `/api/v2` when needed; v1 remains until Web migrates.
