export type TutorialTreeNode = {
  id: string;
  title: string;
  slug: string;
  path: string;
  sortOrder: number;
  hasConcept: boolean;
  hasQuestions: boolean;
  children: TutorialTreeNode[];
};

export type TutorialBreadcrumbItem = {
  id: string;
  title: string;
  slug: string;
  path: string;
};

export type TutorialTopicRequest = {
  title: string;
  slug?: string | null;
  parentId?: string | null;
  sortOrder?: number | null;
};

export type TutorialTopicResponse = {
  id: string;
  parentId: string | null;
  title: string;
  slug: string;
  path: string;
  sortOrder: number;
  hasConcept: boolean;
  hasQuestions: boolean;
  childCount: number;
  breadcrumb: TutorialBreadcrumbItem[];
  createdAt: string;
  updatedAt: string;
};

export type TutorialConceptResponse = {
  topicId: string;
  title: string;
  path: string;
  breadcrumb: TutorialBreadcrumbItem[];
  content: string;
  updatedAt: string;
};

export type TutorialQuestionResponse = {
  id: string;
  topicId: string;
  question: string;
  answer: string;
  sortOrder: number;
  updatedAt: string;
};

export type TutorialQuestionsPageResponse = {
  title: string;
  path: string;
  breadcrumb: TutorialBreadcrumbItem[];
  questions: TutorialQuestionResponse[];
};

export type TutorialSearchResult = {
  topicId: string;
  title: string;
  path: string;
  breadcrumb: TutorialBreadcrumbItem[];
  snippet: string;
  contentType: string;
  rank: number;
};

export type TutorialSearchPageResponse = {
  query: string;
  content: TutorialSearchResult[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};
