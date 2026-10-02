package com.acos.testsupport;

import java.util.Optional;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Shared PostgreSQL Testcontainers / local fallback wiring for integration and repository tests.
 */
public final class PostgresTestSupport {

  public static final String SCHEMA = "acos";

  private PostgresTestSupport() {}

  /**
   * Starts a PostgreSQL container when Docker is available.
   *
   * @return started container, or empty when Docker is unavailable
   */
  public static Optional<PostgreSQLContainer<?>> startPostgresIfAvailable() {
    if (!DockerClientFactory.instance().isDockerAvailable()) {
      return Optional.empty();
    }
    PostgreSQLContainer<?> container =
        new PostgreSQLContainer<>("postgres:17.5-alpine")
            .withDatabaseName(SCHEMA)
            .withUsername(SCHEMA)
            .withPassword(SCHEMA);
    container.start();
    return Optional.of(container);
  }

  /**
   * Registers datasource, JPA, and Flyway properties for the {@code acos} schema.
   *
   * @param registry Spring dynamic property registry
   * @param postgres optional running container
   * @param includeJwtSecret whether to register a test JWT secret
   */
  public static void registerDatasourceProperties(
      DynamicPropertyRegistry registry,
      Optional<PostgreSQLContainer<?>> postgres,
      boolean includeJwtSecret) {
    if (postgres.isPresent()) {
      registry.add(
          "spring.datasource.url", () -> postgres.get().getJdbcUrl() + "?currentSchema=" + SCHEMA);
      registry.add("spring.datasource.username", () -> postgres.get().getUsername());
      registry.add("spring.datasource.password", () -> postgres.get().getPassword());
    } else {
      registry.add(
          "spring.datasource.url",
          () ->
              "jdbc:postgresql://"
                  + env("ACOS_DB_HOST", "localhost")
                  + ":"
                  + env("ACOS_DB_PORT", "5432")
                  + "/"
                  + env("ACOS_DB_NAME", "postgres")
                  + "?currentSchema="
                  + SCHEMA);
      registry.add("spring.datasource.username", () -> env("ACOS_DB_USER", "postgres"));
      registry.add("spring.datasource.password", () -> env("ACOS_DB_PASSWORD", "postgres"));
    }
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    registry.add("spring.jpa.properties.hibernate.default_schema", () -> SCHEMA);
    registry.add("spring.flyway.enabled", () -> "true");
    registry.add("spring.flyway.schemas", () -> SCHEMA);
    registry.add("spring.flyway.default-schema", () -> SCHEMA);
    registry.add("spring.flyway.create-schemas", () -> "true");
    if (includeJwtSecret) {
      registry.add("acos.jwt.secret", () -> "integration-test-secret-key-at-least-32-chars-long");
    }
  }

  /**
   * Reads an environment variable with a default.
   *
   * @param name variable name
   * @param defaultValue default when unset or blank
   * @return resolved value
   */
  public static String env(String name, String defaultValue) {
    String value = System.getenv(name);
    return value == null || value.isBlank() ? defaultValue : value;
  }
}
