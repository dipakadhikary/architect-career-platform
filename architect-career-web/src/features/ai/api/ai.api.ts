import { appConfig } from '@/app/config/app.config';
import { moduleConfig } from '@/app/config/module.config';
import { apiClient } from '@/shared/api/axios.instance';
import { unwrapApiResponse } from '@/shared/api/unwrap';
import type { ApiResponse } from '@/shared/api/types';
import type {
  ChatCompletionRequest,
  ChatCompletionResponse,
  CoverLetterRequest,
  CoverLetterResponse,
  EvaluateProgressRequest,
  EvaluateProgressResponse,
  InterviewAnalysisRequest,
  InterviewAnalysisResponse,
  KnowledgeSearchRequest,
  KnowledgeSearchResponse,
  KnowledgeSummarizeRequest,
  KnowledgeSummarizeResponse,
  LearningQuizRequest,
  LearningQuizResponse,
  PortfolioReviewRequest,
  PortfolioReviewResponse,
  RecommendNextTopicRequest,
  RecommendNextTopicResponse,
  ResumeGenerateRequest,
  ResumeGenerateResponse,
  SkillGapRequest,
  SkillGapResponse,
  AiPlatformHealth,
} from '../types/ai.types';

const BASE = appConfig.ai.integrationBasePath;
const timeout = moduleConfig.ai.requestTimeoutMs;

/**
 * Java AI Integration Layer client.
 * Never calls the Python AI Platform directly.
 * Capability POSTs align with Feign contracts and will activate when BFF controllers ship.
 */
export const aiApi = {
  async getHealth(): Promise<AiPlatformHealth> {
    const response = await apiClient.get<ApiResponse<AiPlatformHealth>>(`${BASE}/health`, {
      timeout,
    });
    const data = unwrapApiResponse(response);
    return {
      ...data,
      // Attach client-measured latency via message enrichment is avoided;
      // callers may time the request separately.
    };
  },

  async timedHealth(): Promise<{ health: AiPlatformHealth; latencyMs: number }> {
    const started = performance.now();
    const health = await aiApi.getHealth();
    return { health, latencyMs: Math.round(performance.now() - started) };
  },

  async searchKnowledge(payload: KnowledgeSearchRequest): Promise<KnowledgeSearchResponse> {
    const response = await apiClient.post<ApiResponse<KnowledgeSearchResponse>>(
      `${BASE}/knowledge/search`,
      payload,
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async summarizeKnowledge(
    payload: KnowledgeSummarizeRequest,
  ): Promise<KnowledgeSummarizeResponse> {
    const response = await apiClient.post<ApiResponse<KnowledgeSummarizeResponse>>(
      `${BASE}/knowledge/summarize`,
      payload,
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async generateQuiz(payload: LearningQuizRequest): Promise<LearningQuizResponse> {
    const response = await apiClient.post<ApiResponse<LearningQuizResponse>>(
      `${BASE}/learning/quiz/generate`,
      payload,
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async recommendNextTopic(
    payload: RecommendNextTopicRequest,
  ): Promise<RecommendNextTopicResponse> {
    const response = await apiClient.post<ApiResponse<RecommendNextTopicResponse>>(
      `${BASE}/learning/topics/recommend-next`,
      payload,
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async evaluateProgress(payload: EvaluateProgressRequest): Promise<EvaluateProgressResponse> {
    const response = await apiClient.post<ApiResponse<EvaluateProgressResponse>>(
      `${BASE}/learning/progress/evaluate`,
      payload,
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async generateResume(payload: ResumeGenerateRequest): Promise<ResumeGenerateResponse> {
    const response = await apiClient.post<ApiResponse<ResumeGenerateResponse>>(
      `${BASE}/career/resume/generate`,
      payload,
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async analyzeInterview(payload: InterviewAnalysisRequest): Promise<InterviewAnalysisResponse> {
    const response = await apiClient.post<ApiResponse<InterviewAnalysisResponse>>(
      `${BASE}/career/interview/analyze`,
      payload,
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async generateCoverLetter(payload: CoverLetterRequest): Promise<CoverLetterResponse> {
    const response = await apiClient.post<ApiResponse<CoverLetterResponse>>(
      `${BASE}/career/cover-letter/generate`,
      payload,
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async reviewPortfolio(payload: PortfolioReviewRequest): Promise<PortfolioReviewResponse> {
    const response = await apiClient.post<ApiResponse<PortfolioReviewResponse>>(
      `${BASE}/portfolio/review`,
      payload,
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async analyzeSkillGap(payload: SkillGapRequest): Promise<SkillGapResponse> {
    const response = await apiClient.post<ApiResponse<SkillGapResponse>>(
      `${BASE}/portfolio/skill-gap/analyze`,
      payload,
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async chatCompletion(payload: ChatCompletionRequest): Promise<ChatCompletionResponse> {
    const response = await apiClient.post<ApiResponse<ChatCompletionResponse>>(
      `${BASE}/chat/completions`,
      payload,
      { timeout },
    );
    return unwrapApiResponse(response);
  },
};
