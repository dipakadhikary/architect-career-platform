import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { certificationsApi } from '../api/portfolio.api';
import { portfolioKeys } from './portfolio.keys';
import type { CertificationRequest } from '../types/portfolio.types';

export function useCertificationsQuery() {
  return useQuery({
    queryKey: portfolioKeys.certifications(),
    queryFn: () => certificationsApi.list(),
  });
}

export function useCertificationMutations() {
  const queryClient = useQueryClient();

  const invalidate = () =>
    queryClient.invalidateQueries({ queryKey: portfolioKeys.certifications() });

  const createMutation = useMutation({
    mutationFn: (payload: CertificationRequest) => certificationsApi.create(payload),
    onSuccess: invalidate,
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: CertificationRequest }) =>
      certificationsApi.update(id, payload),
    onSuccess: invalidate,
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => certificationsApi.remove(id),
    onSuccess: invalidate,
  });

  return { createMutation, updateMutation, deleteMutation };
}
