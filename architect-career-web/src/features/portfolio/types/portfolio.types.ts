import type { PageParams, PageResponse } from '@/shared/types/pagination';

export type ProjectStatus = 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';

export type ProficiencyLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED' | 'EXPERT';

export interface PortfolioProject {
  id: string;
  title: string;
  summary: string;
  description: string;
  repositoryUrl: string | null;
  liveUrl: string | null;
  status: ProjectStatus;
  startDate: string | null;
  endDate: string | null;
  technologies: string[];
  createdAt: string;
  updatedAt: string;
}

export interface PortfolioProjectRequest {
  title: string;
  summary: string;
  description: string;
  repositoryUrl?: string | null;
  liveUrl?: string | null;
  status: ProjectStatus;
  startDate?: string | null;
  endDate?: string | null;
  technologyNames?: string[] | null;
}

export type PortfolioProjectPage = PageResponse<PortfolioProject>;

export interface ProjectListParams extends PageParams {
  sort?: string;
}

export interface ProjectSearchParams extends PageParams {
  q: string;
  sort?: string;
}

export interface Technology {
  id: string;
  name: string;
  category: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface TechnologyRequest {
  name: string;
  category?: string | null;
}

export interface Skill {
  id: string;
  name: string;
  proficiencyLevel: ProficiencyLevel;
  yearsOfExperience: number | null;
  description: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface SkillRequest {
  name: string;
  proficiencyLevel: ProficiencyLevel;
  yearsOfExperience?: number | null;
  description?: string | null;
}

export interface Certification {
  id: string;
  name: string;
  issuer: string;
  credentialId: string | null;
  credentialUrl: string | null;
  issuedOn: string;
  expiresOn: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CertificationRequest {
  name: string;
  issuer: string;
  credentialId?: string | null;
  credentialUrl?: string | null;
  issuedOn: string;
  expiresOn?: string | null;
}

export interface Achievement {
  id: string;
  title: string;
  description: string;
  achievedOn: string;
  organization: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface AchievementRequest {
  title: string;
  description: string;
  achievedOn: string;
  organization?: string | null;
}

export type ExperienceEventType = 'project' | 'certification' | 'achievement';

export interface ExperienceTimelineEvent {
  id: string;
  type: ExperienceEventType;
  title: string;
  description?: string;
  date: string;
  meta?: string;
}

export type PortfolioTab =
  'projects' | 'skills' | 'technologies' | 'certifications' | 'achievements' | 'experience';
