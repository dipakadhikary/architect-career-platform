import { apiClient } from '@/shared/api/axios.instance';
import { assertApiSuccess, unwrapApiResponse } from '@/shared/api/unwrap';
import { toPageQuery } from '@/shared/api/pageParams';
import type { ApiResponse } from '@/shared/api/types';
import type {
  Achievement,
  AchievementRequest,
  Certification,
  CertificationRequest,
  PortfolioProject,
  PortfolioProjectPage,
  PortfolioProjectRequest,
  ProjectListParams,
  ProjectSearchParams,
  Skill,
  SkillRequest,
  Technology,
  TechnologyRequest,
} from '../types/portfolio.types';

const BASE = '/api/v1/portfolio';

export const projectsApi = {
  async list(params: ProjectListParams = {}): Promise<PortfolioProjectPage> {
    const response = await apiClient.get<ApiResponse<PortfolioProjectPage>>(`${BASE}/projects`, {
      params: toPageQuery(params),
    });
    return unwrapApiResponse(response);
  },

  async search(params: ProjectSearchParams): Promise<PortfolioProjectPage> {
    const { q, ...pageParams } = params;
    const response = await apiClient.get<ApiResponse<PortfolioProjectPage>>(
      `${BASE}/projects/search`,
      { params: { q, ...toPageQuery(pageParams) } },
    );
    return unwrapApiResponse(response);
  },

  async get(projectId: string): Promise<PortfolioProject> {
    const response = await apiClient.get<ApiResponse<PortfolioProject>>(
      `${BASE}/projects/${projectId}`,
    );
    return unwrapApiResponse(response);
  },

  async create(payload: PortfolioProjectRequest): Promise<PortfolioProject> {
    const response = await apiClient.post<ApiResponse<PortfolioProject>>(
      `${BASE}/projects`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async update(projectId: string, payload: PortfolioProjectRequest): Promise<PortfolioProject> {
    const response = await apiClient.put<ApiResponse<PortfolioProject>>(
      `${BASE}/projects/${projectId}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async remove(projectId: string): Promise<void> {
    const response = await apiClient.delete<ApiResponse<null>>(`${BASE}/projects/${projectId}`);
    assertApiSuccess(response);
  },
};

export const technologiesApi = {
  async list(): Promise<Technology[]> {
    const response = await apiClient.get<ApiResponse<Technology[]>>(`${BASE}/technologies`);
    return unwrapApiResponse(response);
  },

  async get(technologyId: string): Promise<Technology> {
    const response = await apiClient.get<ApiResponse<Technology>>(
      `${BASE}/technologies/${technologyId}`,
    );
    return unwrapApiResponse(response);
  },

  async create(payload: TechnologyRequest): Promise<Technology> {
    const response = await apiClient.post<ApiResponse<Technology>>(`${BASE}/technologies`, payload);
    return unwrapApiResponse(response);
  },

  async update(technologyId: string, payload: TechnologyRequest): Promise<Technology> {
    const response = await apiClient.put<ApiResponse<Technology>>(
      `${BASE}/technologies/${technologyId}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async remove(technologyId: string): Promise<void> {
    const response = await apiClient.delete<ApiResponse<null>>(
      `${BASE}/technologies/${technologyId}`,
    );
    assertApiSuccess(response);
  },
};

export const skillsApi = {
  async list(): Promise<Skill[]> {
    const response = await apiClient.get<ApiResponse<Skill[]>>(`${BASE}/skills`);
    return unwrapApiResponse(response);
  },

  async get(skillId: string): Promise<Skill> {
    const response = await apiClient.get<ApiResponse<Skill>>(`${BASE}/skills/${skillId}`);
    return unwrapApiResponse(response);
  },

  async create(payload: SkillRequest): Promise<Skill> {
    const response = await apiClient.post<ApiResponse<Skill>>(`${BASE}/skills`, payload);
    return unwrapApiResponse(response);
  },

  async update(skillId: string, payload: SkillRequest): Promise<Skill> {
    const response = await apiClient.put<ApiResponse<Skill>>(`${BASE}/skills/${skillId}`, payload);
    return unwrapApiResponse(response);
  },

  async remove(skillId: string): Promise<void> {
    const response = await apiClient.delete<ApiResponse<null>>(`${BASE}/skills/${skillId}`);
    assertApiSuccess(response);
  },
};

export const certificationsApi = {
  async list(): Promise<Certification[]> {
    const response = await apiClient.get<ApiResponse<Certification[]>>(`${BASE}/certifications`);
    return unwrapApiResponse(response);
  },

  async get(certificationId: string): Promise<Certification> {
    const response = await apiClient.get<ApiResponse<Certification>>(
      `${BASE}/certifications/${certificationId}`,
    );
    return unwrapApiResponse(response);
  },

  async create(payload: CertificationRequest): Promise<Certification> {
    const response = await apiClient.post<ApiResponse<Certification>>(
      `${BASE}/certifications`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async update(certificationId: string, payload: CertificationRequest): Promise<Certification> {
    const response = await apiClient.put<ApiResponse<Certification>>(
      `${BASE}/certifications/${certificationId}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async remove(certificationId: string): Promise<void> {
    const response = await apiClient.delete<ApiResponse<null>>(
      `${BASE}/certifications/${certificationId}`,
    );
    assertApiSuccess(response);
  },
};

export const achievementsApi = {
  async list(): Promise<Achievement[]> {
    const response = await apiClient.get<ApiResponse<Achievement[]>>(`${BASE}/achievements`);
    return unwrapApiResponse(response);
  },

  async get(achievementId: string): Promise<Achievement> {
    const response = await apiClient.get<ApiResponse<Achievement>>(
      `${BASE}/achievements/${achievementId}`,
    );
    return unwrapApiResponse(response);
  },

  async create(payload: AchievementRequest): Promise<Achievement> {
    const response = await apiClient.post<ApiResponse<Achievement>>(
      `${BASE}/achievements`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async update(achievementId: string, payload: AchievementRequest): Promise<Achievement> {
    const response = await apiClient.put<ApiResponse<Achievement>>(
      `${BASE}/achievements/${achievementId}`,
      payload,
    );
    return unwrapApiResponse(response);
  },

  async remove(achievementId: string): Promise<void> {
    const response = await apiClient.delete<ApiResponse<null>>(
      `${BASE}/achievements/${achievementId}`,
    );
    assertApiSuccess(response);
  },
};

export const portfolioApi = {
  projects: projectsApi,
  technologies: technologiesApi,
  skills: skillsApi,
  certifications: certificationsApi,
  achievements: achievementsApi,
};
