package com.acos.notification.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;

/** Wires notification settings and the SES client when AWS email is selected. */
@Configuration
@EnableConfigurationProperties(NotificationProperties.class)
public class NotificationConfiguration {

  /**
   * Creates the SES client. Credentials come from the default AWS provider chain.
   *
   * @param properties region
   * @return SES client
   */
  @Bean(destroyMethod = "close")
  @ConditionalOnProperty(prefix = "acos.notification.email", name = "provider", havingValue = "aws")
  public SesV2Client sesV2Client(NotificationProperties properties) {
    return SesV2Client.builder()
        .region(Region.of(properties.email().region()))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }
}
