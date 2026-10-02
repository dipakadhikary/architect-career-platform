package com.acos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** OpenAPI platform configuration for ACOS API documentation. */
@Configuration
public class OpenApiConfiguration {

  public static final String BEARER_JWT_SCHEME = "bearer-jwt";

  /**
   * Builds the shared OpenAPI definition used by SpringDoc.
   *
   * @param applicationName Spring application name
   * @param buildProperties optional Maven build-info properties
   * @return configured OpenAPI model
   */
  @Bean
  public OpenAPI acosOpenApi(
      @Value("${spring.application.name}") String applicationName,
      ObjectProvider<BuildProperties> buildProperties) {
    String version =
        buildProperties.stream().findFirst().map(BuildProperties::getVersion).orElse("development");

    SecurityScheme bearerJwt =
        new SecurityScheme()
            .name(BEARER_JWT_SCHEME)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("JWT access token issued by POST /api/v1/auth/login");

    return new OpenAPI()
        .info(
            new Info()
                .title("Architect Career Operating System API")
                .description("Platform API documentation for " + applicationName)
                .version(version)
                .contact(new Contact().name("ACOS Engineering").email("platform@acos.local"))
                .license(new License().name("Proprietary")))
        .components(new Components().addSecuritySchemes(BEARER_JWT_SCHEME, bearerJwt))
        .addSecurityItem(new SecurityRequirement().addList(BEARER_JWT_SCHEME));
  }
}
