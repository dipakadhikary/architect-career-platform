package com.acos.dashboard.service;

import com.acos.dashboard.dto.DashboardResponse;

/** Application service for dashboard summaries. */
public interface DashboardService {

  /**
   * Returns the dashboard summary for an authenticated user.
   *
   * @param email authenticated user email
   * @return dashboard response
   */
  DashboardResponse getDashboard(String email);
}
