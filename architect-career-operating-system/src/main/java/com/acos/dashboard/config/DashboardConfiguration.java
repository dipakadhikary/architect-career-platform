package com.acos.dashboard.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Enables dashboard configuration property binding. */
@Configuration
@EnableConfigurationProperties(DashboardProperties.class)
public class DashboardConfiguration {}
