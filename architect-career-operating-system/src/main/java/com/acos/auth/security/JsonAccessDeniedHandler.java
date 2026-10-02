package com.acos.auth.security;

import com.acos.common.api.ApiError;
import com.acos.common.api.ApiResponse;
import com.acos.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/** Returns JSON 403 responses for authenticated callers lacking permission. */
@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  /**
   * Creates the access-denied handler.
   *
   * @param objectMapper JSON mapper
   */
  public JsonAccessDeniedHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    ApiError error = ApiError.create(ErrorCode.ACCESS_DENIED.getCode(), "Access denied");
    objectMapper.writeValue(response.getOutputStream(), ApiResponse.failure(error));
  }
}
