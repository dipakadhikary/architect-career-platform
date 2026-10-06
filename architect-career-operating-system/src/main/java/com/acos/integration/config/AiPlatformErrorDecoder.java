package com.acos.integration.config;

import com.acos.integration.exception.AiAuthenticationException;
import com.acos.integration.exception.AiPlatformException;
import com.acos.integration.exception.AiPlatformUnavailableException;
import com.acos.integration.exception.AiRateLimitException;
import com.acos.integration.exception.AiTimeoutException;
import com.acos.integration.exception.AiValidationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

/** Maps AI Platform HTTP error responses to typed integration exceptions. */
public class AiPlatformErrorDecoder implements ErrorDecoder {

  private static final int DETAIL_LIMIT = 500;
  private static final ObjectMapper JSON = new ObjectMapper();

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
    String detail = problemDetail(response);
    if (!detail.isBlank()) {
      message = message + ": " + detail;
    }
    Function<String, Exception> mapper = STATUS_MAP.get(status);
    if (mapper == null) {
      return new AiPlatformException(message);
    }
    return mapper.apply(message);
  }

  private static String problemDetail(Response response) {
    byte[] body = readBody(response);
    if (body.length == 0) {
      return "";
    }
    try {
      JsonNode root = JSON.readTree(body);
      String detail = text(root.get("detail"));
      String fields = fieldErrors(root.get("errors"));
      if (!fields.isBlank()) {
        detail = detail.isBlank() ? fields : detail + ": " + fields;
      }
      if (detail.length() > DETAIL_LIMIT) {
        return detail.substring(0, DETAIL_LIMIT);
      }
      return detail;
    } catch (IOException ex) {
      return "";
    }
  }

  private static String fieldErrors(JsonNode errors) {
    if (errors == null || !errors.isArray() || errors.isEmpty()) {
      return "";
    }
    StringBuilder fields = new StringBuilder();
    for (JsonNode error : errors) {
      if (!fields.isEmpty()) {
        fields.append("; ");
      }
      fields.append(text(error.get("field"))).append(' ').append(text(error.get("message")));
    }
    return fields.toString().trim();
  }

  private static String text(JsonNode node) {
    if (node == null || node.isNull()) {
      return "";
    }
    return node.asText("");
  }

  private static byte[] readBody(Response response) {
    if (response.body() == null) {
      return new byte[0];
    }
    try (InputStream input = response.body().asInputStream()) {
      return input.readAllBytes();
    } catch (IOException ex) {
      return new byte[0];
    }
  }
}
