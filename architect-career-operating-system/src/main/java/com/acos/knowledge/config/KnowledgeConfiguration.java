package com.acos.knowledge.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Enables knowledge configuration property binding. */
@Configuration
@EnableConfigurationProperties(KnowledgeProperties.class)
public class KnowledgeConfiguration {}
