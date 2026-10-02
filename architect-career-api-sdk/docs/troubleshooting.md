# Troubleshooting guide

## OpenAPI download fails

**Symptom:** `npm run openapi:download` exits non-zero.

**Checks:**

1. Backend is running and healthy: `curl http://127.0.0.1:8080/actuator/health`
2. Docs endpoint responds: `curl http://127.0.0.1:8080/v3/api-docs`
3. Override URL if needed: `ACOS_OPENAPI_URL=... npm run openapi:download`

If `/v3/api-docs.yaml` returns `401` while JSON succeeds, use the default JSON path (already preferred by the script).

## Generator fails on OpenAPI 3.1 features

Generation uses `--skip-validate-spec` to tolerate SpringDoc 3.1 documents. If generation still fails:

1. Inspect the failing schema in `openapi/acos-api.yaml`
2. Upgrade OpenAPI Generator version in `openapitools.json` / CLI pin
3. Prefer fixing annotations in the Java backend rather than hand-editing the snapshot

## TypeScript build errors after regenerate

1. Confirm `typescript/generated` was fully replaced
2. Rebuild wrappers: `npm run build --prefix typescript`
3. If generated method names changed (operationId churn), update only `typescript/custom/*`

## Java module not found in reactor

Ensure `java/generated/pom.xml` exists (run `npm run generate:java`) and artifactId remains `architect-career-api-sdk`.

```bash
mvn -f java/pom.xml -q projects
mvn -f java/pom.xml -B verify
```

## Feign output missing

Feign is opt-in:

```bash
ACOS_JAVA_CLIENT_LIBRARY=feign npm run generate:java
```

Output appears under `java/generated-feign`.

## Version did not change after backend update

If OpenAPI still says `development`, SemVer sync will not rewrite `VERSION`. Publish a SemVer from the backend OpenAPI `info.version` or update `VERSION` intentionally.
