import { useMutation } from '@tanstack/react-query';
import { aiApi } from '../api/ai.api';
import { useAiAvailability } from './useAiHealth';
import { getAiUserMessage } from '../services/aiAvailability';
import type {
  ChatCompletionRequest,
  CoverLetterRequest,
  EvaluateProgressRequest,
  InterviewAnalysisRequest,
  KnowledgeSearchRequest,
  KnowledgeSummarizeRequest,
  LearningQuizRequest,
  PortfolioReviewRequest,
  RecommendNextTopicRequest,
  ResumeGenerateRequest,
  SkillGapRequest,
} from '../types/ai.types';

function useGuardedMutation<TPayload, TResult>(
  mutationFn: (payload: TPayload) => Promise<TResult>,
) {
  const { canInvoke, unavailableReason } = useAiAvailability();

  return useMutation({
    mutationFn: async (payload: TPayload) => {
      if (!canInvoke) {
        throw new Error(unavailableReason ?? 'AI Platform is currently unavailable.');
      }
      try {
        return await mutationFn(payload);
      } catch (error) {
        throw new Error(getAiUserMessage(error));
      }
    },
  });
}

export function useKnowledgeSearchMutation() {
  return useGuardedMutation((payload: KnowledgeSearchRequest) => aiApi.searchKnowledge(payload));
}

export function useKnowledgeSummarizeMutation() {
  return useGuardedMutation((payload: KnowledgeSummarizeRequest) =>
    aiApi.summarizeKnowledge(payload),
  );
}

export function useGenerateQuizMutation() {
  return useGuardedMutation((payload: LearningQuizRequest) => aiApi.generateQuiz(payload));
}

export function useRecommendNextTopicMutation() {
  return useGuardedMutation((payload: RecommendNextTopicRequest) =>
    aiApi.recommendNextTopic(payload),
  );
}

export function useEvaluateProgressMutation() {
  return useGuardedMutation((payload: EvaluateProgressRequest) => aiApi.evaluateProgress(payload));
}

export function useGenerateResumeMutation() {
  return useGuardedMutation((payload: ResumeGenerateRequest) => aiApi.generateResume(payload));
}

export function useAnalyzeInterviewMutation() {
  return useGuardedMutation((payload: InterviewAnalysisRequest) => aiApi.analyzeInterview(payload));
}

export function useGenerateCoverLetterMutation() {
  return useGuardedMutation((payload: CoverLetterRequest) => aiApi.generateCoverLetter(payload));
}

export function usePortfolioReviewMutation() {
  return useGuardedMutation((payload: PortfolioReviewRequest) => aiApi.reviewPortfolio(payload));
}

export function useSkillGapMutation() {
  return useGuardedMutation((payload: SkillGapRequest) => aiApi.analyzeSkillGap(payload));
}

export function useChatCompletionMutation() {
  return useGuardedMutation((payload: ChatCompletionRequest) => aiApi.chatCompletion(payload));
}
