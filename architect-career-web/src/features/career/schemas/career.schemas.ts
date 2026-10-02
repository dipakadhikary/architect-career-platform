import { z } from 'zod';
import { moduleConfig } from '@/app/config/module.config';

const { maxNotesLength, maxTitleLength } = moduleConfig.career;

export const companySchema = z.object({
  name: z.string().trim().min(1, 'Company name is required').max(maxTitleLength),
  website: z.string().trim().url('Enter a valid URL').optional().or(z.literal('')),
  industry: z.string().trim().max(100).optional().or(z.literal('')),
  location: z.string().trim().max(200).optional().or(z.literal('')),
  notes: z.string().trim().max(maxNotesLength).optional().or(z.literal('')),
});

export type CompanyFormValues = z.infer<typeof companySchema>;

export const recruiterSchema = z.object({
  companyId: z.string().optional().or(z.literal('')),
  fullName: z.string().trim().min(1, 'Full name is required').max(200),
  email: z.string().trim().email('Enter a valid email').optional().or(z.literal('')),
  phone: z.string().trim().max(30).optional().or(z.literal('')),
  linkedInUrl: z.string().trim().url('Enter a valid URL').optional().or(z.literal('')),
  lastContactDate: z.string().optional().or(z.literal('')),
  nextFollowUpDate: z.string().optional().or(z.literal('')),
  status: z.enum(['ACTIVE', 'INACTIVE', 'DO_NOT_CONTACT']),
  notes: z.string().trim().max(maxNotesLength).optional().or(z.literal('')),
});

export type RecruiterFormValues = z.infer<typeof recruiterSchema>;

export const applicationSchema = z.object({
  companyId: z.string().min(1, 'Company is required'),
  recruiterId: z.string().optional().or(z.literal('')),
  title: z.string().trim().min(1, 'Job title is required').max(maxTitleLength),
  jobDescription: z
    .string()
    .trim()
    .max(moduleConfig.career.maxJobDescriptionLength)
    .optional()
    .or(z.literal('')),
  source: z.string().trim().max(100).optional().or(z.literal('')),
  salaryExpectation: z.coerce.number().min(0).optional().or(z.literal('')),
  currency: z
    .string()
    .trim()
    .length(moduleConfig.career.currencyLength, 'Currency must be 3 characters')
    .optional()
    .or(z.literal('')),
  resumeVersion: z.string().trim().max(100).optional().or(z.literal('')),
  appliedOn: z.string().min(1, 'Applied date is required'),
  location: z.string().trim().max(200).optional().or(z.literal('')),
  jobUrl: z.string().trim().url('Enter a valid URL').optional().or(z.literal('')),
  notes: z.string().trim().max(maxNotesLength).optional().or(z.literal('')),
});

export type ApplicationFormValues = z.infer<typeof applicationSchema>;

export const statusTransitionSchema = z.object({
  newStatus: z.enum([
    'DRAFT',
    'APPLIED',
    'SCREENING',
    'TECHNICAL_INTERVIEW',
    'MANAGER_INTERVIEW',
    'HR_INTERVIEW',
    'OFFER',
    'ACCEPTED',
    'DECLINED',
    'REJECTED',
    'WITHDRAWN',
  ]),
  comments: z.string().trim().max(maxNotesLength).optional().or(z.literal('')),
});

export type StatusTransitionFormValues = z.infer<typeof statusTransitionSchema>;

export const interviewSchema = z.object({
  interviewRound: z.enum(['SCREENING', 'TECHNICAL', 'MANAGER', 'HR', 'FINAL', 'OTHER']),
  interviewer: z.string().trim().max(200).optional().or(z.literal('')),
  interviewDate: z.string().min(1, 'Interview date is required'),
  durationMinutes: z.coerce.number().min(1).max(480).optional().or(z.literal('')),
  status: z.enum(['SCHEDULED', 'COMPLETED', 'CANCELLED', 'NO_SHOW', 'RESCHEDULED']),
  rating: z.coerce.number().min(1).max(5).optional().or(z.literal('')),
  feedback: z.string().trim().max(maxNotesLength).optional().or(z.literal('')),
  questionsAsked: z.string().trim().max(maxNotesLength).optional().or(z.literal('')),
  strengths: z.string().trim().max(maxNotesLength).optional().or(z.literal('')),
  weaknesses: z.string().trim().max(maxNotesLength).optional().or(z.literal('')),
  improvementAreas: z.string().trim().max(maxNotesLength).optional().or(z.literal('')),
  candidateNotes: z.string().trim().max(maxNotesLength).optional().or(z.literal('')),
  confidenceRating: z.coerce.number().min(1).max(5).optional().or(z.literal('')),
  interviewReminderDate: z.string().optional().or(z.literal('')),
  locationOrLink: z.string().trim().max(500).optional().or(z.literal('')),
  notes: z.string().trim().max(maxNotesLength).optional().or(z.literal('')),
});

export type InterviewFormValues = z.infer<typeof interviewSchema>;

export const offerSchema = z.object({
  baseSalary: z.coerce.number().min(0, 'Base salary is required'),
  currency: z
    .string()
    .trim()
    .length(moduleConfig.career.currencyLength, 'Currency must be 3 characters'),
  joiningBonus: z.coerce.number().min(0).optional().or(z.literal('')),
  annualBonus: z.coerce.number().min(0).optional().or(z.literal('')),
  stockOptions: z.string().trim().max(500).optional().or(z.literal('')),
  location: z.string().trim().max(200).optional().or(z.literal('')),
  workMode: z.enum(['ONSITE', 'REMOTE', 'HYBRID']).optional().or(z.literal('')),
  joiningDate: z.string().optional().or(z.literal('')),
  noticePeriodDays: z.coerce.number().min(0).max(365).optional().or(z.literal('')),
  offerStatus: z.enum(['PENDING', 'ACCEPTED', 'DECLINED', 'EXPIRED', 'WITHDRAWN']),
  offerExpiryDate: z.string().optional().or(z.literal('')),
  notes: z.string().trim().max(maxNotesLength).optional().or(z.literal('')),
});

export type OfferFormValues = z.infer<typeof offerSchema>;
