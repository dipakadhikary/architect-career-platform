import { useQuery } from '@tanstack/react-query';
import { moduleConfig } from '@/app/config/module.config';
import { aiApi } from '../api/ai.api';
import { isAiFeatureToggleEnabled, isAiOperational } from '../services/aiAvailability';

export const aiQueryKeys = {
  all: ['ai'] as const,
  health: () => [...aiQueryKeys.all, 'health'] as const,
};

export function useAiHealth() {
  const toggleEnabled = isAiFeatureToggleEnabled();

  const query = useQuery({
    queryKey: aiQueryKeys.health(),
    queryFn: () => aiApi.timedHealth(),
    enabled: toggleEnabled,
    refetchInterval: moduleConfig.ai.healthRefetchIntervalMs,
    retry: 1,
  });

  const health = query.data?.health ?? null;
  const latencyMs = query.data?.latencyMs ?? null;
  const operational = isAiOperational(health);

  return {
    toggleEnabled,
    health,
    latencyMs,
    operational,
    isLoading: toggleEnabled && query.isLoading,
    isFetching: query.isFetching,
    isError: query.isError,
    error: query.error,
    refetch: query.refetch,
  };
}

export function useAiAvailability() {
  const healthState = useAiHealth();
  return {
    ...healthState,
    canInvoke: healthState.toggleEnabled && healthState.operational,
    unavailableReason: !healthState.toggleEnabled
      ? 'AI Platform is currently unavailable.'
      : healthState.health?.status === 'UNAVAILABLE'
        ? healthState.health.message || 'AI Platform is currently unavailable.'
        : healthState.isError
          ? 'Unable to verify AI Platform health.'
          : null,
  };
}
