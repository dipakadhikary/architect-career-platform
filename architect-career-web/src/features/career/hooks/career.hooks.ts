import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { moduleConfig } from '@/app/config/module.config';
import { careerApi } from '@/features/career/api/career.api';
import { careerQueryKeys } from '@/features/career/api/queryKeys';
import type {
  ApplicationSearchFilters,
  ApplicationStatusUpdateRequest,
  CompanyRequest,
  InterviewRequest,
  JobApplicationRequest,
  OfferRequest,
  RecruiterRequest,
} from '@/features/career/types/career.types';
import type { PageParams } from '@/shared/types/pagination';

const staleTime = moduleConfig.query.refetchIntervalMs;

export function useCareerDashboard() {
  return useQuery({
    queryKey: careerQueryKeys.dashboard(),
    queryFn: () => careerApi.getDashboard(),
    staleTime,
  });
}

export function useCompanies() {
  return useQuery({
    queryKey: careerQueryKeys.companies(),
    queryFn: () => careerApi.listCompanies(),
    staleTime,
  });
}

export function useCompanyMutations() {
  const queryClient = useQueryClient();

  const invalidate = () => {
    void queryClient.invalidateQueries({ queryKey: careerQueryKeys.companies() });
    void queryClient.invalidateQueries({ queryKey: careerQueryKeys.dashboard() });
  };

  const create = useMutation({
    mutationFn: (payload: CompanyRequest) => careerApi.createCompany(payload),
    onSuccess: invalidate,
  });

  const update = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: CompanyRequest }) =>
      careerApi.updateCompany(id, payload),
    onSuccess: invalidate,
  });

  const remove = useMutation({
    mutationFn: (id: string) => careerApi.deleteCompany(id),
    onSuccess: invalidate,
  });

  return { create, update, remove };
}

export function useRecruiters() {
  return useQuery({
    queryKey: careerQueryKeys.recruiters(),
    queryFn: () => careerApi.listRecruiters(),
    staleTime,
  });
}

export function useRecruiterMutations() {
  const queryClient = useQueryClient();

  const invalidate = () => {
    void queryClient.invalidateQueries({ queryKey: careerQueryKeys.recruiters() });
    void queryClient.invalidateQueries({ queryKey: careerQueryKeys.dashboard() });
  };

  const create = useMutation({
    mutationFn: (payload: RecruiterRequest) => careerApi.createRecruiter(payload),
    onSuccess: invalidate,
  });

  const update = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: RecruiterRequest }) =>
      careerApi.updateRecruiter(id, payload),
    onSuccess: invalidate,
  });

  const remove = useMutation({
    mutationFn: (id: string) => careerApi.deleteRecruiter(id),
    onSuccess: invalidate,
  });

  return { create, update, remove };
}

export function useApplicationsSearch(filters: ApplicationSearchFilters & PageParams) {
  return useQuery({
    queryKey: careerQueryKeys.applicationsSearch(filters),
    queryFn: () => careerApi.searchApplications(filters),
    staleTime,
  });
}

export function useApplication(id: string) {
  return useQuery({
    queryKey: careerQueryKeys.application(id),
    queryFn: () => careerApi.getApplication(id),
    enabled: Boolean(id),
    staleTime,
  });
}

export function useApplicationMutations() {
  const queryClient = useQueryClient();

  const invalidateApplications = () => {
    void queryClient.invalidateQueries({ queryKey: careerQueryKeys.applications() });
    void queryClient.invalidateQueries({ queryKey: careerQueryKeys.dashboard() });
  };

  const create = useMutation({
    mutationFn: (payload: JobApplicationRequest) => careerApi.createApplication(payload),
    onSuccess: invalidateApplications,
  });

  const update = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: JobApplicationRequest }) =>
      careerApi.updateApplication(id, payload),
    onSuccess: (_, { id }) => {
      invalidateApplications();
      void queryClient.invalidateQueries({ queryKey: careerQueryKeys.application(id) });
    },
  });

  const archive = useMutation({
    mutationFn: (id: string) => careerApi.archiveApplication(id),
    onSuccess: invalidateApplications,
  });

  const updateStatus = useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: ApplicationStatusUpdateRequest }) =>
      careerApi.updateApplicationStatus(id, payload),
    onSuccess: (_, { id }) => {
      invalidateApplications();
      void queryClient.invalidateQueries({ queryKey: careerQueryKeys.application(id) });
      void queryClient.invalidateQueries({ queryKey: careerQueryKeys.applicationHistory(id) });
      void queryClient.invalidateQueries({ queryKey: careerQueryKeys.applicationTimeline(id) });
    },
  });

  return { create, update, archive, updateStatus };
}

export function useApplicationHistory(id: string) {
  return useQuery({
    queryKey: careerQueryKeys.applicationHistory(id),
    queryFn: () => careerApi.getApplicationHistory(id),
    enabled: Boolean(id),
    staleTime,
  });
}

export function useApplicationTimeline(id: string) {
  return useQuery({
    queryKey: careerQueryKeys.applicationTimeline(id),
    queryFn: () => careerApi.getApplicationTimeline(id),
    enabled: Boolean(id),
    staleTime,
  });
}

export function useInterviews(applicationId: string) {
  return useQuery({
    queryKey: careerQueryKeys.interviews(applicationId),
    queryFn: () => careerApi.listInterviews(applicationId),
    enabled: Boolean(applicationId),
    staleTime,
  });
}

export function useInterviewMutations(applicationId: string) {
  const queryClient = useQueryClient();

  const invalidate = () => {
    void queryClient.invalidateQueries({ queryKey: careerQueryKeys.interviews(applicationId) });
    void queryClient.invalidateQueries({ queryKey: careerQueryKeys.dashboard() });
  };

  const create = useMutation({
    mutationFn: (payload: InterviewRequest) => careerApi.createInterview(applicationId, payload),
    onSuccess: invalidate,
  });

  const update = useMutation({
    mutationFn: ({ interviewId, payload }: { interviewId: string; payload: InterviewRequest }) =>
      careerApi.updateInterview(applicationId, interviewId, payload),
    onSuccess: invalidate,
  });

  const remove = useMutation({
    mutationFn: (interviewId: string) => careerApi.deleteInterview(applicationId, interviewId),
    onSuccess: invalidate,
  });

  return { create, update, remove };
}

export function useOffers(applicationId: string) {
  return useQuery({
    queryKey: careerQueryKeys.offers(applicationId),
    queryFn: () => careerApi.listOffers(applicationId),
    enabled: Boolean(applicationId),
    staleTime,
  });
}

export function useOfferMutations(applicationId: string) {
  const queryClient = useQueryClient();

  const invalidate = () => {
    void queryClient.invalidateQueries({ queryKey: careerQueryKeys.offers(applicationId) });
    void queryClient.invalidateQueries({ queryKey: careerQueryKeys.dashboard() });
  };

  const create = useMutation({
    mutationFn: (payload: OfferRequest) => careerApi.createOffer(applicationId, payload),
    onSuccess: invalidate,
  });

  const update = useMutation({
    mutationFn: ({ offerId, payload }: { offerId: string; payload: OfferRequest }) =>
      careerApi.updateOffer(applicationId, offerId, payload),
    onSuccess: invalidate,
  });

  const remove = useMutation({
    mutationFn: (offerId: string) => careerApi.deleteOffer(applicationId, offerId),
    onSuccess: invalidate,
  });

  return { create, update, remove };
}
