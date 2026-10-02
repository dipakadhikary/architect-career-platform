package com.acos.dashboard.service;

import com.acos.dashboard.dto.DashboardResponse;
import com.acos.dashboard.mapper.DashboardMapper;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Default {@link DashboardService} implementation.
 *
 * <p>Returns configured placeholder metrics and does not query the database.
 */
@Service
public class DashboardServiceImpl implements DashboardService {

  private final DashboardMapper dashboardMapper;

  /**
   * Creates the dashboard service.
   *
   * @param dashboardMapper dashboard mapper
   */
  public DashboardServiceImpl(DashboardMapper dashboardMapper) {
    this.dashboardMapper = dashboardMapper;
  }

  @Override
  public DashboardResponse getDashboard(String email) {
    Objects.requireNonNull(email, "email must not be null");
    return dashboardMapper.toResponse(email);
  }
}
