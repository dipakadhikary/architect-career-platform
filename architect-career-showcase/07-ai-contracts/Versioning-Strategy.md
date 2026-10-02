# Versioning Strategy

## Package SemVer

- `VERSION` = **1.0.0** (contracts package)
- Maven `com.acos.ai:architect-career-ai-contracts:1.0.0`
- npm package `architect-career-ai-contracts@1.0.0`
- Generator configs embed `1.0.0` for Python/TS package metadata

Documented in `docs/versioning.md`.

## OpenAPI / URL versioning

- Path major: `/api/v1/...`
- Breaking REST surface → `/api/v2/...` with new module files / aggregator

## Schema / event versioning

- Event channel addresses include `.v1`
- Headers/payloads carry `schemaVersion`
- Payload evolution follows SemVer compatibility table

## Compatibility policy (repo)

| Change | SemVer |
| --- | --- |
| Add optional field / endpoint / event | MINOR |
| Docs only | PATCH |
| Remove/rename field, type change, requiredness change | MAJOR |

## Deprecation

1. Mark deprecated in OpenAPI/AsyncAPI  
2. Keep for at least one minor line  
3. Remove in next MAJOR  
4. Record in `CHANGELOG.md`

## Migration strategy

`docs/migration-guide.md` supports consumer upgrades; MAJOR bumps require coordinated Business/AI releases.

## Interview Discussion

### Why Contract First?

Versioning rules apply to YAML first — code follows.

### Alternative approaches

Date versioning; header-only versioning. URL major is explicit for HTTP.

### Trade-offs

Parallel v1/v2 aggregators temporarily increase maintenance.

### Why not shared DTO libraries?

SemVer on contracts package versions all languages together.

### Why OpenAPI?

`info.version` + path version dual signal.

### When would you choose gRPC?

Package version + proto package names.

### Scaling considerations

Compatibility tests in CI between versions.

### Principal API Architect interview questions

**Q1. Current package version?**  
1.0.0.

**Q2. How do you add a required field?**  
MAJOR + migration guide — do not sneak into MINOR.
