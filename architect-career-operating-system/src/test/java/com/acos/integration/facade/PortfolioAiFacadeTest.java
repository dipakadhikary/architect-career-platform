package com.acos.integration.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.acos.integration.config.AiPlatformProperties;
import com.acos.integration.dto.PortfolioReviewRequest;
import com.acos.integration.dto.PortfolioReviewResponse;
import com.acos.integration.gateway.PortfolioAiGateway;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link PortfolioAiFacade}. */
@ExtendWith(MockitoExtension.class)
class PortfolioAiFacadeTest {

  private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

  @Mock private PortfolioAiGateway portfolioAiGateway;

  private PortfolioAiFacade facade;

  @BeforeEach
  void setUp() {
    facade = new PortfolioAiFacade(portfolioAiGateway, new AiFacadeSupport(properties(false)));
  }

  @Test
  void shouldReturnEmptyAnalysisWhenDisabled() {
    PortfolioReviewRequest request = new PortfolioReviewRequest(USER_ID, List.of(), "Architect");

    PortfolioReviewResponse response = facade.reviewPortfolio(request);

    assertThat(response.summary()).isEmpty();
    assertThat(response.strengths()).isEmpty();
    assertThat(response.improvements()).isEmpty();
    verify(portfolioAiGateway, never()).reviewPortfolio(any());
  }

  private static AiPlatformProperties properties(boolean enabled) {
    return new AiPlatformProperties(
        enabled,
        "http://localhost:8090",
        "key",
        Duration.ofSeconds(3),
        Duration.ofSeconds(30),
        "BASIC",
        new AiPlatformProperties.CompressionProperties(true, true, 2048),
        new AiPlatformProperties.ResilienceProperties("ai-platform"),
        new AiPlatformProperties.RetryProperties(
            true, 3, Duration.ofMillis(200), Duration.ofSeconds(2)));
  }
}
