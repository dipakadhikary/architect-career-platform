import { ApiClientError } from '@/shared/api/types';
import type { AiErrorCode, AiPlatformHealth, AiPlatformHealthStatus } from '../types/ai.types';
import { appConfig } from '@/app/config/app.config';

const USER_MESSAGES: Record<AiErrorCode, string> = {
  AI_PLATFORM_UNAVAILABLE: 'AI Platform is currently unavailable. Please try again later.',
  AI_PLATFORM_ERROR: 'The AI service could not complete this request. Please try again.',
  AI_TIMEOUT: 'The AI request timed out. Please try again.',
  AI_AUTHENTICATION_FAILED: 'AI authentication failed. Please sign in again.',
  AI_VALIDATION_FAILED: 'Please review your input and try again.',
  AI_RATE_LIMITED: 'Too many AI requests. Please wait a moment and retry.',
};

export function isAiErrorCode(code: string): code is AiErrorCode {
  return code in USER_MESSAGES;
}

export function getAiUserMessage(error: unknown, fallback?: string): string {
  if (error instanceof ApiClientError) {
    if (isAiErrorCode(error.code)) {
      return USER_MESSAGES[error.code];
    }
    if (error.status === 404 || error.status === 501) {
      return 'This AI capability is not available on the Business Platform yet.';
    }
    if (error.status === 503) {
      return USER_MESSAGES.AI_PLATFORM_UNAVAILABLE;
    }
    if (error.message && !/exception|stack|nullpointer/i.test(error.message)) {
      return error.message;
    }
  }
  return fallback ?? 'AI Platform is currently unavailable.';
}

export function isAiFeatureToggleEnabled(): boolean {
  return appConfig.ai.enabled;
}

export function isAiOperational(health?: AiPlatformHealth | null): boolean {
  if (!isAiFeatureToggleEnabled()) return false;
  if (!health) return false;
  return health.enabled && (health.status === 'AVAILABLE' || health.status === 'DEGRADED');
}

export function describeHealthStatus(status: AiPlatformHealthStatus): string {
  switch (status) {
    case 'AVAILABLE':
      return 'Operational';
    case 'DEGRADED':
      return 'Degraded';
    case 'UNAVAILABLE':
    default:
      return 'Unavailable';
  }
}
