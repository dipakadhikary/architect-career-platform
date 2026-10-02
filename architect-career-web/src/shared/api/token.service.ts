import { appConfig } from '@/app/config/app.config';
import { storage } from '@/shared/utils/storage';
import type { AuthUser, TokenPair } from '@/features/auth/types/auth.types';

const { auth } = appConfig;

/**
 * JWT / refresh-token persistence. Access tokens are JWTs; refresh tokens are opaque.
 * On refresh, the backend rotates both tokens — always replace the pair.
 *
 * Security note: tokens are stored in localStorage (existing project approach).
 * Never log token values. Prefer XSS-safe rendering (sanitized Markdown) and CSP in production.
 * Migrating to httpOnly cookies would require coordinated backend session changes.
 */
export const tokenService = {
  getAccessToken(): string | null {
    return storage.getString(auth.accessTokenKey);
  },

  getRefreshToken(): string | null {
    return storage.getString(auth.refreshTokenKey);
  },

  getTokenType(): string {
    return storage.getString(auth.tokenTypeKey) ?? 'Bearer';
  },

  getExpiresAt(): number | null {
    const raw = storage.getString(auth.expiresAtKey);
    if (!raw) return null;
    const value = Number(raw);
    return Number.isFinite(value) ? value : null;
  },

  getUser(): AuthUser | null {
    return storage.getJson<AuthUser>(auth.userKey);
  },

  setTokens(tokens: TokenPair): void {
    storage.setString(auth.accessTokenKey, tokens.accessToken);
    storage.setString(auth.refreshTokenKey, tokens.refreshToken);
    storage.setString(auth.tokenTypeKey, tokens.tokenType);
    const expiresAt = Date.now() + tokens.expiresIn * 1000;
    storage.setString(auth.expiresAtKey, String(expiresAt));
  },

  setUser(user: AuthUser): void {
    storage.setJson(auth.userKey, user);
  },

  setSession(user: AuthUser, tokens: TokenPair): void {
    tokenService.setUser(user);
    tokenService.setTokens(tokens);
  },

  clearSession(): void {
    storage.removeMany([
      auth.accessTokenKey,
      auth.refreshTokenKey,
      auth.tokenTypeKey,
      auth.expiresAtKey,
      auth.userKey,
    ]);
  },

  hasAccessToken(): boolean {
    return Boolean(tokenService.getAccessToken());
  },

  isAccessTokenExpired(skewMs = 30_000): boolean {
    const expiresAt = tokenService.getExpiresAt();
    if (!expiresAt) return !tokenService.hasAccessToken();
    return Date.now() >= expiresAt - skewMs;
  },
};
