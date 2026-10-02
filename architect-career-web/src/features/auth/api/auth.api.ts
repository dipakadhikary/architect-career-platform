import { apiClient } from '@/shared/api/axios.instance';
import { assertApiSuccess, unwrapApiResponse } from '@/shared/api/unwrap';
import type { ApiResponse } from '@/shared/api/types';
import type {
  AuthenticationResult,
  AuthUser,
  LoginRequest,
  LogoutRequest,
  RefreshRequest,
  RegisterRequest,
  RegisterResult,
  TokenPair,
} from '../types/auth.types';

const AUTH_BASE = '/api/v1/auth';

/**
 * Auth API client foundation — endpoints only, no UI wiring.
 */
export const authApi = {
  async login(payload: LoginRequest): Promise<AuthenticationResult> {
    const response = await apiClient.post<ApiResponse<AuthenticationResult>>(
      `${AUTH_BASE}/login`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async register(payload: RegisterRequest): Promise<RegisterResult> {
    const response = await apiClient.post<ApiResponse<RegisterResult>>(
      `${AUTH_BASE}/register`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async refresh(payload: RefreshRequest): Promise<TokenPair> {
    const response = await apiClient.post<ApiResponse<TokenPair>>(`${AUTH_BASE}/refresh`, payload);
    return unwrapApiResponse(response);
  },

  async logout(payload: LogoutRequest): Promise<void> {
    const response = await apiClient.post<ApiResponse<null>>(`${AUTH_BASE}/logout`, payload);
    assertApiSuccess(response);
  },

  async me(): Promise<AuthUser> {
    const response = await apiClient.get<ApiResponse<AuthUser>>(`${AUTH_BASE}/me`);
    return unwrapApiResponse(response);
  },
};
