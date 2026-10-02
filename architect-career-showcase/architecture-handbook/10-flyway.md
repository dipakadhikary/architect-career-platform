# Flyway

## Standard

Flyway owns database schema changes. Hibernate does not create or update schema (`ddl-auto: validate`).

## Schema

- Application schema name: **`acos`**
- Created in `V1__initial_schema.sql`
- Local/test profiles set:
  - `spring.flyway.schemas=acos`
  - `spring.flyway.default-schema=acos`
  - `spring.jpa.properties.hibernate.default_schema=acos`
- JDBC URLs use `currentSchema=acos`

## Migration location and naming

- Location: `classpath:db/migration`
- Files: `src/main/resources/db/migration/Vn__description.sql`
- Current sequence includes `V1` … `V9`
- `spring.flyway.validate-on-migrate=true`

## Entity mapping

Entities declare:

```java
@Table(name = "...", schema = "acos")
```

and extend `BaseEntity` so identity/version/timestamps align with migration columns.

## Change rules reflected by the setup

1. Add a new numbered migration for schema changes; do not rely on Hibernate DDL.
2. Do not edit already-applied migrations in shared environments; add a new version instead.
3. Keep constraint/index names explicit in SQL where migrations already do so.
4. Feature tables commonly use feature prefixes (`career_*`, `portfolio_*`, `learning_*`); auth core tables are unprefixed as implemented.

## Evidence

- `src/main/resources/db/migration/`
- `src/main/resources/application.yml`
- `src/main/resources/application-local.yml`
- `src/main/resources/application-test.yml`
- Entity `@Table(..., schema = "acos")` annotations
