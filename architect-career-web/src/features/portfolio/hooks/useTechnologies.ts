import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { technologiesApi } from '../api/portfolio.api';
import { portfolioKeys } from './portfolio.keys';
import type { TechnologyRequest } from '../types/portfolio.types';

export function useTechnologiesQuery() {
  return useQuery({
    queryKey: portfolioKeys.technologies(),
    queryFn: () => technologiesApi.list(),
  });
}

export function useTechnologyMutations() {
  const queryClient = useQueryClient();

  const invalidate = () =>
    queryClient.invalidateQueries({ queryKey: portfolioKeys.technologies() });

  const createMutation = useMutation({
    mutationFn: (payload: TechnologyRequest) => technologiesApi.create(payload),
    onSuccess: invalidate,
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: TechnologyRequest }) =>
      technologiesApi.update(id, payload),
    onSuccess: invalidate,
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => technologiesApi.remove(id),
    onSuccess: invalidate,
  });

  return { createMutation, updateMutation, deleteMutation };
}
