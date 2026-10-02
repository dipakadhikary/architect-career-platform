# Folder structure

```text
architect-career-api-sdk/
├── .github/workflows/
│   └── generate-and-validate.yml
├── docs/
│   ├── architecture-overview.md
│   ├── consume-sdk.md
│   ├── folder-structure.md
│   ├── sdk-generation.md
│   ├── troubleshooting.md
│   ├── update-openapi.md
│   └── versioning.md
├── openapi/
│   ├── acos-api.json
│   └── acos-api.yaml
├── scripts/
│   ├── download-openapi.mjs
│   ├── normalize-openapi.mjs
│   ├── sync-version.mjs
│   ├── generate-typescript.mjs
│   ├── generate-java.mjs
│   └── postprocess-java.mjs
├── typescript/
│   ├── custom/                 # hand-maintained wrappers + tests
│   ├── generated/              # generated Axios SDK
│   ├── index.ts
│   ├── package.json
│   ├── tsconfig.json
│   └── vitest.config.ts
├── java/
│   ├── custom/                 # hand-maintained wrappers + tests
│   ├── generated/              # generated RestClient SDK
│   └── pom.xml                 # parent reactor
├── openapitools.json
├── package.json
├── VERSION
└── README.md
```

## Editable vs generated

| Path                      | Editable                             |
| ------------------------- | ------------------------------------ |
| `openapi/*`               | Yes (via download/normalize scripts) |
| `typescript/custom/**`    | Yes                                  |
| `java/custom/**`          | Yes                                  |
| `scripts/**`              | Yes                                  |
| `docs/**`                 | Yes                                  |
| `typescript/generated/**` | No                                   |
| `java/generated/src/**`   | No                                   |
