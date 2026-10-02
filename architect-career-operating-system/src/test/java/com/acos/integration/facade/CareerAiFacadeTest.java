package com.acos.integration.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.acos.integration.config.AiPlatformProperties;
import com.acos.integration.dto.ResumeRequest;
import com.acos.integration.dto.ResumeResponse;
import com.acos.integration.gateway.CareerAiGateway;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link CareerAiFacade}. */
@ExtendWith(MockitoExtension.class)
class CareerAiFacadeTest {

  private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

  @Mock private CareerAiGateway careerAiGateway;

  private CareerAiFacade facade;

  @BeforeEach
  void setUp() {
    facade = new CareerAiFacade(careerAiGateway, new AiFacadeSupport(properties(false)));
  }

  @Test
  void shouldReturnUnavailableMessageWhenDisabled() {
    ResumeRequest request = new ResumeRequest(USER_ID, "Architect", List.of(), List.of());

    ResumeResponse response = facade.generateResume(request);

    assertThat(response.content()).isEqualTo(AiPlatformFallbacks.UNAVAILABLE_MESSAGE);
    assertThat(response.format()).isEqualTo("plain");
    verify(careerAiGateway, never()).generateResume(any());
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
