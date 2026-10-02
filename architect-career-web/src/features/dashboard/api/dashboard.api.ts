import { apiClient } from '@/shared/api/axios.instance';
import { unwrapApiResponse } from '@/shared/api/unwrap';
import type { ApiResponse } from '@/shared/api/types';

export interface DashboardMetrics {
  welcomeMessage: string;
  profileCompletion: number;
  activeLearningPlans: number;
  completedCourses: number;
  portfolioProjects: number;
  jobApplications: number;
  upcomingInterviews: number;
}

const DASHBOARD_BASE = '/api/v1/dashboard';

export const dashboardApi = {
  async getDashboard(): Promise<DashboardMetrics> {
    const response = await apiClient.get<ApiResponse<DashboardMetrics>>(DASHBOARD_BASE);
    return unwrapApiResponse(response);
  },
};
