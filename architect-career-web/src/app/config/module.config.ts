/**
 * Domain limits aligned with backend acos.* configuration defaults.
 * Prefer this over scattering magic numbers in feature code.
 */
export const moduleConfig = {
  knowledge: {
    maxTitleLength: 200,
    maxSummaryLength: 500,
    maxContentLength: 100_000,
    maxCategoryLength: 100,
    maxTagLength: 50,
    maxTags: 20,
    defaultPageSize: 20,
  },
  learning: {
    maxTitleLength: 200,
    maxDescriptionLength: 2000,
    maxMilestonesPerPlan: 50,
    maxTopicsPerMilestone: 100,
    defaultPageSize: 20,
  },
  portfolio: {
    maxTitleLength: 200,
    maxSummaryLength: 500,
    maxDescriptionLength: 50_000,
    maxUrlLength: 500,
    maxTechnologiesPerProject: 30,
    defaultPageSize: 20,
  },
  career: {
    maxNotesLength: 2000,
    maxJobDescriptionLength: 10_000,
    maxTitleLength: 200,
    defaultPageSize: 20,
    currencyLength: 3,
  },
  query: {
    refetchIntervalMs: 60_000,
  },
  ai: {
    healthRefetchIntervalMs: 30_000,
    chatMaxMessages: 200,
    chatStorageKey: 'acos.ai.chat.session',
    defaultSearchLimit: 10,
    defaultQuizQuestions: 5,
    maxPromptLength: 8_000,
    maxTranscriptLength: 50_000,
    requestTimeoutMs: 60_000,
  },
} as const;
