import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { achievementsApi } from '../api/portfolio.api';
import { portfolioKeys } from './portfolio.keys';
import type { AchievementRequest } from '../types/portfolio.types';

export function useAchievementsQuery() {
  return useQuery({
    queryKey: portfolioKeys.achievements(),
    queryFn: () => achievementsApi.list(),
  });
}

export function useAchievementMutations() {
  const queryClient = useQueryClient();

  const invalidate = () =>
    queryClient.invalidateQueries({ queryKey: portfolioKeys.achievements() });

  const createMutation = useMutation({
    mutationFn: (payload: AchievementRequest) => achievementsApi.create(payload),
    onSuccess: invalidate,
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: AchievementRequest }) =>
      achievementsApi.update(id, payload),
    onSuccess: invalidate,
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => achievementsApi.remove(id),
    onSuccess: invalidate,
  });

  return { createMutation, updateMutation, deleteMutation };
}
