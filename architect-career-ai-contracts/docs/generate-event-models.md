# How to generate event models

```bash
npm run bundle:asyncapi
npm run generate:events
```

## Outputs

`generated/asyncapi/<domain>/schemas/*.schema.json`

These JSON Schema snapshots are the portable event model source for:

- Java (`jsonschema2pojo`, jackson-module-jsonSchema workflows, etc.)
- Python (`datamodel-code-generator`, Pydantic)
- TypeScript (`json-schema-to-typescript`)

Message metadata is exported under `generated/asyncapi/<domain>/messages/`.
