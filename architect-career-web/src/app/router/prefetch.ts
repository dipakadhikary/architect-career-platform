/** Prefetch helpers for sidebar hover — warm route chunks. */
export const routePrefetchers: Record<string, () => Promise<unknown>> = {
  '/': () => import('@/features/dashboard'),
  '/learning': () => import('@/pages/learning/LearningPlansPage'),
  '/career': () => import('@/features/career'),
  '/portfolio': () => import('@/pages/portfolio'),
  '/knowledge': () => import('@/pages/knowledge/KnowledgeListPage'),
  '/tutorials': () => import('@/pages/tutorials'),
  '/ai': () => import('@/features/ai'),
};
