import type { AiCapabilityDefinition } from '../types/ai.types';

/**
 * Catalog of AI capabilities surfaced in the UX.
 * Paths target the Java AI Integration Layer BFF (not the Python platform).
 * Only health is live today; capability POSTs become available when controllers ship.
 */
export const AI_CAPABILITIES: readonly AiCapabilityDefinition[] = [
  {
    id: 'knowledge.search',
    domain: 'knowledge',
    title: 'Semantic Search',
    description: 'Find notes by meaning, not just keywords.',
    lifecycle: 'planned',
    path: '/knowledge/search',
  },
  {
    id: 'knowledge.summarize',
    domain: 'knowledge',
    title: 'Document Summarization',
    description: 'Generate concise summaries and key points from note content.',
    lifecycle: 'planned',
    path: '/knowledge/summarize',
  },
  {
    id: 'knowledge.related',
    domain: 'knowledge',
    title: 'Related Notes',
    description: 'Discover related knowledge based on semantic similarity.',
    lifecycle: 'coming_soon',
    path: '/knowledge/related',
  },
  {
    id: 'knowledge.insights',
    domain: 'knowledge',
    title: 'Knowledge Insights',
    description: 'Surface themes and gaps across your knowledge base.',
    lifecycle: 'coming_soon',
    path: '/knowledge/insights',
  },
  {
    id: 'learning.quiz',
    domain: 'learning',
    title: 'Generate Quiz',
    description: 'Create practice quizzes for a learning topic.',
    lifecycle: 'planned',
    path: '/learning/quiz/generate',
  },
  {
    id: 'learning.recommend',
    domain: 'learning',
    title: 'Learning Recommendation',
    description: 'Get personalized learning recommendations.',
    lifecycle: 'coming_soon',
    path: '/learning/recommend',
  },
  {
    id: 'learning.next-topic',
    domain: 'learning',
    title: 'Next Topic Recommendation',
    description: 'Suggest the best next topic in your learning plan.',
    lifecycle: 'planned',
    path: '/learning/topics/recommend-next',
  },
  {
    id: 'learning.progress',
    domain: 'learning',
    title: 'Progress Evaluation',
    description: 'Evaluate learning progress with strengths and focus areas.',
    lifecycle: 'planned',
    path: '/learning/progress/evaluate',
  },
  {
    id: 'learning.weak-topics',
    domain: 'learning',
    title: 'Weak Topic Detection',
    description: 'Identify topics that need more practice.',
    lifecycle: 'coming_soon',
    path: '/learning/weak-topics',
  },
  {
    id: 'career.resume',
    domain: 'career',
    title: 'Resume Generator',
    description: 'Draft a role-targeted resume from your highlights and skills.',
    lifecycle: 'planned',
    path: '/career/resume/generate',
  },
  {
    id: 'career.interview-analysis',
    domain: 'career',
    title: 'Interview Analysis',
    description: 'Analyze interview transcripts against a job description.',
    lifecycle: 'planned',
    path: '/career/interview/analyze',
  },
  {
    id: 'career.mock-interview',
    domain: 'career',
    title: 'Mock Interview',
    description: 'Practice interviews with guided prompts and feedback.',
    lifecycle: 'coming_soon',
    path: '/career/interview/mock',
  },
  {
    id: 'career.cover-letter',
    domain: 'career',
    title: 'Cover Letter Generator',
    description: 'Generate a tailored cover letter for an application.',
    lifecycle: 'planned',
    path: '/career/cover-letter/generate',
  },
  {
    id: 'career.recommendation',
    domain: 'career',
    title: 'Career Recommendation',
    description: 'Receive career path and next-step recommendations.',
    lifecycle: 'coming_soon',
    path: '/career/recommend',
  },
  {
    id: 'portfolio.review',
    domain: 'portfolio',
    title: 'Portfolio Review',
    description: 'Review projects for a target role with scored feedback.',
    lifecycle: 'planned',
    path: '/portfolio/review',
  },
  {
    id: 'portfolio.skill-gap',
    domain: 'portfolio',
    title: 'Skill Gap Analysis',
    description: 'Compare current skills to a target role and close gaps.',
    lifecycle: 'planned',
    path: '/portfolio/skill-gap/analyze',
  },
  {
    id: 'portfolio.technology',
    domain: 'portfolio',
    title: 'Technology Recommendation',
    description: 'Recommend technologies to strengthen your portfolio.',
    lifecycle: 'coming_soon',
    path: '/portfolio/technology/recommend',
  },
  {
    id: 'portfolio.project-summary',
    domain: 'portfolio',
    title: 'Project Summary',
    description: 'Generate professional summaries for portfolio projects.',
    lifecycle: 'coming_soon',
    path: '/portfolio/project/summarize',
  },
  {
    id: 'portfolio.profile-review',
    domain: 'portfolio',
    title: 'Professional Profile Review',
    description: 'Review your overall professional profile narrative.',
    lifecycle: 'coming_soon',
    path: '/portfolio/profile/review',
  },
  {
    id: 'chat.assistant',
    domain: 'chat',
    title: 'AI Chat Assistant',
    description: 'Conversational assistance across ACOS domains.',
    lifecycle: 'planned',
    path: '/chat',
  },
] as const;

export function getCapabilitiesByDomain(domain: AiCapabilityDefinition['domain']) {
  return AI_CAPABILITIES.filter((capability) => capability.domain === domain);
}

export function getCapability(id: AiCapabilityDefinition['id']) {
  return AI_CAPABILITIES.find((capability) => capability.id === id);
}
