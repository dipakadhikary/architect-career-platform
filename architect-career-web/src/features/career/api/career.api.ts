import { apiClient } from '@/shared/api/axios.instance';
import { assertApiSuccess, unwrapApiResponse } from '@/shared/api/unwrap';
import { toPageQuery } from '@/shared/api/pageParams';
import type { ApiResponse } from '@/shared/api/types';
import type { PageParams, PageResponse } from '@/shared/types/pagination';
import type {
  ApplicationSearchFilters,
  ApplicationStatusHistoryResponse,
  ApplicationStatusUpdateRequest,
  ApplicationTimelineResponse,
  CareerDashboardResponse,
  CompanyRequest,
  CompanyResponse,
  InterviewRequest,
  InterviewResponse,
  JobApplicationRequest,
  JobApplicationResponse,
  OfferRequest,
  OfferResponse,
  RecruiterRequest,
  RecruiterResponse,
} from '@/features/career/types/career.types';

const CAREER_BASE = '/api/v1/career';

function buildSearchQuery(
  filters: ApplicationSearchFilters & PageParams,
): Record<string, string | number> {
  const query: Record<string, string | number> = { ...toPageQuery(filters) };

  if (filters.companyId) query.companyId = filters.companyId;
  if (filters.recruiterId) query.recruiterId = filters.recruiterId;
  if (filters.status) query.status = filters.status;
  if (filters.interviewRound) query.interviewRound = filters.interviewRound;
  if (filters.appliedFrom) query.appliedFrom = filters.appliedFrom;
  if (filters.appliedTo) query.appliedTo = filters.appliedTo;
  if (filters.salaryMin !== undefined) query.salaryMin = filters.salaryMin;
  if (filters.salaryMax !== undefined) query.salaryMax = filters.salaryMax;
  if (filters.keyword) query.keyword = filters.keyword;

  return query;
}

export const careerApi = {
  getDashboard: async (): Promise<CareerDashboardResponse> => {
    const response = await apiClient.get<ApiResponse<CareerDashboardResponse>>(
      `${CAREER_BASE}/dashboard`,
    );
    return unwrapApiResponse(response);
  },

  listCompanies: async (): Promise<CompanyResponse[]> => {
    const response = await apiClient.get<ApiResponse<CompanyResponse[]>>(
      `${CAREER_BASE}/companies`,
    );
    return unwrapApiResponse(response);
  },

  getCompany: async (id: string): Promise<CompanyResponse> => {
    const response = await apiClient.get<ApiResponse<CompanyResponse>>(
      `${CAREER_BASE}/companies/${id}`,
    );
    return unwrapApiResponse(response);
  },

  createCompany: async (payload: CompanyRequest): Promise<CompanyResponse> => {
    const response = await apiClient.post<ApiResponse<CompanyResponse>>(
      `${CAREER_BASE}/companies`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  updateCompany: async (id: string, payload: CompanyRequest): Promise<CompanyResponse> => {
    const response = await apiClient.put<ApiResponse<CompanyResponse>>(
      `${CAREER_BASE}/companies/${id}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  deleteCompany: async (id: string): Promise<void> => {
    const response = await apiClient.delete<ApiResponse<null>>(`${CAREER_BASE}/companies/${id}`);
    assertApiSuccess(response);
  },

  listRecruiters: async (): Promise<RecruiterResponse[]> => {
    const response = await apiClient.get<ApiResponse<RecruiterResponse[]>>(
      `${CAREER_BASE}/recruiters`,
    );
    return unwrapApiResponse(response);
  },

  getRecruiter: async (id: string): Promise<RecruiterResponse> => {
    const response = await apiClient.get<ApiResponse<RecruiterResponse>>(
      `${CAREER_BASE}/recruiters/${id}`,
    );
    return unwrapApiResponse(response);
  },

  createRecruiter: async (payload: RecruiterRequest): Promise<RecruiterResponse> => {
    const response = await apiClient.post<ApiResponse<RecruiterResponse>>(
      `${CAREER_BASE}/recruiters`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  updateRecruiter: async (id: string, payload: RecruiterRequest): Promise<RecruiterResponse> => {
    const response = await apiClient.put<ApiResponse<RecruiterResponse>>(
      `${CAREER_BASE}/recruiters/${id}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  deleteRecruiter: async (id: string): Promise<void> => {
    const response = await apiClient.delete<ApiResponse<null>>(`${CAREER_BASE}/recruiters/${id}`);
    assertApiSuccess(response);
  },

  listApplications: async (
    params: PageParams & { archived?: boolean } = {},
  ): Promise<PageResponse<JobApplicationResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<JobApplicationResponse>>>(
      `${CAREER_BASE}/applications`,
      {
        params: {
          ...toPageQuery(params),
          ...(params.archived !== undefined ? { archived: params.archived } : {}),
        },
      },
    );
    return unwrapApiResponse(response);
  },

  listArchivedApplications: async (
    params: PageParams = {},
  ): Promise<PageResponse<JobApplicationResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<JobApplicationResponse>>>(
      `${CAREER_BASE}/applications/archived`,
      { params: toPageQuery(params) },
    );
    return unwrapApiResponse(response);
  },

  searchApplications: async (
    filters: ApplicationSearchFilters & PageParams = {},
  ): Promise<PageResponse<JobApplicationResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<JobApplicationResponse>>>(
      `${CAREER_BASE}/applications/search`,
      { params: buildSearchQuery(filters) },
    );
    return unwrapApiResponse(response);
  },

  getApplication: async (id: string): Promise<JobApplicationResponse> => {
    const response = await apiClient.get<ApiResponse<JobApplicationResponse>>(
      `${CAREER_BASE}/applications/${id}`,
    );
    return unwrapApiResponse(response);
  },

  createApplication: async (payload: JobApplicationRequest): Promise<JobApplicationResponse> => {
    const response = await apiClient.post<ApiResponse<JobApplicationResponse>>(
      `${CAREER_BASE}/applications`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  updateApplication: async (
    id: string,
    payload: JobApplicationRequest,
  ): Promise<JobApplicationResponse> => {
    const response = await apiClient.put<ApiResponse<JobApplicationResponse>>(
      `${CAREER_BASE}/applications/${id}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  archiveApplication: async (id: string): Promise<void> => {
    const response = await apiClient.delete<ApiResponse<null>>(`${CAREER_BASE}/applications/${id}`);
    assertApiSuccess(response);
  },

  updateApplicationStatus: async (
    id: string,
    payload: ApplicationStatusUpdateRequest,
  ): Promise<JobApplicationResponse> => {
    const response = await apiClient.patch<ApiResponse<JobApplicationResponse>>(
      `${CAREER_BASE}/applications/${id}/status`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  getApplicationHistory: async (id: string): Promise<ApplicationStatusHistoryResponse[]> => {
    const response = await apiClient.get<ApiResponse<ApplicationStatusHistoryResponse[]>>(
      `${CAREER_BASE}/applications/${id}/history`,
    );
    return unwrapApiResponse(response);
  },

  getApplicationTimeline: async (id: string): Promise<ApplicationTimelineResponse> => {
    const response = await apiClient.get<ApiResponse<ApplicationTimelineResponse>>(
      `${CAREER_BASE}/applications/${id}/timeline`,
    );
    return unwrapApiResponse(response);
  },

  listInterviews: async (applicationId: string): Promise<InterviewResponse[]> => {
    const response = await apiClient.get<ApiResponse<InterviewResponse[]>>(
      `${CAREER_BASE}/applications/${applicationId}/interviews`,
    );
    return unwrapApiResponse(response);
  },

  getInterview: async (applicationId: string, interviewId: string): Promise<InterviewResponse> => {
    const response = await apiClient.get<ApiResponse<InterviewResponse>>(
      `${CAREER_BASE}/applications/${applicationId}/interviews/${interviewId}`,
    );
    return unwrapApiResponse(response);
  },

  createInterview: async (
    applicationId: string,
    payload: InterviewRequest,
  ): Promise<InterviewResponse> => {
    const response = await apiClient.post<ApiResponse<InterviewResponse>>(
      `${CAREER_BASE}/applications/${applicationId}/interviews`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  updateInterview: async (
    applicationId: string,
    interviewId: string,
    payload: InterviewRequest,
  ): Promise<InterviewResponse> => {
    const response = await apiClient.put<ApiResponse<InterviewResponse>>(
      `${CAREER_BASE}/applications/${applicationId}/interviews/${interviewId}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  deleteInterview: async (applicationId: string, interviewId: string): Promise<void> => {
    const response = await apiClient.delete<ApiResponse<null>>(
      `${CAREER_BASE}/applications/${applicationId}/interviews/${interviewId}`,
    );
    assertApiSuccess(response);
  },

  listOffers: async (applicationId: string): Promise<OfferResponse[]> => {
    const response = await apiClient.get<ApiResponse<OfferResponse[]>>(
      `${CAREER_BASE}/applications/${applicationId}/offers`,
    );
    return unwrapApiResponse(response);
  },

  getOffer: async (applicationId: string, offerId: string): Promise<OfferResponse> => {
    const response = await apiClient.get<ApiResponse<OfferResponse>>(
      `${CAREER_BASE}/applications/${applicationId}/offers/${offerId}`,
    );
    return unwrapApiResponse(response);
  },

  createOffer: async (applicationId: string, payload: OfferRequest): Promise<OfferResponse> => {
    const response = await apiClient.post<ApiResponse<OfferResponse>>(
      `${CAREER_BASE}/applications/${applicationId}/offers`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  updateOffer: async (
    applicationId: string,
    offerId: string,
    payload: OfferRequest,
  ): Promise<OfferResponse> => {
    const response = await apiClient.put<ApiResponse<OfferResponse>>(
      `${CAREER_BASE}/applications/${applicationId}/offers/${offerId}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  deleteOffer: async (applicationId: string, offerId: string): Promise<void> => {
    const response = await apiClient.delete<ApiResponse<null>>(
      `${CAREER_BASE}/applications/${applicationId}/offers/${offerId}`,
    );
    assertApiSuccess(response);
  },
};
