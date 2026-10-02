# Swagger / OpenAPI

## Standard

API documentation is provided by **SpringDoc OpenAPI**.

## Platform configuration

`OpenApiConfiguration` defines:

- API title/description/version
- HTTP Bearer security scheme named **`bearer-jwt`**
- Global security requirement for the bearer scheme

SpringDoc settings in `application.yml`:

- API docs path: `/v3/api-docs`
- Swagger UI path: `/swagger-ui.html`
- `packages-to-scan: com.acos`
- Actuator endpoints not shown in Swagger (`show-actuator: false`)

These docs paths are permitted anonymously by Spring Security.

## Controller documentation conventions

Controllers document endpoints with:

- `@Tag` on the controller
- `@Operation` on handlers
- `@SecurityRequirement(name = "bearer-jwt")` for protected APIs
- `@SecurityRequirements` cleared on public auth endpoints
- `@Schema` on DTO records for field descriptions/examples
- Nested schema helper records where controllers document `ApiResponse` shapes

## Evidence

- `src/main/java/com/acos/config/OpenApiConfiguration.java`
- `src/main/resources/application.yml` (`springdoc` section)
- Annotated controllers across auth/knowledge/learning/portfolio/career/dashboard
