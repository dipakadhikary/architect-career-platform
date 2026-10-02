import type { AxiosError, AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import axios from 'axios';
import { appConfig } from '@/app/config/app.config';
import { createCorrelationId } from '@/shared/utils/correlation';
import { tokenService } from './token.service';
import { ApiClientError, type ApiResponse } from './types';

type RetriableConfig = InternalAxiosRequestConfig & { _retry?: boolean };

let refreshPromise: Promise<string | null> | null = null;

function isPublicAuthPath(url?: string): boolean {
  if (!url) return false;
  return appConfig.auth.publicAuthPaths.some((path) => url.includes(path));
}

async function refreshAccessToken(): Promise<string | null> {
  const refreshToken = tokenService.getRefreshToken();
  if (!refreshToken) {
    tokenService.clearSession();
    return null;
  }

  try {
    const response = await axios.post<
      ApiResponse<{
        accessToken: string;
        refreshToken: string;
        tokenType: string;
        expiresIn: number;
      }>
    >(
      `${appConfig.api.baseUrl}/api/v1/auth/refresh`,
      { refreshToken },
      {
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
          [appConfig.api.correlationHeader]: createCorrelationId(),
        },
      },
    );

    const payload = response.data.data;
    if (!response.data.success || !payload) {
      tokenService.clearSession();
      return null;
    }

    tokenService.setTokens(payload);
    return payload.accessToken;
  } catch {
    tokenService.clearSession();
    return null;
  }
}

function toApiClientError(error: AxiosError<ApiResponse<unknown>>): ApiClientError {
  const status = error.response?.status ?? 0;
  const body = error.response?.data;
  const apiError = body?.error;

  return new ApiClientError(apiError?.message ?? error.message ?? 'Unexpected API error', {
    code: apiError?.code ?? (status === 0 ? 'NETWORK_ERROR' : 'INTERNAL_ERROR'),
    status,
    details: apiError?.details ?? [],
    correlationId: body?.correlationId,
    cause: error,
  });
}

export function attachInterceptors(client: AxiosInstance): void {
  client.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    const headers = config.headers;

    if (!headers[appConfig.api.correlationHeader]) {
      headers[appConfig.api.correlationHeader] = createCorrelationId();
    }

    if (!isPublicAuthPath(config.url)) {
      const accessToken = tokenService.getAccessToken();
      if (accessToken) {
        headers.Authorization = `${tokenService.getTokenType()} ${accessToken}`;
      }
    }

    return config;
  });

  client.interceptors.response.use(
    (response: AxiosResponse) => response,
    async (error: AxiosError<ApiResponse<unknown>>) => {
      const original = error.config as RetriableConfig | undefined;
      const status = error.response?.status;

      if (status === 401 && original && !original._retry && !isPublicAuthPath(original.url)) {
        original._retry = true;

        refreshPromise ??= refreshAccessToken().finally(() => {
          refreshPromise = null;
        });

        const newAccessToken = await refreshPromise;
        if (newAccessToken) {
          original.headers.Authorization = `${tokenService.getTokenType()} ${newAccessToken}`;
          return client(original);
        }

        window.dispatchEvent(new CustomEvent('acos:session-expired'));
      }

      return Promise.reject(toApiClientError(error));
    },
  );
}
