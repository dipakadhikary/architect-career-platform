package com.acos.integration.logging;

/**
 * Structured fields for an AI Platform call log entry.
 *
 * @param feature feature module name
 * @param capability capability name
 * @param endpoint relative endpoint path
 * @param latencyMs call latency in milliseconds
 * @param status outcome status ({@code SUCCESS}, {@code FAILURE}, {@code MOCKED})
 */
public record AiPlatformCallLog(
    String feature, String capability, String endpoint, long latencyMs, String status) {}
