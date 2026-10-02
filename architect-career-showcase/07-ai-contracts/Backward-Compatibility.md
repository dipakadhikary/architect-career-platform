# Backward Compatibility

## Compatibility rules

Follow `docs/versioning.md`:

- Additive optional changes → MINOR (safe for existing consumers)
- Removals, renames, type/requiredness changes → MAJOR
- Keep deprecated elements until MAJOR removal

## API evolution

- Prefer new optional properties and new paths
- Avoid changing existing `operationId`s (breaks generated method names)
- New major aggregator file for `/api/v2`

## Schema evolution

- Events: new optional fields OK; rename channel address = breaking
- Preserve `schemaVersion` semantics for payload readers

## Consumer impact

| Consumer | Impact of breaking change |
| --- | --- |
| AI Platform | Regenerate/vendor Python; fix FastAPI handlers |
| Business | Update Feign DTOs/paths; regression tests |
| Web | Update hand clients / future TS SDK |

## Interview Discussion

### Why Contract First?

Compatibility is reviewed on YAML diffs before code merges.

### Alternative approaches

“Move fast” without SemVer — causes multi-repo outages.

### Trade-offs

Stricter process; slower breaking refactors (intentional).

### Why not shared DTO libraries?

Binary JAR breakage is worse across languages; contracts make breakage visible in PR review.

### Why OpenAPI?

Diffable, reviewable compatibility.

### When would you choose gRPC?

Wire compatibility rules differ (field numbers) — still need governance.

### Scaling considerations

Automated openapi-diff / Spectral rules in CI (extend beyond current lint).

### Principal API Architect interview questions

**Q1. Can I rename a JSON field in a PATCH?**  
Only with MAJOR + dual-read period ideally.

**Q2. Who feels breakage first?**  
Whoever regenerates first — ideally CI contract job before app merges.
