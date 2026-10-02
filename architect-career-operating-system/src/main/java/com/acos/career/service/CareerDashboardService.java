package com.acos.career.service;

import com.acos.career.dto.CareerDashboardResponse;
import java.util.UUID;

/** Application service for career dashboard aggregation. */
public interface CareerDashboardService {

  /**
   * Returns an aggregated, analytics-ready career tracker summary for the authenticated owner.
   *
   * @param ownerId owning user id
   * @return dashboard summary
   */
  CareerDashboardResponse getSummary(UUID ownerId);
}
