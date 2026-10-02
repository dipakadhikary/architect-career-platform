import type { AxiosInstance, AxiosResponse, RawAxiosRequestConfig } from 'axios';
import {
  CareerApplicationsApi,
  CareerCompaniesApi,
  CareerDashboardApi,
  CareerInterviewsApi,
  CareerOffersApi,
  CareerRecruitersApi,
  type CareerApplicationsApiArchiveRequest,
  type CareerApplicationsApiCreate11Request,
  type CareerApplicationsApiGet10Request,
  type CareerApplicationsApiGetHistoryRequest,
  type CareerApplicationsApiGetTimelineRequest,
  type CareerApplicationsApiList11Request,
  type CareerApplicationsApiListArchivedRequest,
  type CareerApplicationsApiSearch2Request,
  type CareerApplicationsApiTransitionStatusRequest,
  type CareerApplicationsApiUpdate11Request,
} from '../generated/api';
import { ServiceSupport, type ServiceSupportOptions } from './ServiceSupport';

/**
 * Domain wrapper for Career Applications and related Career APIs.
 */
export class CareerApiService extends ServiceSupport {
  readonly applications: CareerApplicationsApi;
  readonly companies: CareerCompaniesApi;
  readonly recruiters: CareerRecruitersApi;
  readonly interviews: CareerInterviewsApi;
  readonly offers: CareerOffersApi;
  readonly dashboard: CareerDashboardApi;

  constructor(options: ServiceSupportOptions) {
    super(options);
    const axios = options.axios as AxiosInstance;
    this.applications = new CareerApplicationsApi(options.configuration, undefined, axios);
    this.companies = new CareerCompaniesApi(options.configuration, undefined, axios);
    this.recruiters = new CareerRecruitersApi(options.configuration, undefined, axios);
    this.interviews = new CareerInterviewsApi(options.configuration, undefined, axios);
    this.offers = new CareerOffersApi(options.configuration, undefined, axios);
    this.dashboard = new CareerDashboardApi(options.configuration, undefined, axios);
  }

  createApplication(
    request: CareerApplicationsApiCreate11Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('career.createApplication', () =>
      this.applications.create11(request, options),
    );
  }

  updateApplication(
    request: CareerApplicationsApiUpdate11Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('career.updateApplication', () =>
      this.applications.update11(request, options),
    );
  }

  getApplication(
    request: CareerApplicationsApiGet10Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('career.getApplication', () => this.applications.get10(request, options));
  }

  listApplications(
    request: CareerApplicationsApiList11Request = {},
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('career.listApplications', () =>
      this.applications.list11(request, options),
    );
  }

  listArchivedApplications(
    request: CareerApplicationsApiListArchivedRequest = {},
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('career.listArchivedApplications', () =>
      this.applications.listArchived(request, options),
    );
  }

  searchApplications(
    request: CareerApplicationsApiSearch2Request = {},
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('career.searchApplications', () =>
      this.applications.search2(request, options),
    );
  }

  archiveApplication(
    request: CareerApplicationsApiArchiveRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('career.archiveApplication', () =>
      this.applications.archive(request, options),
    );
  }

  transitionApplicationStatus(
    request: CareerApplicationsApiTransitionStatusRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('career.transitionApplicationStatus', () =>
      this.applications.transitionStatus(request, options),
    );
  }

  getApplicationTimeline(
    request: CareerApplicationsApiGetTimelineRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('career.getApplicationTimeline', () =>
      this.applications.getTimeline(request, options),
    );
  }

  getApplicationHistory(
    request: CareerApplicationsApiGetHistoryRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('career.getApplicationHistory', () =>
      this.applications.getHistory(request, options),
    );
  }

  getCareerDashboard(options?: RawAxiosRequestConfig): Promise<AxiosResponse> {
    return this.execute('career.getCareerDashboard', () => this.dashboard.getSummary(options));
  }
}
