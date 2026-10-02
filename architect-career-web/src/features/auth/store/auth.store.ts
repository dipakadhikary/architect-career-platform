import { create } from 'zustand';
import { tokenService } from '@/shared/api/token.service';
import type { AuthUser, TokenPair } from '../types/auth.types';

interface AuthState {
  user: AuthUser | null;
  isAuthenticated: boolean;
  isHydrated: boolean;
  hydrate: () => void;
  setSession: (user: AuthUser, tokens: TokenPair) => void;
  setUser: (user: AuthUser) => void;
  setTokens: (tokens: TokenPair) => void;
  clearSession: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: false,
  isHydrated: false,

  hydrate: () => {
    const user = tokenService.getUser();
    const hasToken = tokenService.hasAccessToken() || Boolean(tokenService.getRefreshToken());
    set({
      user,
      isAuthenticated: Boolean(user && hasToken),
      isHydrated: true,
    });
  },

  setSession: (user, tokens) => {
    tokenService.setSession(user, tokens);
    set({ user, isAuthenticated: true, isHydrated: true });
  },

  setUser: (user) => {
    tokenService.setUser(user);
    set({ user, isAuthenticated: true });
  },

  setTokens: (tokens) => {
    tokenService.setTokens(tokens);
    set((state) => ({
      isAuthenticated: Boolean(state.user),
    }));
  },

  clearSession: () => {
    tokenService.clearSession();
    set({ user: null, isAuthenticated: false, isHydrated: true });
  },
}));
