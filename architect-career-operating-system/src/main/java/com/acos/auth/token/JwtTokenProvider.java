package com.acos.auth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

/** Creates and validates HMAC-signed JWT access tokens using jjwt. */
@Component
public final class JwtTokenProvider {

  static final String CLAIM_EMAIL = "email";
  static final String CLAIM_ROLES = "roles";

  private final JwtProperties properties;
  private final SecretKey secretKey;
  private final Clock clock;

  /**
   * Creates a provider with an explicit clock.
   *
   * @param properties JWT properties
   * @param clock time source
   */
  public JwtTokenProvider(JwtProperties properties, Clock clock) {
    this.properties = Objects.requireNonNull(properties, "properties must not be null");
    this.clock = Objects.requireNonNull(clock, "clock must not be null");
    this.secretKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Creates a signed access token for the given subject.
   *
   * @param userId user identifier (JWT subject)
   * @param email user email claim
   * @param roles role names claim
   * @return compact JWT
   */
  public String createAccessToken(UUID userId, String email, Collection<String> roles) {
    Objects.requireNonNull(userId, "userId must not be null");
    Objects.requireNonNull(email, "email must not be null");
    List<String> roleClaims = roles == null ? List.of() : List.copyOf(roles);

    Instant issuedAt = Instant.now(clock);
    Instant expiresAt = issuedAt.plus(properties.accessTokenTtl());

    return Jwts.builder()
        .id(UUID.randomUUID().toString())
        .issuer(properties.issuer())
        .subject(userId.toString())
        .issuedAt(Date.from(issuedAt))
        .expiration(Date.from(expiresAt))
        .claim(CLAIM_EMAIL, email)
        .claim(CLAIM_ROLES, roleClaims)
        .signWith(secretKey)
        .compact();
  }

  /**
   * Returns whether the token is syntactically valid, signed, and unexpired.
   *
   * @param token compact JWT
   * @return {@code true} when valid
   */
  public boolean isValid(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException exception) {
      return false;
    }
  }

  /**
   * Returns whether the token is expired.
   *
   * @param token compact JWT
   * @return {@code true} when expired
   */
  public boolean isExpired(String token) {
    try {
      parseClaims(token);
      return false;
    } catch (ExpiredJwtException exception) {
      return true;
    } catch (JwtException | IllegalArgumentException exception) {
      return false;
    }
  }

  /**
   * Extracts the user id (subject) from a valid token.
   *
   * @param token compact JWT
   * @return user id
   */
  public UUID getUserId(String token) {
    return UUID.fromString(parseClaims(token).getSubject());
  }

  /**
   * Extracts the email claim from a valid token.
   *
   * @param token compact JWT
   * @return email
   */
  public String getEmail(String token) {
    return parseClaims(token).get(CLAIM_EMAIL, String.class);
  }

  /**
   * Extracts role claims from a valid token.
   *
   * @param token compact JWT
   * @return roles
   */
  @SuppressWarnings("unchecked")
  public List<String> getRoles(String token) {
    Object roles = parseClaims(token).get(CLAIM_ROLES);
    if (roles instanceof List<?> roleList) {
      return roleList.stream().map(String::valueOf).toList();
    }
    return List.of();
  }

  /**
   * Returns the token expiration instant.
   *
   * @param token compact JWT
   * @return expiration
   */
  public Instant getExpiration(String token) {
    Date expiration = parseClaims(token).getExpiration();
    return expiration.toInstant();
  }

  /**
   * Parses and verifies signed JWT claims.
   *
   * @param token compact JWT
   * @return claims
   */
  public Claims parseClaims(String token) {
    Objects.requireNonNull(token, "token must not be null");
    if (token.isBlank()) {
      throw new IllegalArgumentException("token must not be blank");
    }
    return Jwts.parser()
        .verifyWith(secretKey)
        .requireIssuer(properties.issuer())
        .clock(() -> Date.from(Instant.now(clock)))
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * Returns the configured access-token TTL in seconds.
   *
   * @return expires-in seconds
   */
  public long getAccessTokenExpiresInSeconds() {
    return properties.accessTokenTtl().toSeconds();
  }
}
