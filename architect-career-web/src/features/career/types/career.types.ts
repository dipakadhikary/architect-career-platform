export type RecruiterStatus = 'ACTIVE' | 'INACTIVE' | 'DO_NOT_CONTACT';

export type ApplicationStatus =
  | 'DRAFT'
  | 'APPLIED'
  | 'SCREENING'
  | 'TECHNICAL_INTERVIEW'
  | 'MANAGER_INTERVIEW'
  | 'HR_INTERVIEW'
  | 'OFFER'
  | 'ACCEPTED'
  | 'DECLINED'
  | 'REJECTED'
  | 'WITHDRAWN';

export type InterviewRound = 'SCREENING' | 'TECHNICAL' | 'MANAGER' | 'HR' | 'FINAL' | 'OTHER';

export type InterviewStatus = 'SCHEDULED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW' | 'RESCHEDULED';

export type OfferStatus = 'PENDING' | 'ACCEPTED' | 'DECLINED' | 'EXPIRED' | 'WITHDRAWN';

export type WorkMode = 'ONSITE' | 'REMOTE' | 'HYBRID';

export interface StatusCount {
  status: ApplicationStatus;
  count: number;
}

export interface CareerDashboardResponse {
  totalCompanies: number;
  totalRecruiters: number;
  totalActiveApplications: number;
  applicationsByStatus: StatusCount[];
  interviewsScheduled: number;
  upcomingInterviews: number;
  offersReceived: number;
  pendingOffers: number;
  acceptedOffers: number;
  rejectedApplications: number;
  acceptanceRatio: number;
  averageInterviewRating: number | null;
}

export interface CompanyRequest {
  name: string;
  website?: string;
  industry?: string;
  location?: string;
  notes?: string;
}

export interface CompanyResponse extends CompanyRequest {
  id: string;
  archived: boolean;
  archivedAt: string | null;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface RecruiterRequest {
  companyId?: string;
  fullName: string;
  email?: string;
  phone?: string;
  linkedInUrl?: string;
  lastContactDate?: string;
  nextFollowUpDate?: string;
  status?: RecruiterStatus;
  notes?: string;
}

export interface RecruiterResponse {
  id: string;
  companyId: string | null;
  fullName: string;
  email: string | null;
  phone: string | null;
  linkedInUrl: string | null;
  lastContactDate: string | null;
  nextFollowUpDate: string | null;
  status: RecruiterStatus;
  notes: string | null;
  archived: boolean;
  archivedAt: string | null;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface CompanySummary {
  id: string;
  name: string;
}

export interface RecruiterSummary {
  id: string;
  fullName: string;
}

export interface JobApplicationRequest {
  companyId: string;
  recruiterId?: string;
  title: string;
  jobDescription?: string;
  source?: string;
  salaryExpectation?: number;
  currency?: string;
  resumeVersion?: string;
  appliedOn: string;
  location?: string;
  jobUrl?: string;
  notes?: string;
}

export interface JobApplicationResponse extends JobApplicationRequest {
  id: string;
  company: CompanySummary;
  recruiter: RecruiterSummary | null;
  status: ApplicationStatus;
  archived: boolean;
  archivedAt: string | null;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface ApplicationStatusUpdateRequest {
  newStatus: ApplicationStatus;
  comments?: string;
}

export interface ApplicationStatusHistoryResponse {
  id: string;
  previousStatus: ApplicationStatus | null;
  newStatus: ApplicationStatus;
  comments: string | null;
  changedAt: string;
  changedBy: string | null;
}

export interface ApplicationTimelineStep {
  status: ApplicationStatus;
  label: string;
  reached: boolean;
  current: boolean;
  changedAt: string | null;
}

export interface ApplicationTimelineResponse {
  steps: ApplicationTimelineStep[];
}

export interface InterviewRequest {
  interviewRound: InterviewRound;
  interviewer?: string;
  interviewDate: string;
  durationMinutes?: number;
  status: InterviewStatus;
  rating?: number;
  feedback?: string;
  questionsAsked?: string;
  strengths?: string;
  weaknesses?: string;
  improvementAreas?: string;
  candidateNotes?: string;
  confidenceRating?: number;
  interviewReminderDate?: string;
  locationOrLink?: string;
  notes?: string;
}

export interface InterviewResponse extends InterviewRequest {
  id: string;
  applicationId: string;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface OfferRequest {
  baseSalary: number;
  currency: string;
  joiningBonus?: number;
  annualBonus?: number;
  stockOptions?: string;
  location?: string;
  workMode?: WorkMode;
  joiningDate?: string;
  noticePeriodDays?: number;
  offerStatus: OfferStatus;
  offerExpiryDate?: string;
  notes?: string;
}

export interface OfferResponse extends OfferRequest {
  id: string;
  applicationId: string;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface ApplicationSearchFilters {
  companyId?: string;
  recruiterId?: string;
  status?: ApplicationStatus;
  interviewRound?: InterviewRound;
  appliedFrom?: string;
  appliedTo?: string;
  salaryMin?: number;
  salaryMax?: number;
  keyword?: string;
}
