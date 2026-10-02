package com.acos.auth.security;

import com.acos.auth.token.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/** Authenticates requests that present a Bearer JWT access token. */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  /**
   * Creates the JWT authentication filter.
   *
   * @param jwtTokenProvider JWT access-token provider
   * @param userDetailsService user details loader
   */
  public JwtAuthenticationFilter(
      JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorization != null
        && authorization.startsWith(BEARER_PREFIX)
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      String token = authorization.substring(BEARER_PREFIX.length()).trim();
      authenticateIfValid(token, request);
    }
    filterChain.doFilter(request, response);
  }

  private void authenticateIfValid(String token, HttpServletRequest request) {
    try {
      if (!jwtTokenProvider.isValid(token)) {
        return;
      }
      String email = jwtTokenProvider.getEmail(token);
      UserDetails userDetails = userDetailsService.loadUserByUsername(email);
      if (!userDetails.isEnabled()) {
        return;
      }
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (UsernameNotFoundException | IllegalArgumentException exception) {
      SecurityContextHolder.clearContext();
      if (LOG.isDebugEnabled()) {
        LOG.debug(
            "JWT authentication skipped for {}: {}",
            Objects.toString(request.getRequestURI(), ""),
            exception.getMessage());
      }
    }
  }
}
