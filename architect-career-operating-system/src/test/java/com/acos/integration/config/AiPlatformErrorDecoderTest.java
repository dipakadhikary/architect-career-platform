package com.acos.integration.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.acos.integration.exception.AiAuthenticationException;
import com.acos.integration.exception.AiPlatformException;
import com.acos.integration.exception.AiPlatformUnavailableException;
import com.acos.integration.exception.AiRateLimitException;
import com.acos.integration.exception.AiTimeoutException;
import com.acos.integration.exception.AiValidationException;
import feign.Request;
import feign.Response;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link AiPlatformErrorDecoder}. */
class AiPlatformErrorDecoderTest {

  private final AiPlatformErrorDecoder decoder = new AiPlatformErrorDecoder();

  @Test
  void shouldMapValidationAndAuthAndTimeoutAndUnavailable() {
    assertThat(decoder.decode("m", response(400))).isInstanceOf(AiValidationException.class);
    assertThat(decoder.decode("m", response(401))).isInstanceOf(AiAuthenticationException.class);
    assertThat(decoder.decode("m", response(404))).isInstanceOf(AiPlatformException.class);
    assertThat(decoder.decode("m", response(429))).isInstanceOf(AiRateLimitException.class);
    assertThat(decoder.decode("m", response(500))).isInstanceOf(AiPlatformException.class);
    assertThat(decoder.decode("m", response(503)))
        .isInstanceOf(AiPlatformUnavailableException.class);
    assertThat(decoder.decode("m", response(504))).isInstanceOf(AiTimeoutException.class);
  }

  @Test
  void shouldIncludeTheProblemFieldsInTheMessage() {
    Response response =
        response(400)
            .toBuilder()
            .body(
                """
                {"detail":"Request validation failed","errors":[
                  {"field":"body.difficulty","message":"Input should be a valid string"}
                ]}
                """,
                StandardCharsets.UTF_8)
            .build();

    Exception decoded = decoder.decode("AssistantAiClient#generateProposal()", response);

    assertThat(decoded)
        .isInstanceOf(AiValidationException.class)
        .hasMessageContaining("body.difficulty Input should be a valid string");
  }

  private static Response response(int status) {
    return Response.builder()
        .status(status)
        .reason("error")
        .request(
            Request.create(
                Request.HttpMethod.POST,
                "/api/v1/ai/test",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null))
        .headers(Collections.emptyMap())
        .build();
  }
}
