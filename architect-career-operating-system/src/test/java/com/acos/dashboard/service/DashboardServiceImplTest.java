package com.acos.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.acos.dashboard.dto.DashboardResponse;
import com.acos.dashboard.mapper.DashboardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link DashboardServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

  private static final String EMAIL = "ada@acos.local";

  @Mock private DashboardMapper dashboardMapper;

  private DashboardServiceImpl dashboardService;

  @BeforeEach
  void setUp() {
    dashboardService = new DashboardServiceImpl(dashboardMapper);
  }

  @Test
  void shouldReturnMappedDashboard() {
    DashboardResponse expected =
        new DashboardResponse("Welcome to ACOS, " + EMAIL, 45, 2, 3, 1, 4, 2);
    when(dashboardMapper.toResponse(EMAIL)).thenReturn(expected);

    DashboardResponse response = dashboardService.getDashboard(EMAIL);

    assertThat(response).isEqualTo(expected);
    verify(dashboardMapper).toResponse(EMAIL);
  }

  @Test
  void shouldRejectNullEmail() {
    assertThatThrownBy(() -> dashboardService.getDashboard(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("email");
  }
}
