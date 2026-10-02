# Prometheus Metrics

## Introduction

Prometheus scrapes textual metrics endpoints and stores time series for RED/USE monitoring.

## Problem Statement

Without metrics, CB opens and latency regressions are invisible.

## Why ACOS Uses This

AI exposes `/api/v1/system/metrics`. Business configures Actuator `prometheus` and custom `acos.ai.platform.*` meters—**registry dependency gap noted** in excellence docs.

## Implementation Overview

AI exposes `/api/v1/system/metrics`. Business configures Actuator `prometheus` and custom `acos.ai.platform.*` meters—**registry dependency gap noted** in excellence docs.

## Best Practices

Name metrics carefully; use labels sparingly; alert on SLIs not raw noise.

## Common Mistakes

- High-cardinality labels (userId).
- Assuming Actuator exposure equals working registry.

## Alternative Approaches

StatsD; cloud-native metrics only; logs-as-metrics.

## Trade-offs

Open standard vs scrape ops. Right direction for ACOS.

## References to ACOS modules

- Metrics docs chapter 08
- AiPlatformMetrics

## Interview Questions

**Q:** AI metrics path?
**A:** `/api/v1/system/metrics`.

**Q:** Business caveat?
**A:** Verify `micrometer-registry-prometheus` presence.

## Further Reading

- prometheus.io docs

