import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { moduleConfig } from '@/app/config/module.config';
import { projectsApi } from '../api/portfolio.api';
import { portfolioKeys } from './portfolio.keys';
import type {
  PortfolioProjectRequest,
  ProjectListParams,
  ProjectSearchParams,
  ProjectStatus,
} from '../types/portfolio.types';

const defaultPageSize = moduleConfig.portfolio.defaultPageSize;

export function useProjectsQuery(params: {
  page: number;
  pageSize?: number;
  sort?: string;
  search?: string;
  statusFilter?: ProjectStatus | '';
}) {
  const pageSize = params.pageSize ?? defaultPageSize;
  const listParams: ProjectListParams = {
    page: params.page,
    size: pageSize,
    sort: params.sort ?? 'createdAt,desc',
  };
  const searchParams: ProjectSearchParams = {
    ...listParams,
    q: params.search ?? '',
  };

  const isSearching = Boolean(params.search?.trim());

  return useQuery({
    queryKey: isSearching
      ? portfolioKeys.projectSearch(searchParams)
      : portfolioKeys.projectList(listParams),
    queryFn: () => (isSearching ? projectsApi.search(searchParams) : projectsApi.list(listParams)),
  });
}

export function useProjectQuery(projectId: string | undefined) {
  return useQuery({
    queryKey: portfolioKeys.projectDetail(projectId ?? ''),
    queryFn: () => projectsApi.get(projectId!),
    enabled: Boolean(projectId),
  });
}

export function useProjectMutations() {
  const queryClient = useQueryClient();

  const invalidateProjects = () =>
    queryClient.invalidateQueries({ queryKey: portfolioKeys.projects() });

  const createMutation = useMutation({
    mutationFn: (payload: PortfolioProjectRequest) => projectsApi.create(payload),
    onSuccess: invalidateProjects,
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: PortfolioProjectRequest }) =>
      projectsApi.update(id, payload),
    onSuccess: (_, { id }) => {
      invalidateProjects();
      queryClient.invalidateQueries({ queryKey: portfolioKeys.projectDetail(id) });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => projectsApi.remove(id),
    onSuccess: invalidateProjects,
  });

  return { createMutation, updateMutation, deleteMutation };
}
