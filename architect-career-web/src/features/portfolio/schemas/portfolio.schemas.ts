import { z } from 'zod';
import { moduleConfig } from '@/app/config/module.config';

const { portfolio: limits } = moduleConfig;

export const projectStatusSchema = z.enum(['DRAFT', 'PUBLISHED', 'ARCHIVED']);

export const proficiencyLevelSchema = z.enum(['BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT']);

export const projectFormSchema = z
  .object({
    title: z
      .string()
      .trim()
      .min(1, 'Title is required')
      .max(limits.maxTitleLength, `Title must be at most ${limits.maxTitleLength} characters`),
    summary: z
      .string()
      .trim()
      .min(1, 'Summary is required')
      .max(
        limits.maxSummaryLength,
        `Summary must be at most ${limits.maxSummaryLength} characters`,
      ),
    description: z
      .string()
      .trim()
      .min(1, 'Description is required')
      .max(
        limits.maxDescriptionLength,
        `Description must be at most ${limits.maxDescriptionLength} characters`,
      ),
    repositoryUrl: z
      .string()
      .trim()
      .max(limits.maxUrlLength)
      .url('Enter a valid URL')
      .optional()
      .or(z.literal('')),
    liveUrl: z
      .string()
      .trim()
      .max(limits.maxUrlLength)
      .url('Enter a valid URL')
      .optional()
      .or(z.literal('')),
    status: projectStatusSchema,
    startDate: z.string().optional().or(z.literal('')),
    endDate: z.string().optional().or(z.literal('')),
    technologyNames: z
      .array(z.string().trim().min(1).max(100))
      .max(
        limits.maxTechnologiesPerProject,
        `At most ${limits.maxTechnologiesPerProject} technologies allowed`,
      ),
  })
  .refine(
    (data) => {
      if (!data.startDate || !data.endDate) return true;
      return data.endDate >= data.startDate;
    },
    { message: 'End date must be on or after start date', path: ['endDate'] },
  );

export const technologyFormSchema = z.object({
  name: z
    .string()
    .trim()
    .min(1, 'Name is required')
    .max(100, 'Name must be at most 100 characters'),
  category: z
    .string()
    .trim()
    .max(100, 'Category must be at most 100 characters')
    .optional()
    .or(z.literal('')),
});

export const skillFormSchema = z.object({
  name: z
    .string()
    .trim()
    .min(1, 'Name is required')
    .max(100, 'Name must be at most 100 characters'),
  proficiencyLevel: proficiencyLevelSchema,
  yearsOfExperience: z.coerce
    .number()
    .min(0, 'Years must be zero or greater')
    .max(99)
    .optional()
    .or(z.literal('')),
  description: z
    .string()
    .trim()
    .max(1000, 'Description must be at most 1000 characters')
    .optional()
    .or(z.literal('')),
});

export const certificationFormSchema = z
  .object({
    name: z
      .string()
      .trim()
      .min(1, 'Name is required')
      .max(200, 'Name must be at most 200 characters'),
    issuer: z
      .string()
      .trim()
      .min(1, 'Issuer is required')
      .max(200, 'Issuer must be at most 200 characters'),
    credentialId: z.string().trim().max(200).optional().or(z.literal('')),
    credentialUrl: z
      .string()
      .trim()
      .max(limits.maxUrlLength)
      .url('Enter a valid URL')
      .optional()
      .or(z.literal('')),
    issuedOn: z.string().min(1, 'Issue date is required'),
    expiresOn: z.string().optional().or(z.literal('')),
  })
  .refine(
    (data) => {
      if (!data.expiresOn) return true;
      return data.expiresOn >= data.issuedOn;
    },
    { message: 'Expiry date must be on or after issue date', path: ['expiresOn'] },
  );

export const achievementFormSchema = z.object({
  title: z
    .string()
    .trim()
    .min(1, 'Title is required')
    .max(limits.maxTitleLength, `Title must be at most ${limits.maxTitleLength} characters`),
  description: z
    .string()
    .trim()
    .min(1, 'Description is required')
    .max(2000, 'Description must be at most 2000 characters'),
  achievedOn: z.string().min(1, 'Date achieved is required'),
  organization: z.string().trim().max(200).optional().or(z.literal('')),
});

export type ProjectFormValues = z.infer<typeof projectFormSchema>;
export type TechnologyFormValues = z.infer<typeof technologyFormSchema>;
export type SkillFormValues = z.infer<typeof skillFormSchema>;
export type CertificationFormValues = z.infer<typeof certificationFormSchema>;
export type AchievementFormValues = z.infer<typeof achievementFormSchema>;

export function toProjectRequest(values: ProjectFormValues) {
  return {
    title: values.title,
    summary: values.summary,
    description: values.description,
    status: values.status,
    repositoryUrl: values.repositoryUrl || null,
    liveUrl: values.liveUrl || null,
    startDate: values.startDate || null,
    endDate: values.endDate || null,
    technologyNames: values.technologyNames.length > 0 ? values.technologyNames : null,
  };
}

export function toTechnologyRequest(values: TechnologyFormValues) {
  return {
    name: values.name,
    category: values.category || null,
  };
}

export function toSkillRequest(values: SkillFormValues) {
  return {
    name: values.name,
    proficiencyLevel: values.proficiencyLevel,
    yearsOfExperience:
      values.yearsOfExperience === '' || values.yearsOfExperience === undefined
        ? null
        : values.yearsOfExperience,
    description: values.description || null,
  };
}

export function toCertificationRequest(values: CertificationFormValues) {
  return {
    name: values.name,
    issuer: values.issuer,
    credentialId: values.credentialId || null,
    credentialUrl: values.credentialUrl || null,
    issuedOn: values.issuedOn,
    expiresOn: values.expiresOn || null,
  };
}

export function toAchievementRequest(values: AchievementFormValues) {
  return {
    title: values.title,
    description: values.description,
    achievedOn: values.achievedOn,
    organization: values.organization || null,
  };
}
