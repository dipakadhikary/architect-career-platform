import { useCallback } from 'react';
import { useAuthStore } from '@/features/auth/store/auth.store';
import { authApi } from '@/features/auth/api/auth.api';
import { tokenService } from '@/shared/api/token.service';
import type { AuthenticationResult, LoginRequest } from '@/features/auth/types/auth.types';

/**
 * Auth session helpers for layout / route guards.
 * Feature screens should call authApi + this hook when implemented.
 */
export function useAuth() {
  const user = useAuthStore((s) => s.user);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const isHydrated = useAuthStore((s) => s.isHydrated);
  const setSession = useAuthStore((s) => s.setSession);
  const clearSession = useAuthStore((s) => s.clearSession);
  const hydrate = useAuthStore((s) => s.hydrate);

  const establishSession = useCallback(
    (result: AuthenticationResult) => {
      setSession(result.user, result.tokens);
    },
    [setSession],
  );

  const login = useCallback(
    async (payload: LoginRequest) => {
      const result = await authApi.login(payload);
      establishSession(result);
      return result;
    },
    [establishSession],
  );

  const logout = useCallback(async () => {
    const refreshToken = tokenService.getRefreshToken();
    try {
      if (refreshToken) {
        await authApi.logout({ refreshToken });
      }
    } catch {
      // Clear local session even if the server logout fails.
    } finally {
      clearSession();
    }
  }, [clearSession]);

  return {
    user,
    isAuthenticated,
    isHydrated,
    hydrate,
    login,
    logout,
    establishSession,
    clearSession,
  };
}
