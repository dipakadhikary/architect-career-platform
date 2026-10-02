import { beforeEach, describe, expect, it } from 'vitest';
import { tokenService } from './token.service';
import type { AuthUser, TokenPair } from '@/features/auth/types/auth.types';

const user: AuthUser = {
  id: '3fa85f64-5717-4562-b3fc-2c963f66afa6',
  email: 'ada@acos.local',
  firstName: 'Ada',
  lastName: 'Lovelace',
  enabled: true,
  roles: ['USER'],
};

const tokens: TokenPair = {
  accessToken: 'access-token',
  refreshToken: 'refresh-token',
  tokenType: 'Bearer',
  expiresIn: 900,
};

describe('tokenService', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('persists and reads a session', () => {
    tokenService.setSession(user, tokens);

    expect(tokenService.getAccessToken()).toBe('access-token');
    expect(tokenService.getRefreshToken()).toBe('refresh-token');
    expect(tokenService.getUser()?.email).toBe('ada@acos.local');
    expect(tokenService.hasAccessToken()).toBe(true);
  });

  it('clears the session', () => {
    tokenService.setSession(user, tokens);
    tokenService.clearSession();

    expect(tokenService.getAccessToken()).toBeNull();
    expect(tokenService.getUser()).toBeNull();
    expect(tokenService.hasAccessToken()).toBe(false);
  });
});
