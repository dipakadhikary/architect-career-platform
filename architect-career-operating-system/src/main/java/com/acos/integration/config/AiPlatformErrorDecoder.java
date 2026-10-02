package com.acos.integration.config;

import com.acos.integration.exception.AiAuthenticationException;
import com.acos.integration.exception.AiPlatformException;
import com.acos.integration.exception.AiPlatformUnavailableException;
import com.acos.integration.exception.AiRateLimitException;
import com.acos.integration.exception.AiTimeoutException;
import com.acos.integration.exception.AiValidationException;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.util.Map;
import java.util.function.Function;

/** Maps AI Platform HTTP error responses to typed integration exceptions. */
public class AiPlatformErrorDecoder implements ErrorDecoder {

  private static final Map<Integer, Function<String, Exception>> STATUS_MAP =
      Map.ofEntries(
          Map.entry(400, AiValidationException::new),
          Map.entry(401, AiAuthenticationException::new),
          Map.entry(403, AiAuthenticationException::new),
          Map.entry(404, AiPlatformException::new),
          Map.entry(408, AiTimeoutException::new),
          Map.entry(422, AiValidationException::new),
          Map.entry(429, AiRateLimitException::new),
          Map.entry(500, AiPlatformException::new),
          Map.entry(502, AiPlatformException::new),
          Map.entry(503, AiPlatformUnavailableException::new),
          Map.entry(504, AiTimeoutException::new));

  @Override
  public Exception decode(String methodKey, Response response) {
    int status = response.status();
    String message = "AI Platform call failed for " + methodKey + " with HTTP " + status;
    Function<String, Exception> mapper = STATUS_MAP.get(status);
    if (mapper == null) {
      return new AiPlatformException(message);
    }
    return mapper.apply(message);
  }
}
