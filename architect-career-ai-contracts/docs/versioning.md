# Versioning strategy

## Package version

`VERSION` is the SemVer for the contracts package (currently `1.0.0`).

## API versioning

- URL major version: `/api/v1/...`
- Future breaking REST surface: `/api/v2/...` with new module files

## Schema versioning

- Event headers carry `schemaVersion`
- Payload schema files are versioned with the package and event major address (`.v1`)

## Compatibility policy

| Change                                                | SemVer | Notes                    |
| ----------------------------------------------------- | ------ | ------------------------ |
| Add optional field / endpoint / event                 | MINOR  | Backward compatible      |
| Documentation-only                                    | PATCH  | No runtime impact        |
| Remove/rename field, change type, change requiredness | MAJOR  | Requires migration guide |

## Deprecation

1. Mark deprecated in OpenAPI/AsyncAPI description
2. Keep serving for at least one minor line
3. Remove only in the next major version
4. Record removal in `CHANGELOG.md`
