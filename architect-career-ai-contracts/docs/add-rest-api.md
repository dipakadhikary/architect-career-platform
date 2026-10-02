# How to add new REST APIs

1. Add or extend schemas in `openapi/common` when reusable.
2. Create/update the domain file under `openapi/<domain>/`.
3. Add path `$ref`s to `openapi/ai-platform-v1.yaml`.
4. Provide `operationId`, tags, descriptions, and examples.
5. Map error responses to `openapi/common/errors.yaml`.
6. Update `CHANGELOG.md` and version policy.
7. Run `npm run verify`.
