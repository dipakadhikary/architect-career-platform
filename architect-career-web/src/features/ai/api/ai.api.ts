import { appConfig } from '@/app/config/app.config';
import { moduleConfig } from '@/app/config/module.config';
import { apiClient } from '@/shared/api/axios.instance';
import { unwrapApiResponse } from '@/shared/api/unwrap';
import type { ApiResponse } from '@/shared/api/types';
import type {
  AgentExecution,
  AssistantAskRequest,
  AssistantAskResponse,
  AuthoringProposal,
  AuthoringProposalRequest,
  AssistantConversation,
  AssistantConversationDetail,
  AssistantConversationPage,
  AssistantMessagePair,
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

  async listConversations(page = 0, size = 20): Promise<AssistantConversationPage> {
    const response = await apiClient.get<ApiResponse<AssistantConversationPage>>(
      `${BASE}/conversations`,
      { timeout, params: { page, size } },
    );
    return unwrapApiResponse(response);
  },

  async createConversation(): Promise<AssistantConversation> {
    const response = await apiClient.post<ApiResponse<AssistantConversation>>(
      `${BASE}/conversations`,
      {},
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async getConversation(conversationId: string): Promise<AssistantConversationDetail> {
    const response = await apiClient.get<ApiResponse<AssistantConversationDetail>>(
      `${BASE}/conversations/${conversationId}`,
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async renameConversation(conversationId: string, title: string): Promise<AssistantConversation> {
    const response = await apiClient.patch<ApiResponse<AssistantConversation>>(
      `${BASE}/conversations/${conversationId}`,
      { title },
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async deleteConversation(conversationId: string): Promise<void> {
    await apiClient.delete(`${BASE}/conversations/${conversationId}`, { timeout });
  },

  async sendConversationMessage(
    conversationId: string,
    content: string,
    idempotencyKey: string,
  ): Promise<AssistantMessagePair> {
    const response = await apiClient.post<ApiResponse<AssistantMessagePair>>(
      `${BASE}/conversations/${conversationId}/messages`,
      { content },
      { timeout, headers: { 'Idempotency-Key': idempotencyKey } },
    );
    return unwrapApiResponse(response);
  },

  async generateProposal(payload: AuthoringProposalRequest): Promise<AuthoringProposal> {
    const response = await apiClient.post<ApiResponse<AuthoringProposal>>(
      `${BASE}/authoring/proposals`,
      payload,
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async editProposal(proposalId: string, content: string): Promise<AuthoringProposal> {
    const response = await apiClient.patch<ApiResponse<AuthoringProposal>>(
      `${BASE}/authoring/proposals/${proposalId}`,
      { content },
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async regenerateProposal(proposalId: string, instructions?: string): Promise<AuthoringProposal> {
    const response = await apiClient.post<ApiResponse<AuthoringProposal>>(
      `${BASE}/authoring/proposals/${proposalId}/regenerate`,
      { instructions },
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async acceptProposal(proposalId: string): Promise<AuthoringProposal> {
    const response = await apiClient.post<ApiResponse<AuthoringProposal>>(
      `${BASE}/authoring/proposals/${proposalId}/accept`,
      {},
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async rejectProposal(proposalId: string): Promise<AuthoringProposal> {
    const response = await apiClient.post<ApiResponse<AuthoringProposal>>(
      `${BASE}/authoring/proposals/${proposalId}/reject`,
      {},
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async executeAgent(goal: string, conversationId?: string): Promise<AgentExecution> {
    const response = await apiClient.post<ApiResponse<AgentExecution>>(
      `${BASE}/agents/execute`,
      { goal, conversationId: conversationId ?? null },
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async decideAgent(executionId: string, decision: 'APPROVE' | 'REJECT'): Promise<AgentExecution> {
    const response = await apiClient.post<ApiResponse<AgentExecution>>(
      `${BASE}/agents/executions/${executionId}/decision`,
      { decision },
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async cancelAgent(executionId: string): Promise<AgentExecution> {
    const response = await apiClient.post<ApiResponse<AgentExecution>>(
      `${BASE}/agents/executions/${executionId}/cancel`,
      {},
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async ask(payload: AssistantAskRequest): Promise<AssistantAskResponse> {
    const response = await apiClient.post<ApiResponse<AssistantAskResponse>>(
      `${BASE}/chat`,
      payload,
      { timeout },
    );
    return unwrapApiResponse(response);
  },

  async chatCompletion(payload: ChatCompletionRequest): Promise<ChatCompletionResponse> {
    const history = (payload.history ?? []).filter(
      (turn) => turn.content.trim().length > 0 && (turn.role === 'user' || turn.role === 'assistant'),
    );
    const last = history[history.length - 1];
    const messages =
      last?.role === 'user' && last.content === payload.message
        ? history
        : [...history, { role: 'user' as const, content: payload.message }];
    const answer = await this.ask({ messages });
    return {
      conversationId: payload.conversationId ?? '',
      message: answer.answer,
      model: answer.model,
    };
  },
};
