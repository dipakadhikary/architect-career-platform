import { useQuery } from '@tanstack/react-query';
import { moduleConfig } from '@/app/config/module.config';
import { careerApi } from '@/features/career/api/career.api';
import { knowledgeApi } from '@/features/knowledge/api/knowledge.api';
import { learningPlansApi } from '@/features/learning/api/plans.api';
import { projectsApi } from '@/features/portfolio/api/portfolio.api';
import { dashboardApi } from '../api/dashboard.api';

export const dashboardKeys = {
  all: ['dashboard'] as const,
  summary: () => [...dashboardKeys.all, 'summary'] as const,
  overview: () => [...dashboardKeys.all, 'overview'] as const,
};

export function useDashboardMetrics() {
  return useQuery({
    queryKey: dashboardKeys.summary(),
    queryFn: () => dashboardApi.getDashboard(),
    refetchInterval: moduleConfig.query.refetchIntervalMs,
  });
}

/**
 * Aggregates cross-module data for rich dashboard widgets beyond the
 * placeholder metrics endpoint.
 */
export function useDashboardOverview() {
  const dashboard = useDashboardMetrics();

  const career = useQuery({
    queryKey: [...dashboardKeys.overview(), 'career'],
    queryFn: () => careerApi.getDashboard(),
    refetchInterval: moduleConfig.query.refetchIntervalMs,
  });

  const knowledge = useQuery({
    queryKey: [...dashboardKeys.overview(), 'knowledge'],
    queryFn: () => knowledgeApi.list({ page: 0, size: 5, sort: 'updatedAt,desc' }),
    refetchInterval: moduleConfig.query.refetchIntervalMs,
  });

  const learning = useQuery({
    queryKey: [...dashboardKeys.overview(), 'learning'],
    queryFn: () => learningPlansApi.list({ page: 0, size: 5, sort: 'updatedAt,desc' }),
    refetchInterval: moduleConfig.query.refetchIntervalMs,
  });

  const projects = useQuery({
    queryKey: [...dashboardKeys.overview(), 'projects'],
    queryFn: () => projectsApi.list({ page: 0, size: 5, sort: 'updatedAt,desc' }),
    refetchInterval: moduleConfig.query.refetchIntervalMs,
  });

  const applications = useQuery({
    queryKey: [...dashboardKeys.overview(), 'applications'],
    queryFn: () =>
      careerApi.listApplications({
        page: 0,
        size: 5,
        sort: 'updatedAt,desc',
        archived: false,
      }),
    refetchInterval: moduleConfig.query.refetchIntervalMs,
  });

  const isLoading =
    dashboard.isLoading ||
    career.isLoading ||
    knowledge.isLoading ||
    learning.isLoading ||
    projects.isLoading ||
    applications.isLoading;

  const isError =
    dashboard.isError ||
    career.isError ||
    knowledge.isError ||
    learning.isError ||
    projects.isError ||
    applications.isError;

  const error =
    dashboard.error ||
    career.error ||
    knowledge.error ||
    learning.error ||
    projects.error ||
    applications.error;

  const refetch = () => {
    void dashboard.refetch();
    void career.refetch();
    void knowledge.refetch();
    void learning.refetch();
    void projects.refetch();
    void applications.refetch();
  };

  return {
    metrics: dashboard.data,
    career: career.data,
    recentNotes: knowledge.data?.content ?? [],
    learningPlans: learning.data?.content ?? [],
    projects: projects.data?.content ?? [],
    applications: applications.data?.content ?? [],
    isLoading,
    isError,
    error,
    refetch,
  };
}
