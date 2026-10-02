import type { AxiosInstance } from 'axios';
import { createAcosConfiguration, type AcosClientOptions } from './configuration';
import { AuthApiService } from './AuthApiService';
import { AiIntegrationApiService } from './AiIntegrationApiService';
import { CareerApiService } from './CareerApiService';
import { DashboardApiService } from './DashboardApiService';
import { KnowledgeApiService } from './KnowledgeApiService';
import { LearningApiService } from './LearningApiService';
import { PortfolioApiService } from './PortfolioApiService';
import type { SdkLoggerOptions } from './logging';
import type { RetryOptions } from './retry';

export interface AcosApiClientOptions extends AcosClientOptions {
  retry?: RetryOptions;
  logger?: SdkLoggerOptions;
}

/**
 * Facade that wires all domain service wrappers from a single configuration.
 */
export class AcosApiClient {
  readonly auth: AuthApiService;
  readonly dashboard: DashboardApiService;
  readonly knowledge: KnowledgeApiService;
  readonly learning: LearningApiService;
  readonly career: CareerApiService;
  readonly portfolio: PortfolioApiService;
  readonly ai: AiIntegrationApiService;

  constructor(options: AcosApiClientOptions) {
    const configuration = createAcosConfiguration(options);
    const shared = {
      configuration,
      axios: options.axios as AxiosInstance | undefined,
      retry: options.retry,
      logger: options.logger,
    };

    this.auth = new AuthApiService(shared);
    this.dashboard = new DashboardApiService(shared);
    this.knowledge = new KnowledgeApiService(shared);
    this.learning = new LearningApiService(shared);
    this.career = new CareerApiService(shared);
    this.portfolio = new PortfolioApiService(shared);
    this.ai = new AiIntegrationApiService(shared);
  }
}
