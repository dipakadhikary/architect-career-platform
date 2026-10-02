# How to generate clients

```bash
npm install
npm run bundle:openapi
npm run generate:java
npm run generate:python
npm run generate:typescript
```

Or all at once:

```bash
npm run generate
```

## Outputs

| Language   | Path                   | Generator                                    |
| ---------- | ---------------------- | -------------------------------------------- |
| Java       | `generated/java`       | OpenAPI Generator `java` + Feign             |
| Python     | `generated/python`     | OpenAPI Generator `python` (Pydantic models) |
| TypeScript | `generated/typescript` | OpenAPI Generator `typescript-axios`         |

Generator knobs live under `generator/<language>/`.
