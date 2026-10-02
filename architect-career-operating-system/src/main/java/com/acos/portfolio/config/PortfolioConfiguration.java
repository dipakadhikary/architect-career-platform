package com.acos.portfolio.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Enables portfolio configuration property binding. */
@Configuration
@EnableConfigurationProperties(PortfolioProperties.class)
public class PortfolioConfiguration {}
