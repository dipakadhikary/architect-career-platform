package com.acos.auth.security;

import com.acos.common.api.ApiError;
import com.acos.common.api.ApiResponse;
import com.acos.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/** Returns JSON 401 responses for unauthenticated API calls. */
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  /**
   * Creates the entry point.
   *
   * @param objectMapper JSON mapper
   */
  public JsonAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ApiError error = ApiError.create(ErrorCode.UNAUTHORIZED.getCode(), "Authentication required");
    objectMapper.writeValue(response.getOutputStream(), ApiResponse.failure(error));
  }
}
