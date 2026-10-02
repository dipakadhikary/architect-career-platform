# Versioning

## Package versions (current)

| Artifact | Version |
| --- | --- |
| Business Platform | `0.0.1` |
| Web Platform | `0.1.0` |
| AI Contracts package | `1.0.0` |
| AI OpenAPI / AsyncAPI `info.version` | `1.0.0` |
| AI HTTP path major | `/api/v1/ai/...` |
| Business HTTP path major | `/api/v1/...` |

## Contracts SemVer policy (documented + practiced)

From `architect-career-ai-contracts/docs/versioning.md`:

| Change | SemVer |
| --- | --- |
| Optional field / endpoint / event | MINOR |
| Docs only | PATCH |
| Remove/rename/type/requiredness | MAJOR |

Deprecate in-spec before MAJOR removal; record in `CHANGELOG.md`.

## Not unified

Business and Web versions are independent early-stage numbers. No ecosystem-wide version lockfile.


## Interview Discussion

### Why this approach?

URL major + package SemVer for AI contracts matches multi-consumer needs.

### Alternative approaches

Header-only versioning; date versions. Less explicit for HTTP clients.

### Trade-offs

Four repos can drift versions independently.

### Enterprise adoption

Publish BOM or compatibility matrix for supported triples.

### Scaling considerations

Automated openapi-diff on MAJOR.

### Principal Architect interview questions

**Q1. Current contracts package version?**  
1.0.0.

**Q2. Business API path version?**  
`/api/v1/...`.
