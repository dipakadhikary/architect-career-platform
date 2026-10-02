# Troubleshooting

## OpenAPI lint fails on `$ref`

- Ensure relative paths are correct from the referencing file
- Run `npm run lint:openapi` (Swagger Parser)
- Bundle with `npm run bundle:openapi` to surface unresolved refs

## AsyncAPI validate fails

- Confirm `asyncapi: 3.0.0`
- Messages referenced by channels must exist under `components.messages`
- Avoid mixing AsyncAPI 2 channel syntax with 3.x operations

## Generator produces empty output

- Confirm `dist/openapi/ai-platform-v1.bundled.yaml` exists
- Re-run `npm run bundle:openapi` before generate scripts
- Check OpenAPI Generator CLI version pin in `openapitools.json`

## Java / Python / TypeScript generation errors

- Prefer fixing the contract over patching generated code
- Keep contracts linted with Swagger Parser / AsyncAPI Parser even when generators use `--skip-validate-spec`
