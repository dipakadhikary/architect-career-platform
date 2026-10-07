import { env } from './env';

/**
 * Application-level configuration derived from environment and product defaults.
 * Aligned with ACOS backend at architect-career-operating-system (port 8080).
 */
export const appConfig = {
  name: env.appName,
  version: env.appVersion,
  api: {
    baseUrl: env.apiBaseUrl,
    timeoutMs: 30_000,
    correlationHeader: 'X-Correlation-Id',
  },
  auth: {
    accessTokenKey: 'acos.accessToken',
    refreshTokenKey: 'acos.refreshToken',
    tokenTypeKey: 'acos.tokenType',
    expiresAtKey: 'acos.expiresAt',
    userKey: 'acos.user',
    /** Paths that must not trigger refresh-token retry loops */
    publicAuthPaths: [
      '/api/v1/auth/login',
      '/api/v1/auth/register',
      '/api/v1/auth/refresh',
      '/api/v1/auth/forgot-user-id',
      '/api/v1/auth/forgot-password',
      '/api/v1/auth/reset-password',
    ] as const,
  },
  routes: {
    login: '/login',
    register: '/register',
    forgotUserId: '/forgot-user-id',
    forgotPassword: '/forgot-password',
    resetPassword: '/reset-password',
    home: '/',
    unauthorized: '/unauthorized',
  },
  query: {
    staleTimeMs: 60_000,
    retry: 1,
    enableDevtools: env.enableQueryDevtools,
  },
  layout: {
    drawerWidth: 260,
    collapsedDrawerWidth: 72,
  },
  /**
   * AI Platform feature toggle (frontend mirror of backend ai.platform.enabled).
   * When false, AI actions are disabled and unavailable messaging is shown.
   */
  ai: {
    enabled: env.aiPlatformEnabled,
    healthPath: '/api/v1/integration/ai/health',
    integrationBasePath: '/api/v1/integration/ai',
  },
} as const;

export type AppConfig = typeof appConfig;
