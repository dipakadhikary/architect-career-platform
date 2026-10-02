package com.acos.knowledge.repository;

import com.acos.config.JpaAuditingConfiguration;
import com.acos.testsupport.PostgresTestSupport;
import java.util.Optional;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/** Shared PostgreSQL setup for knowledge repository slice tests. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingConfiguration.class)
class KnowledgeRepositoryTestSupport {

  private static final Optional<PostgreSQLContainer<?>> POSTGRES =
      PostgresTestSupport.startPostgresIfAvailable();

  @DynamicPropertySource
  static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
    PostgresTestSupport.registerDatasourceProperties(registry, POSTGRES, false);
  }
}
