# Contract-first development

1. Design or change the OpenAPI/AsyncAPI document.
2. Lint and validate contracts in CI.
3. Generate language artifacts.
4. Implement services against generated models/clients.
5. Never introduce hand-written cross-service DTOs that diverge from contracts.

## Definition of done for a contract change

- Spec lint passes
- Examples are present for new operations/messages
- Versioning impact documented in `CHANGELOG.md`
- Generators succeed for Java, Python, and TypeScript
- Consumer and producer guides updated when behavior changes
