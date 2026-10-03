/** AI Platform health — mirrors com.acos.integration.health.AiPlatformHealthResponse */
export type AiPlatformHealthStatus = 'AVAILABLE' | 'UNAVAILABLE' | 'DEGRADED';

export interface AiPlatformHealth {
  status: AiPlatformHealthStatus;
  message: string;
  checkedAt: string;
  enabled: boolean;
}

export type AiDomain = 'knowledge' | 'learning' | 'career' | 'portfolio' | 'chat';

export type AiCapabilityId =
  | 'knowledge.search'
  | 'knowledge.summarize'
  | 'knowledge.related'
  | 'knowledge.insights'
  | 'learning.quiz'
  | 'learning.recommend'
  | 'learning.next-topic'
  | 'learning.progress'
  | 'learning.weak-topics'
  | 'career.resume'
  | 'career.interview-analysis'
  | 'career.mock-interview'
  | 'career.cover-letter'
  | 'career.recommendation'
  | 'portfolio.review'
  | 'portfolio.skill-gap'
  | 'portfolio.technology'
  | 'portfolio.project-summary'
  | 'portfolio.profile-review'
  | 'chat.assistant';

export type AiCapabilityLifecycle = 'live' | 'planned' | 'coming_soon';

export interface AiCapabilityDefinition {
  id: AiCapabilityId;
  domain: AiDomain;
  title: string;
  description: string;
  lifecycle: AiCapabilityLifecycle;
  /** Relative path under /api/v1/integration/ai */
  path: string;
}

export type ChatRole = 'user' | 'assistant' | 'system';

export type ChatMessageStatus = 'pending' | 'streaming' | 'complete' | 'error';

export interface ChatMessage {
  id: string;
  role: ChatRole;
  content: string;
  createdAt: string;
  status: ChatMessageStatus;
  /** Reserved for future token streaming chunks */
  streaming?: boolean;
}

export interface ChatSession {
  id: string;
  title: string;
  messages: ChatMessage[];
  updatedAt: string;
}

/** Knowledge AI contracts (aligned with Feign DTOs, consumed via Java Integration Layer) */
export interface KnowledgeSearchRequest {
  query: string;
  limit?: number;
}

export interface KnowledgeSearchHit {
  noteId: string;
  title: string;
  snippet: string;
  score: number;
}

export interface KnowledgeSearchResponse {
  hits: KnowledgeSearchHit[];
}

export interface KnowledgeSummarizeRequest {
  noteId: string;
  content: string;
  maxLength?: number;
}

export interface KnowledgeSummarizeResponse {
  noteId: string;
  summary: string;
  keyPoints: string[];
}

export interface LearningQuizRequest {
  topic: string;
  difficulty: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';
  questionCount: number;
}

export interface QuizQuestion {
  prompt: string;
  choices: string[];
  correctAnswer: string;
  explanation: string;
}

export interface LearningQuizResponse {
  quizId: string;
  topic: string;
  questions: QuizQuestion[];
}

export interface RecommendNextTopicRequest {
  planId: string;
  completedTopics: string[];
  goals: string[];
}

export interface RecommendNextTopicResponse {
  topic: string;
  rationale: string;
  relatedTopics: string[];
}

export interface EvaluateProgressRequest {
  planId: string;
  completedTopics: string[];
  quizScores: number[];
}

export interface EvaluateProgressResponse {
  progressPercent: number;
  summary: string;
  strengths: string[];
  focusAreas: string[];
}

export interface ResumeGenerateRequest {
  targetRole: string;
  experienceHighlights: string[];
  skills: string[];
}

export interface ResumeGenerateResponse {
  content: string;
  format: string;
}

export interface InterviewAnalysisRequest {
  interviewId?: string;
  transcript: string;
  jobDescription: string;
}

export interface InterviewAnalysisResponse {
  summary: string;
  strengths: string[];
  improvements: string[];
  score: number;
}

export interface CoverLetterRequest {
  targetRole: string;
  companyName: string;
  highlights: string[];
}

export interface CoverLetterResponse {
  content: string;
  format: string;
}

export interface PortfolioReviewRequest {
  projectIds: string[];
  targetRole: string;
}

export interface PortfolioReviewResponse {
  summary: string;
  strengths: string[];
  improvements: string[];
  score: number;
}

export interface SkillGapRequest {
  targetRole: string;
  currentSkills: string[];
  projectTechnologies: string[];
}

export interface SkillGapResponse {
  summary: string;
  missingSkills: string[];
  recommendedActions: string[];
}

export interface AssistantAskRequest {
  messages: Array<{ role: ChatRole; content: string }>;
}

export interface AssistantAskResponse {
  answer: string;
  model: string;
  provider: string;
}

export interface ChatCompletionRequest {
  message: string;
  conversationId?: string;
  history?: Array<{ role: ChatRole; content: string }>;
}

export interface ChatCompletionResponse {
  conversationId: string;
  message: string;
  model?: string;
}

export const AI_ERROR_CODES = [
  'AI_PLATFORM_UNAVAILABLE',
  'AI_PLATFORM_ERROR',
  'AI_TIMEOUT',
  'AI_AUTHENTICATION_FAILED',
  'AI_VALIDATION_FAILED',
  'AI_RATE_LIMITED',
] as const;

export type AiErrorCode = (typeof AI_ERROR_CODES)[number];
