import { z } from 'zod';

const booleanFlag = z
  .union([z.literal('true'), z.literal('false'), z.literal('')])
  .optional()
  .default('false')
  .transform((value) => value === 'true');

const envSchema = z.object({
  VITE_APP_NAME: z.string().min(1).default('ACOS'),
  VITE_APP_VERSION: z.string().min(1).default('0.1.0'),
  VITE_API_BASE_URL: z.string().optional().default(''),
  VITE_API_PROXY_TARGET: z.string().optional().default('http://localhost:8080'),
  VITE_ENABLE_QUERY_DEVTOOLS: booleanFlag,
  /** Mirrors backend ai.platform.enabled — gates AI UX and capability actions. */
  VITE_AI_PLATFORM_ENABLED: booleanFlag,
});

const parsed = envSchema.safeParse({
  VITE_APP_NAME: import.meta.env.VITE_APP_NAME,
  VITE_APP_VERSION: import.meta.env.VITE_APP_VERSION,
  VITE_API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
  VITE_API_PROXY_TARGET: import.meta.env.VITE_API_PROXY_TARGET,
  VITE_ENABLE_QUERY_DEVTOOLS: import.meta.env.VITE_ENABLE_QUERY_DEVTOOLS,
  VITE_AI_PLATFORM_ENABLED: import.meta.env.VITE_AI_PLATFORM_ENABLED,
});

if (!parsed.success) {
  console.error('Invalid environment configuration', parsed.error.flatten().fieldErrors);
  throw new Error('Invalid environment configuration');
}

export const env = {
  appName: parsed.data.VITE_APP_NAME,
  appVersion: parsed.data.VITE_APP_VERSION,
  apiBaseUrl: parsed.data.VITE_API_BASE_URL,
  apiProxyTarget: parsed.data.VITE_API_PROXY_TARGET,
  enableQueryDevtools: parsed.data.VITE_ENABLE_QUERY_DEVTOOLS,
  aiPlatformEnabled: parsed.data.VITE_AI_PLATFORM_ENABLED,
} as const;
