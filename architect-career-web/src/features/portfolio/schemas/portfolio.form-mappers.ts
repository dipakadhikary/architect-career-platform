import type {
  AchievementFormValues,
  CertificationFormValues,
  ProjectFormValues,
  SkillFormValues,
  TechnologyFormValues,
} from '../schemas/portfolio.schemas';

export function toProjectFormValues(
  project?: {
    title: string;
    summary: string;
    description: string;
    repositoryUrl: string | null;
    liveUrl: string | null;
    status: ProjectFormValues['status'];
    startDate: string | null;
    endDate: string | null;
    technologies: string[];
  } | null,
): ProjectFormValues {
  if (!project) {
    return {
      title: '',
      summary: '',
      description: '',
      repositoryUrl: '',
      liveUrl: '',
      status: 'DRAFT',
      startDate: '',
      endDate: '',
      technologyNames: [],
    };
  }

  return {
    title: project.title,
    summary: project.summary,
    description: project.description,
    repositoryUrl: project.repositoryUrl ?? '',
    liveUrl: project.liveUrl ?? '',
    status: project.status,
    startDate: project.startDate ?? '',
    endDate: project.endDate ?? '',
    technologyNames: project.technologies,
  };
}

export function toTechnologyFormValues(
  technology?: {
    name: string;
    category: string | null;
  } | null,
): TechnologyFormValues {
  if (!technology) {
    return { name: '', category: '' };
  }
  return {
    name: technology.name,
    category: technology.category ?? '',
  };
}

export function toSkillFormValues(
  skill?: {
    name: string;
    proficiencyLevel: SkillFormValues['proficiencyLevel'];
    yearsOfExperience: number | null;
    description: string | null;
  } | null,
): SkillFormValues {
  if (!skill) {
    return {
      name: '',
      proficiencyLevel: 'INTERMEDIATE',
      yearsOfExperience: '',
      description: '',
    };
  }

  return {
    name: skill.name,
    proficiencyLevel: skill.proficiencyLevel,
    yearsOfExperience: skill.yearsOfExperience ?? '',
    description: skill.description ?? '',
  };
}

export function toCertificationFormValues(
  certification?: {
    name: string;
    issuer: string;
    credentialId: string | null;
    credentialUrl: string | null;
    issuedOn: string;
    expiresOn: string | null;
  } | null,
): CertificationFormValues {
  if (!certification) {
    return {
      name: '',
      issuer: '',
      credentialId: '',
      credentialUrl: '',
      issuedOn: '',
      expiresOn: '',
    };
  }

  return {
    name: certification.name,
    issuer: certification.issuer,
    credentialId: certification.credentialId ?? '',
    credentialUrl: certification.credentialUrl ?? '',
    issuedOn: certification.issuedOn,
    expiresOn: certification.expiresOn ?? '',
  };
}

export function toAchievementFormValues(
  achievement?: {
    title: string;
    description: string;
    achievedOn: string;
    organization: string | null;
  } | null,
): AchievementFormValues {
  if (!achievement) {
    return {
      title: '',
      description: '',
      achievedOn: '',
      organization: '',
    };
  }

  return {
    title: achievement.title,
    description: achievement.description,
    achievedOn: achievement.achievedOn,
    organization: achievement.organization ?? '',
  };
}
