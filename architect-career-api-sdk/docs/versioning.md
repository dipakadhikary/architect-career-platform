# Versioning strategy

## Goal

SDK package versions match backend API SemVer whenever the OpenAPI document publishes a SemVer `info.version`.

Examples:

| Backend API  | SDK          |
| ------------ | ------------ |
| `1.0.0`      | `1.0.0`      |
| `1.2.0`      | `1.2.0`      |
| `2.0.0-rc.1` | `2.0.0-rc.1` |

## Source of truth cascade

1. OpenAPI `info.version` when it matches SemVer
2. Otherwise repository `VERSION` file
3. Synchronized into npm + Maven packaging metadata by `npm run version:sync`

## Current platform note

The running ACOS OpenAPI currently reports `development` when Spring Boot build-info is not present. In that case the SDK keeps `0.0.1` to match `architect-career-operating-system` Maven coordinates.

Once the backend publishes SemVer via build-info / OpenAPI `info.version`, sync will automatically promote the SDK.

## Compatibility guidance

- **MAJOR** — breaking request/response or auth changes
- **MINOR** — additive endpoints/fields
- **PATCH** — documentation or non-breaking fixes in wrappers/tooling

Generated churn alone is not a SemVer bump unless the contract changed.
