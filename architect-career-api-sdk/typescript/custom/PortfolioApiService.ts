import type { AxiosInstance, AxiosResponse, RawAxiosRequestConfig } from 'axios';
import {
  PortfolioAchievementsApi,
  PortfolioCertificationsApi,
  PortfolioProjectsApi,
  PortfolioSkillsApi,
  PortfolioTechnologiesApi,
  type PortfolioAchievementsApiCreate4Request,
  type PortfolioAchievementsApiDelete4Request,
  type PortfolioAchievementsApiGet4Request,
  type PortfolioAchievementsApiUpdate4Request,
  type PortfolioCertificationsApiCreate3Request,
  type PortfolioCertificationsApiDelete3Request,
  type PortfolioCertificationsApiGet3Request,
  type PortfolioCertificationsApiUpdate3Request,
  type PortfolioProjectsApiCreate2Request,
  type PortfolioProjectsApiDelete2Request,
  type PortfolioProjectsApiGet2Request,
  type PortfolioProjectsApiList2Request,
  type PortfolioProjectsApiSearchRequest,
  type PortfolioProjectsApiUpdate2Request,
  type PortfolioSkillsApiCreate1Request,
  type PortfolioSkillsApiDelete1Request,
  type PortfolioSkillsApiGet1Request,
  type PortfolioSkillsApiUpdate1Request,
  type PortfolioTechnologiesApiCreateRequest,
  type PortfolioTechnologiesApiDeleteRequest,
  type PortfolioTechnologiesApiGetRequest,
  type PortfolioTechnologiesApiUpdateRequest,
} from '../generated/api';
import { ServiceSupport, type ServiceSupportOptions } from './ServiceSupport';

/**
 * Domain wrapper for Portfolio Projects, Skills, Technologies, Certifications, and Achievements.
 */
export class PortfolioApiService extends ServiceSupport {
  readonly projects: PortfolioProjectsApi;
  readonly skills: PortfolioSkillsApi;
  readonly technologies: PortfolioTechnologiesApi;
  readonly certifications: PortfolioCertificationsApi;
  readonly achievements: PortfolioAchievementsApi;

  constructor(options: ServiceSupportOptions) {
    super(options);
    const axios = options.axios as AxiosInstance;
    this.projects = new PortfolioProjectsApi(options.configuration, undefined, axios);
    this.skills = new PortfolioSkillsApi(options.configuration, undefined, axios);
    this.technologies = new PortfolioTechnologiesApi(options.configuration, undefined, axios);
    this.certifications = new PortfolioCertificationsApi(options.configuration, undefined, axios);
    this.achievements = new PortfolioAchievementsApi(options.configuration, undefined, axios);
  }

  createProject(
    request: PortfolioProjectsApiCreate2Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.createProject', () => this.projects.create2(request, options));
  }

  updateProject(
    request: PortfolioProjectsApiUpdate2Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.updateProject', () => this.projects.update2(request, options));
  }

  deleteProject(
    request: PortfolioProjectsApiDelete2Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.deleteProject', () => this.projects.delete2(request, options));
  }

  getProject(
    request: PortfolioProjectsApiGet2Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.getProject', () => this.projects.get2(request, options));
  }

  listProjects(
    request: PortfolioProjectsApiList2Request = {},
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.listProjects', () => this.projects.list2(request, options));
  }

  searchProjects(
    request: PortfolioProjectsApiSearchRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.searchProjects', () => this.projects.search(request, options));
  }

  createSkill(
    request: PortfolioSkillsApiCreate1Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.createSkill', () => this.skills.create1(request, options));
  }

  updateSkill(
    request: PortfolioSkillsApiUpdate1Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.updateSkill', () => this.skills.update1(request, options));
  }

  deleteSkill(
    request: PortfolioSkillsApiDelete1Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.deleteSkill', () => this.skills.delete1(request, options));
  }

  getSkill(
    request: PortfolioSkillsApiGet1Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.getSkill', () => this.skills.get1(request, options));
  }

  listSkills(options?: RawAxiosRequestConfig): Promise<AxiosResponse> {
    return this.execute('portfolio.listSkills', () => this.skills.list1(options));
  }

  createTechnology(
    request: PortfolioTechnologiesApiCreateRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.createTechnology', () =>
      this.technologies.create(request, options),
    );
  }

  updateTechnology(
    request: PortfolioTechnologiesApiUpdateRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.updateTechnology', () =>
      this.technologies.update(request, options),
    );
  }

  deleteTechnology(
    request: PortfolioTechnologiesApiDeleteRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.deleteTechnology', () =>
      this.technologies._delete(request, options),
    );
  }

  getTechnology(
    request: PortfolioTechnologiesApiGetRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.getTechnology', () => this.technologies.get(request, options));
  }

  listTechnologies(options?: RawAxiosRequestConfig): Promise<AxiosResponse> {
    return this.execute('portfolio.listTechnologies', () => this.technologies.list(options));
  }

  createCertification(
    request: PortfolioCertificationsApiCreate3Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.createCertification', () =>
      this.certifications.create3(request, options),
    );
  }

  updateCertification(
    request: PortfolioCertificationsApiUpdate3Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.updateCertification', () =>
      this.certifications.update3(request, options),
    );
  }

  deleteCertification(
    request: PortfolioCertificationsApiDelete3Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.deleteCertification', () =>
      this.certifications.delete3(request, options),
    );
  }

  getCertification(
    request: PortfolioCertificationsApiGet3Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.getCertification', () =>
      this.certifications.get3(request, options),
    );
  }

  listCertifications(options?: RawAxiosRequestConfig): Promise<AxiosResponse> {
    return this.execute('portfolio.listCertifications', () => this.certifications.list3(options));
  }

  createAchievement(
    request: PortfolioAchievementsApiCreate4Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.createAchievement', () =>
      this.achievements.create4(request, options),
    );
  }

  updateAchievement(
    request: PortfolioAchievementsApiUpdate4Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.updateAchievement', () =>
      this.achievements.update4(request, options),
    );
  }

  deleteAchievement(
    request: PortfolioAchievementsApiDelete4Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.deleteAchievement', () =>
      this.achievements.delete4(request, options),
    );
  }

  getAchievement(
    request: PortfolioAchievementsApiGet4Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('portfolio.getAchievement', () => this.achievements.get4(request, options));
  }

  listAchievements(options?: RawAxiosRequestConfig): Promise<AxiosResponse> {
    return this.execute('portfolio.listAchievements', () => this.achievements.list4(options));
  }
}
