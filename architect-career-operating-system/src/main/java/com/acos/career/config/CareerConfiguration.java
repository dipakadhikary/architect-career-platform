package com.acos.career.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Enables career configuration property binding. */
@Configuration
@EnableConfigurationProperties(CareerProperties.class)
public class CareerConfiguration {}
