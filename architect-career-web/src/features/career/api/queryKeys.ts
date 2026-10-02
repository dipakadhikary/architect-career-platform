import type { ApplicationSearchFilters } from '@/features/career/types/career.types';
import type { PageParams } from '@/shared/types/pagination';

export const careerQueryKeys = {
  all: ['career'] as const,
  dashboard: () => [...careerQueryKeys.all, 'dashboard'] as const,
  companies: () => [...careerQueryKeys.all, 'companies'] as const,
  company: (id: string) => [...careerQueryKeys.companies(), id] as const,
  recruiters: () => [...careerQueryKeys.all, 'recruiters'] as const,
  recruiter: (id: string) => [...careerQueryKeys.recruiters(), id] as const,
  applications: () => [...careerQueryKeys.all, 'applications'] as const,
  applicationsList: (params: PageParams & { archived?: boolean }) =>
    [...careerQueryKeys.applications(), 'list', params] as const,
  applicationsSearch: (filters: ApplicationSearchFilters & PageParams) =>
    [...careerQueryKeys.applications(), 'search', filters] as const,
  applicationsArchived: (params: PageParams) =>
    [...careerQueryKeys.applications(), 'archived', params] as const,
  application: (id: string) => [...careerQueryKeys.applications(), id] as const,
  applicationHistory: (id: string) => [...careerQueryKeys.application(id), 'history'] as const,
  applicationTimeline: (id: string) => [...careerQueryKeys.application(id), 'timeline'] as const,
  interviews: (applicationId: string) =>
    [...careerQueryKeys.application(applicationId), 'interviews'] as const,
  interview: (applicationId: string, interviewId: string) =>
    [...careerQueryKeys.interviews(applicationId), interviewId] as const,
  offers: (applicationId: string) =>
    [...careerQueryKeys.application(applicationId), 'offers'] as const,
  offer: (applicationId: string, offerId: string) =>
    [...careerQueryKeys.offers(applicationId), offerId] as const,
};
