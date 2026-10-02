# Generator notes for Java consumers

Output: `generated/java`

- Client library: OpenFeign
- Models: Jackson-annotated DTOs with Jakarta Bean Validation
- Target: Java 21 compatible bytecode/source settings in generated POM
- Problem Details and shared schemas are included from the composed OpenAPI bundle

Native Java `record` generation is not fully supported by the upstream OpenAPI Generator Feign templates. Generated models are intentionally immutable-friendly POJOs. Applications may map them to records at the domain boundary.

Do not hand-edit generated sources. Regenerate from OpenAPI.
