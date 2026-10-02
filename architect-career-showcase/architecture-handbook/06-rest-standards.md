# REST Standards

## Base path

All HTTP APIs are versioned under:

```text
/api/v1/...
```

Examples:

- `/api/v1/auth`
- `/api/v1/knowledge/...`
- `/api/v1/learning/...`
- `/api/v1/portfolio/...`
- `/api/v1/career/...`
- `/api/v1/dashboard`
- `/api/v1/career/dashboard`

## Controller design

Controllers are thin:

1. Extract authenticated user id from `@AuthenticationPrincipal AcosUserDetails`
2. Validate request body with `@Valid`
3. Delegate to a service interface
4. Wrap the result in `ApiResponse`
5. Return the appropriate `ResponseEntity` status

Controllers do not contain business rules, persistence access, or exception-to-HTTP mapping beyond choosing success status codes.

## HTTP conventions used

| Operation | Typical status |
| --- | --- |
| Create | `201 CREATED` |
| Read / update / successful delete-or-archive | `200 OK` |
| Validation / business / not-found failures | Mapped by `GlobalExceptionHandler` / security handlers |

Request/response media type is JSON (`MediaType.APPLICATION_JSON_VALUE`) on consuming/producing endpoints.

## Security annotations

- Protected controllers declare `@SecurityRequirement(name = "bearer-jwt")`
- Public auth endpoints clear security requirements with `@SecurityRequirements` where applicable (register/login/refresh)

## OpenAPI annotations

Controllers commonly use:

- `@Tag`
- `@Operation`
- `@Parameter` / `@ParameterObject` for path/query/pageable inputs
- `@Schema` on DTO records

## Pagination

List endpoints that page use Spring Data `Pageable` with `@PageableDefault` and return page DTO records (content + page metadata).

## Evidence

- Feature controllers under `com.acos.*.controller` and `com.acos.dashboard.web`
- `AcosUserDetails` principal usage
- `ApiResponse` wrapping at controller boundaries
