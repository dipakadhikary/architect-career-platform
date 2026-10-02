import type { AxiosInstance, AxiosResponse, RawAxiosRequestConfig } from 'axios';
import {
  LearningMilestonesApi,
  LearningPlansApi,
  LearningTopicsApi,
  type LearningMilestonesApiCreate6Request,
  type LearningMilestonesApiDelete6Request,
  type LearningMilestonesApiGet6Request,
  type LearningMilestonesApiList6Request,
  type LearningMilestonesApiUpdate6Request,
  type LearningPlansApiCreate5Request,
  type LearningPlansApiDelete5Request,
  type LearningPlansApiGet5Request,
  type LearningPlansApiList5Request,
  type LearningPlansApiUpdate5Request,
  type LearningTopicsApiCreate7Request,
  type LearningTopicsApiDelete7Request,
  type LearningTopicsApiList7Request,
  type LearningTopicsApiUpdate7Request,
  type LearningTopicsApiUpdateStatusRequest,
} from '../generated/api';
import { ServiceSupport, type ServiceSupportOptions } from './ServiceSupport';

/**
 * Domain wrapper for Learning Plans, Milestones, and Topics APIs.
 */
export class LearningApiService extends ServiceSupport {
  readonly plans: LearningPlansApi;
  readonly milestones: LearningMilestonesApi;
  readonly topics: LearningTopicsApi;

  constructor(options: ServiceSupportOptions) {
    super(options);
    const axios = options.axios as AxiosInstance;
    this.plans = new LearningPlansApi(options.configuration, undefined, axios);
    this.milestones = new LearningMilestonesApi(options.configuration, undefined, axios);
    this.topics = new LearningTopicsApi(options.configuration, undefined, axios);
  }

  createPlan(
    request: LearningPlansApiCreate5Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.createPlan', () => this.plans.create5(request, options));
  }

  updatePlan(
    request: LearningPlansApiUpdate5Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.updatePlan', () => this.plans.update5(request, options));
  }

  deletePlan(
    request: LearningPlansApiDelete5Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.deletePlan', () => this.plans.delete5(request, options));
  }

  getPlan(
    request: LearningPlansApiGet5Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.getPlan', () => this.plans.get5(request, options));
  }

  listPlans(
    request: LearningPlansApiList5Request = {},
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.listPlans', () => this.plans.list5(request, options));
  }

  createMilestone(
    request: LearningMilestonesApiCreate6Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.createMilestone', () =>
      this.milestones.create6(request, options),
    );
  }

  updateMilestone(
    request: LearningMilestonesApiUpdate6Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.updateMilestone', () =>
      this.milestones.update6(request, options),
    );
  }

  deleteMilestone(
    request: LearningMilestonesApiDelete6Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.deleteMilestone', () =>
      this.milestones.delete6(request, options),
    );
  }

  getMilestone(
    request: LearningMilestonesApiGet6Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.getMilestone', () => this.milestones.get6(request, options));
  }

  listMilestones(
    request: LearningMilestonesApiList6Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.listMilestones', () => this.milestones.list6(request, options));
  }

  createTopic(
    request: LearningTopicsApiCreate7Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.createTopic', () => this.topics.create7(request, options));
  }

  updateTopic(
    request: LearningTopicsApiUpdate7Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.updateTopic', () => this.topics.update7(request, options));
  }

  deleteTopic(
    request: LearningTopicsApiDelete7Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.deleteTopic', () => this.topics.delete7(request, options));
  }

  listTopics(
    request: LearningTopicsApiList7Request,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.listTopics', () => this.topics.list7(request, options));
  }

  updateTopicStatus(
    request: LearningTopicsApiUpdateStatusRequest,
    options?: RawAxiosRequestConfig,
  ): Promise<AxiosResponse> {
    return this.execute('learning.updateTopicStatus', () =>
      this.topics.updateStatus(request, options),
    );
  }
}
