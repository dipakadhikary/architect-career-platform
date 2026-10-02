package com.acos.learning.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Enables learning configuration property binding. */
@Configuration
@EnableConfigurationProperties(LearningProperties.class)
public class LearningConfiguration {}
