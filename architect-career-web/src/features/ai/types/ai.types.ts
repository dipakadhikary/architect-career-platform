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
  grounded?: boolean;
  sources?: AssistantSource[];
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

export interface AssistantSource {
  contentId: string;
  topicId?: string | null;
  title: string;
  contentType: string;
  section: string;
  path: string;
  url: string;
  chunkId: string;
  score: number;
}

export interface AssistantAskResponse {
  answer: string;
  model: string;
  provider: string;
  grounded?: boolean;
  sources?: AssistantSource[];
}

export interface AssistantConversation {
  id: string;
  title: string;
  createdAt: string;
  updatedAt: string;
}

export interface AssistantConversationPage {
  content: AssistantConversation[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface AssistantConversationMessage {
  id: string;
  role: string;
  content: string;
  sequenceNumber: number;
  status: string;
  createdAt: string;
  model: string;
  provider: string;
  grounded: boolean;
  sources: AssistantSource[];
}

export interface AssistantConversationDetail extends AssistantConversation {
  messages: AssistantConversationMessage[];
  truncated: boolean;
}

export interface AssistantMessagePair {
  userMessage: AssistantConversationMessage;
  assistantMessage: AssistantConversationMessage;
}

export type AuthoringOperation =
  | 'GENERATE'
  | 'IMPROVE'
  | 'REWRITE'
  | 'SUMMARIZE'
  | 'EXPAND'
  | 'GENERATE_QA'
  | 'GENERATE_EXAMPLES'
  | 'GENERATE_EXPLANATION'
  | 'GENERATE_OBJECTIVES'
  | 'GENERATE_PREREQUISITES'
  | 'SUGGEST_STRUCTURE'
  | 'GENERATE_CODE';

export interface AuthoringQuestion {
  question: string;
  answer: string;
  difficulty: string;
  explanation?: string;
}

export interface AuthoringProposal {
  proposalId: string;
  operation: AuthoringOperation;
  status: 'GENERATED' | 'EDITING' | 'APPROVED' | 'REJECTED';
  content: string;
  questions: AuthoringQuestion[];
  sources: AssistantSource[];
  warnings: string[];
  model: string;
  provider: string;
  promptVersion: string;
  grounded: boolean;
  authoritative: false;
  contentId?: string | null;
  sourceVersion?: number | null;
}

export interface AuthoringProposalRequest {
  operation: AuthoringOperation;
  topic?: string;
  instructions?: string;
  contentId?: string;
  conversationId?: string;
  useConversation?: boolean;
  useKnowledge?: boolean;
  questionCount?: number;
  difficulty?: string;
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

export interface AgentStep {
  stepId: string;
  tool: string;
  label: string;
  status: string;
}

export interface AgentExecution {
  executionId: string;
  status: string;
  answer: string;
  errorCode: string;
  sources: AssistantSource[];
  steps: AgentStep[];
  approvalRequired: boolean;
  proposedAction: string;
}
