import { beforeEach, describe, expect, it, vi } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useDebouncedValue } from './useDebouncedValue';
import { useAuthStore } from '@/features/auth/store/auth.store';
import type { AuthUser, TokenPair } from '@/features/auth/types/auth.types';

describe('useDebouncedValue', () => {
  it('updates after the delay', async () => {
    vi.useFakeTimers();
    const { result, rerender } = renderHook(({ value }) => useDebouncedValue(value, 300), {
      initialProps: { value: 'a' },
    });
    expect(result.current).toBe('a');
    rerender({ value: 'ab' });
    expect(result.current).toBe('a');
    await act(async () => {
      vi.advanceTimersByTime(300);
    });
    expect(result.current).toBe('ab');
    vi.useRealTimers();
  });
});

describe('useAuthStore', () => {
  const user: AuthUser = {
    id: '1',
    email: 'ada@acos.local',
    firstName: 'Ada',
    lastName: 'Lovelace',
    enabled: true,
    roles: ['USER'],
  };

  const tokens: TokenPair = {
    accessToken: 'access',
    refreshToken: 'refresh',
    tokenType: 'Bearer',
    expiresIn: 900,
  };

  beforeEach(() => {
    localStorage.clear();
    useAuthStore.setState({
      user: null,
      isAuthenticated: false,
      isHydrated: false,
    });
  });

  it('establishes and clears a session', () => {
    act(() => {
      useAuthStore.getState().setSession(user, tokens);
    });
    expect(useAuthStore.getState().isAuthenticated).toBe(true);
    expect(useAuthStore.getState().user?.email).toBe('ada@acos.local');

    act(() => {
      useAuthStore.getState().clearSession();
    });
    expect(useAuthStore.getState().isAuthenticated).toBe(false);
    expect(useAuthStore.getState().user).toBeNull();
  });
});
