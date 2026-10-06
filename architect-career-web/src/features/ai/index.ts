export { AiShell } from '@/features/ai/layouts/AiShell';
export {
  AgentAiPage,
  AskAiPage,
  AiDashboardPage,
  AiChatPage,
  KnowledgeAiPage,
  LearningAiPage,
  CareerAiPage,
  PortfolioAiPage,
} from '@/features/ai/pages';
export * from '@/features/ai/components';
export { useAiHealth, useAiAvailability, aiQueryKeys } from '@/features/ai/hooks/useAiHealth';
export { useAiChat } from '@/features/ai/hooks/useAiChat';
export { useAskAi } from '@/features/ai/hooks/useAskAi';
export {
  useKnowledgeSearchMutation,
  useKnowledgeSummarizeMutation,
  useGenerateQuizMutation,
  useRecommendNextTopicMutation,
  useEvaluateProgressMutation,
  useGenerateResumeMutation,
  useAnalyzeInterviewMutation,
  useGenerateCoverLetterMutation,
  usePortfolioReviewMutation,
  useSkillGapMutation,
  useChatCompletionMutation,
} from '@/features/ai/hooks/useAiMutations';
export type {
  AiCapabilityDefinition,
  AiCapabilityId,
  AiCapabilityLifecycle,
  AiDomain,
  AiPlatformHealth,
  AiPlatformHealthStatus,
  ChatMessage as AiChatMessage,
  ChatSession,
} from '@/features/ai/types/ai.types';
export {
  AI_CAPABILITIES,
  getCapabilitiesByDomain,
  getCapability,
} from '@/features/ai/services/aiCapabilities';
