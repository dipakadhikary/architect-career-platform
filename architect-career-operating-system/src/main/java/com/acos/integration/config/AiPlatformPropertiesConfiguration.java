package com.acos.integration.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Enables AI Platform configuration property binding. */
@Configuration
@EnableConfigurationProperties(AiPlatformProperties.class)
public class AiPlatformPropertiesConfiguration {}
