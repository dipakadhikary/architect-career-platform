package com.acos.integration.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/** Enables the OpenFeign AI Platform client when {@code ai.platform.enabled=true}. */
@Configuration
@EnableFeignClients(basePackages = "com.acos.integration.client")
@ConditionalOnProperty(prefix = "ai.platform", name = "enabled", havingValue = "true")
public class AiPlatformFeignClientConfiguration {}
