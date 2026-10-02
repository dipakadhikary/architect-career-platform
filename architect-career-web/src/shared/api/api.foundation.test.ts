import { describe, expect, it } from 'vitest';
import { loginSchema, registerSchema } from '@/features/auth/schemas/auth.schemas';
import { unwrapApiResponse, assertApiSuccess } from '@/shared/api/unwrap';
import type { AxiosResponse } from 'axios';
import type { ApiResponse } from '@/shared/api/types';
import {
  isAiFeatureToggleEnabled,
  getAiUserMessage,
  isAiOperational,
} from '@/features/ai/services/aiAvailability';
import { ApiClientError } from '@/shared/api/types';
import type { AiPlatformHealth } from '@/features/ai/types/ai.types';

function mockResponse<T>(body: ApiResponse<T>, status = 200): AxiosResponse<ApiResponse<T>> {
  return {
    data: body,
    status,
    statusText: 'OK',
    headers: {},
    config: { headers: {} },
  } as AxiosResponse<ApiResponse<T>>;
}

describe('auth schemas', () => {
  it('accepts valid login payloads', () => {
    const parsed = loginSchema.safeParse({
      email: 'ada@acos.local',
      password: 'secret',
    });
    expect(parsed.success).toBe(true);
  });

  it('rejects weak register passwords', () => {
    const parsed = registerSchema.safeParse({
      email: 'ada@acos.local',
      firstName: 'Ada',
      lastName: 'Lovelace',
      password: 'short',
      confirmPassword: 'short',
    });
    expect(parsed.success).toBe(false);
  });
});

describe('unwrapApiResponse', () => {
  it('returns data on success', () => {
    const data = unwrapApiResponse(
      mockResponse({
        success: true,
        data: { id: '1' },
        error: null,
        correlationId: 'c1',
        timestamp: new Date().toISOString(),
      }),
    );
    expect(data).toEqual({ id: '1' });
  });

  it('throws ApiClientError on failure', () => {
    expect(() =>
      unwrapApiResponse(
        mockResponse({
          success: false,
          data: null,
          error: { code: 'UNAUTHORIZED', message: 'Nope', details: [] },
          correlationId: 'c1',
          timestamp: new Date().toISOString(),
        }),
      ),
    ).toThrow(ApiClientError);
  });
});

describe('assertApiSuccess', () => {
  it('allows null payloads', () => {
    expect(() =>
      assertApiSuccess(
        mockResponse({
          success: true,
          data: null,
          error: null,
          correlationId: 'c1',
          timestamp: new Date().toISOString(),
        }),
      ),
    ).not.toThrow();
  });
});

describe('AI feature toggles', () => {
  it('exposes toggle helper', () => {
    expect(typeof isAiFeatureToggleEnabled()).toBe('boolean');
  });

  it('maps AI unavailable errors to friendly copy', () => {
    const message = getAiUserMessage(
      new ApiClientError('raw', {
        code: 'AI_PLATFORM_UNAVAILABLE',
        status: 503,
      }),
    );
    expect(message).toMatch(/unavailable/i);
  });

  it('requires enabled health to be operational', () => {
    const health: AiPlatformHealth = {
      status: 'AVAILABLE',
      message: 'ok',
      checkedAt: new Date().toISOString(),
      enabled: true,
    };
    // When frontend toggle is false (default in .env.development), operational is false.
    if (!isAiFeatureToggleEnabled()) {
      expect(isAiOperational(health)).toBe(false);
    }
  });
});
