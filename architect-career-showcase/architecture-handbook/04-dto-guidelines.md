# DTO Guidelines

## Standard

The HTTP boundary uses Java **records** for request and response DTOs. JPA entities are not returned from controllers.

## Request DTOs

1. Declared as `public record` under `com.acos.<feature>.dto`
2. Carry Jakarta Validation annotations (`@NotNull`, `@NotBlank`, `@Size`, `@Email`, etc.)
3. Validated at controllers with `@Valid @RequestBody`
4. Often documented with `@Schema` for OpenAPI

## Response DTOs

1. Declared as records
2. Produced by MapStruct mappers (or service assembly into mapper-backed types)
3. Wrapped in `ApiResponse<T>` by controllers
4. Compact constructors commonly enforce required fields with `Objects.requireNonNull`

## Envelope

All REST payloads use:

```text
ApiResponse<T> {
  success
  data
  error
  correlationId
  timestamp
}
```

Success: `ApiResponse.success(data)`  
Failure: `ApiResponse.failure(ApiError)`

## Mapping

- Controllers talk DTOs only.
- Services persist/load entities and convert via MapStruct mappers.
- Nested summaries (for example company/recruiter summaries on applications) are DTO projections, not entity graphs serialized directly.

## Version field note

- Entities inherit `@Version` from `BaseEntity`.
- Career response DTOs expose `version` to clients.
- Knowledge / learning / portfolio response DTOs currently do **not** expose `version`.

Do not treat DTO `version` exposure as a platform-wide rule unless other features adopt it.

## Evidence

- `src/main/java/com/acos/common/api/ApiResponse.java`
- Feature `dto` packages
- Controllers returning `ResponseEntity<ApiResponse<...>>`
- Mappers under `com.acos.*.mapper`
