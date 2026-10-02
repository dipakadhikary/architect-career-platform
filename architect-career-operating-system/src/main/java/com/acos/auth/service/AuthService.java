package com.acos.auth.service;

import com.acos.auth.dto.AuthenticationResponse;
import com.acos.auth.dto.LoginRequest;
import com.acos.auth.dto.LoginResponse;
import com.acos.auth.dto.RegisterRequest;
import com.acos.auth.dto.RegisterResponse;
import com.acos.auth.token.TokenResponse;
import java.util.UUID;

/** Authentication use-cases for the platform. */
public interface AuthService {

  /**
   * Registers a new user account with the default {@code USER} role.
   *
   * @param request registration request
   * @return registration response
   */
  RegisterResponse register(RegisterRequest request);

  /**
   * Authenticates an existing user and issues access/refresh tokens.
   *
   * @param request login request
   * @return authentication response
   */
  AuthenticationResponse login(LoginRequest request);

  /**
   * Returns the current authenticated user profile.
   *
   * @param userId authenticated user id
   * @return user profile
   */
  LoginResponse currentUser(UUID userId);

  /**
   * Exchanges a valid refresh token for a new token pair (rotation).
   *
   * @param refreshToken opaque refresh token
   * @return new token pair
   */
  TokenResponse refresh(String refreshToken);

  /**
   * Revokes the presented refresh token.
   *
   * @param refreshToken opaque refresh token
   */
  void logout(String refreshToken);
}
