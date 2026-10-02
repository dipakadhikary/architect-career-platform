# Coding Standards

## Language and platform

- Java **21** (`pom.xml` `java.version`, Enforcer `[21,22)`)
- Spring Boot **3.5.x**
- Constructor injection only (`private final` collaborators; no field `@Autowired` in `src/main/java`)

## Formatting

- Spotless with **Google Java Format** (`GOOGLE` style)
- Unused imports removed by Spotless
- Spotless check runs in Maven `validate`
- Indentation: **2 spaces** (Checkstyle)
- No tab characters (Checkstyle `FileTabCharacter`)

## Checkstyle highlights

Configured in `config/checkstyle/checkstyle.xml`:

| Rule | Standard |
| --- | --- |
| Line length | Max **120** |
| Imports | No star imports; no redundant/unused imports |
| Package names | `^com\.acos(\.[a-z][a-z0-9]{0,31})*$` |
| TODO comments | Must be named, e.g. `TODO (name): ...` |
| FIXME | Forbidden; prefer named TODO |
| Javadoc | Present on packages and public types/methods as enforced by config |

## Style conventions used in source

1. **No Lombok annotations in application source** — entities and DTOs are handwritten; Lombok is present mainly for MapStruct processor binding.
2. **No `@Data` on JPA entities.**
3. Prefer `Objects.requireNonNull(...)` for constructor/service invariants.
4. Prefer `Locale.ROOT` for case conversions where used (`toLowerCase` / `toUpperCase`).
5. Logging via SLF4J `LoggerFactory.getLogger(...)`.
6. Service naming: interface + `*Impl` implementation.
7. DTO types are Java **records**.

## Quality gates (verify)

Also enforced by Maven:

- Checkstyle
- PMD (+ CPD)
- SpotBugs
- Enforcer (Java version, dependency convergence)
- JaCoCo reporting/check scaffolding (coverage minimum currently `0.00`)

## Evidence

- `pom.xml` (Spotless, Enforcer, quality plugins)
- `config/checkstyle/checkstyle.xml`
- `config/pmd/pmd-ruleset.xml`
- `config/spotbugs/spotbugs-exclude.xml`
- `.editorconfig`
