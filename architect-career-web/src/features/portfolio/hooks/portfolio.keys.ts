import type { ProjectListParams, ProjectSearchParams } from '../types/portfolio.types';

export const portfolioKeys = {
  all: ['portfolio'] as const,
  projects: () => [...portfolioKeys.all, 'projects'] as const,
  projectList: (params: ProjectListParams) =>
    [...portfolioKeys.projects(), 'list', params] as const,
  projectSearch: (params: ProjectSearchParams) =>
    [...portfolioKeys.projects(), 'search', params] as const,
  projectDetail: (projectId: string) => [...portfolioKeys.projects(), projectId] as const,
  technologies: () => [...portfolioKeys.all, 'technologies'] as const,
  skills: () => [...portfolioKeys.all, 'skills'] as const,
  certifications: () => [...portfolioKeys.all, 'certifications'] as const,
  achievements: () => [...portfolioKeys.all, 'achievements'] as const,
};
