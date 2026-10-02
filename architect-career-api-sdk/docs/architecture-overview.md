# Architecture overview

## Purpose

`architect-career-api-sdk` converts the Java Business Platform OpenAPI document into typed client libraries. It is the contract boundary between:

- **Producer:** `architect-career-operating-system` (Spring Boot + SpringDoc)
- **Consumers:** web, AI, tests, CLI, and future mobile clients

## Design principles

1. **Contract-first generation** — models and API clients are generated; never duplicated by hand.
2. **Generated isolation** — generated trees are disposable and regeneratable.
3. **Custom extension layer** — retry, logging, metrics hooks, and stable domain names live outside generated code.
4. **Multi-language packaging** — TypeScript (Axios) and Java (RestClient, optional Feign) from one OpenAPI source.
5. **SemVer alignment** — SDK packaging version tracks backend API SemVer when available.

## Runtime topology

```text
Java Business Platform
        │
        │  GET /v3/api-docs  (preferred)
        │  GET /v3/api-docs.yaml (fallback when permitted)
        ▼
openapi/acos-api.yaml  ← committed snapshot
        │
        ├───────────────┐
        ▼               ▼
TypeScript Axios     Java RestClient
typescript/generated java/generated
        │               │
        ▼               ▼
typescript/custom    java/custom
(domain wrappers)    (domain wrappers)
```

## Security model

Generated clients honor the OpenAPI `bearer-jwt` security scheme. Wrappers accept static tokens or token suppliers and never log credentials, refresh tokens, or passwords.

## Future versioning of APIs

When the platform introduces `/api/v2`, keep additional snapshots under `openapi/` (for example `acos-api-v2.yaml`) and add generator profiles. Consumers can migrate module-by-module while `v1` remains available.
