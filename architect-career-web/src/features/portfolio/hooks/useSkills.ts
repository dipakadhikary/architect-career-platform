import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { skillsApi } from '../api/portfolio.api';
import { portfolioKeys } from './portfolio.keys';
import type { SkillRequest } from '../types/portfolio.types';

export function useSkillsQuery() {
  return useQuery({
    queryKey: portfolioKeys.skills(),
    queryFn: () => skillsApi.list(),
  });
}

export function useSkillMutations() {
  const queryClient = useQueryClient();

  const invalidate = () => queryClient.invalidateQueries({ queryKey: portfolioKeys.skills() });

  const createMutation = useMutation({
    mutationFn: (payload: SkillRequest) => skillsApi.create(payload),
    onSuccess: invalidate,
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: SkillRequest }) =>
      skillsApi.update(id, payload),
    onSuccess: invalidate,
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => skillsApi.remove(id),
    onSuccess: invalidate,
  });

  return { createMutation, updateMutation, deleteMutation };
}
